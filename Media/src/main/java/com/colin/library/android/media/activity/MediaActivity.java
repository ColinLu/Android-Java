package com.colin.library.android.media.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.base.BaseActivity;
import com.colin.library.android.media.MediaHelper;
import com.colin.library.android.media.R;
import com.colin.library.android.media.adapter.MediaAdapter;
import com.colin.library.android.media.def.Action;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaFolder;
import com.colin.library.android.media.def.MediaResult;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.media.dialog.FolderDialog;
import com.colin.library.android.media.task.MediaLeaderTask;
import com.colin.library.android.media.task.OnTaskListener;
import com.colin.library.android.media.util.MediaUtil;
import com.colin.library.android.utils.ActivityUtil;
import com.colin.library.android.utils.IntentUtil;
import com.colin.library.android.utils.PermissionUtil;
import com.colin.library.android.utils.ResourceUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.utils.WidgetUtil;
import com.colin.library.android.widgets.recycler.SpaceItemDecoration;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 01:47
 * <p>
 * 描述： 多媒体主界面  显示相册，音频，视频
 */
public class MediaActivity extends BaseActivity {
    public static Action<List<MediaFile>> sResultMultiple;      //多选操作返回
    public static Action<MediaFile> sResultSingle;              //单选操作返回
    public static Action<String> sCancel;                       //取消提示语句
    public static Filter<Long> sFilterSize;                     //筛选条件 大小
    public static Filter<Long> sFilterDuration;                 //筛选条件 时长（视频）
    public static Filter<String> sFilterMimeType;               //筛选条件 多媒体类型类型
    @MediaType
    private int mMediaType = MediaType.UNKNOWN;                 //选择多媒体 图片、视频、图片+视频（相册） 默认相册
    private int mColumn = MediaHelper.getInstance().getMediaConfig().getColumn();   //列表展示列数
    private String mTitle;                                      //显示标题
    private int mFacing;                                        //前置 or 后置
    private int mLimitCount = 1;                                //多选    限制数量
    private int mLimitQuality = 1;                              //视频    限制质量 0 or 1
    private long mLimitSize = Long.MAX_VALUE;                   //视频    限制大小 1 - 0x7fffffffffffffffL
    private long mLimitDuration = Long.MAX_VALUE;               //视频    限制时间 1 - 0x7fffffffffffffffL
    private boolean mMultipleMode = true;                       //选择多样式的话是否支持多种格式
    private boolean mDisplayCamera = true;                      //是否显示相机item 默认显示
    private boolean mNeedCrop = true;                           //单选    是否剪切
    private boolean mDisplayInvalid = true;                     //是否筛选要求不符的显示 默认显示
    private boolean toSettingPermission = false;


    private List<MediaFolder> mMediaFolders;
    private ArrayList<MediaFile> mSelectedList;             //选中的文件
    private int mDisplayPosition = 0;

    private MediaLeaderTask mMediaLeaderTask;

    private AppBarLayout layout_title;
    private Toolbar toolbar;
    private RecyclerView recycler_list;
    private TextView text_media_folder;
    private GridLayoutManager mLayoutManager;
    private MediaAdapter mAdapter;
    private FolderDialog mFolderDialog;
    private PopupMenu mCameraPopupMenu;
    private MenuItem album_menu_preview;
    private MenuItem album_menu_finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) initExtras(savedInstanceState);
        else initExtras(getIntent().getExtras());
        super.onCreate(savedInstanceState);
        ActivityUtil.initLocale(this, MediaHelper.getInstance().getMediaConfig().getLocale());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toSettingPermission) {
            toSettingPermission = false;
            loadData(true);
        }
    }

    /*屏幕旋转 重新初始化*/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int position = mLayoutManager.findFirstVisibleItemPosition();
        mLayoutManager.setOrientation(WidgetUtil.getOrientation(newConfig));
        recycler_list.setAdapter(mAdapter);
        mLayoutManager.scrollToPosition(position);
        if (mFolderDialog != null && !mFolderDialog.isShowing()) mFolderDialog = null;
        refreshCount();
    }

    @Override
    public void onBackPressed() {
        if (mMediaLeaderTask != null) mMediaLeaderTask.cancel(true);
        mMediaLeaderTask = null;
        callbackCancel(R.string.media_cancel);
    }

    @Override
    public void finish() {
        sFilterSize = null;
        sFilterMimeType = null;
        sFilterDuration = null;
        sResultSingle = null;
        sResultMultiple = null;
        sCancel = null;
        if (mSelectedList != null) mSelectedList.clear();
        mSelectedList = null;
        mFolderDialog = null;
        mCameraPopupMenu = null;
        toSettingPermission = false;
        super.finish();
    }


    @Override
    public int layoutRes() {
        return R.layout.activity_media;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        if (bundle == null) {
            layout_title = findViewById(R.id.layout_title);
            toolbar = findViewById(R.id.toolbar);
            recycler_list = findViewById(R.id.recycler_list);
            text_media_folder = findViewById(R.id.text_media_folder);
        }
        //toolbar
        this.setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
        if (null == mAdapter) mAdapter = new MediaAdapter(this, mDisplayCamera, mMultipleMode);
        mLayoutManager = new GridLayoutManager(this, mColumn, WidgetUtil.getOrientation(this), false);
        recycler_list.setLayoutManager(mLayoutManager);
        recycler_list.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_4)));
        recycler_list.setItemAnimator(null);
        recycler_list.setHasFixedSize(true);
        recycler_list.setAdapter(mAdapter);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        text_media_folder.setOnClickListener(v -> dialog());
        mAdapter.setOnItemCheckedListener((view, position, object) -> {
            if (view.getId() == R.id.text_item_media_invalid) {
                ToastUtil.show(R.string.media_item_invalid);
                return;
            }
            if (view.getId() == R.id.image_album_camera) {
                chooseCamera(view);
                return;
            }
            if (view.getId() == R.id.image_item_media && object instanceof MediaFile) {
                chooseMedia(view, position, (MediaFile) object);
                return;
            }
        });
        mAdapter.setOnItemCheckedListener((view, position, object) -> selectedMedia(position, (MediaFile) object));
    }


    @Override
    public void initData(@Nullable Bundle bundle) {
        toolbar.setTitle(getMediaTitle());
        refreshCount();
    }

    /*线程 加载本地多媒体数据*/
    @Override
    public void loadData(boolean refresh) {
        if (!PermissionUtil.request(this, Constants.REQUEST_MEDIA, Constants.PERMISSION_MEDIA)) {
            return;
        }
        if (mMediaLeaderTask != null) {
            mMediaLeaderTask.cancel(true);
        }
        mMediaLeaderTask = new MediaLeaderTask(this, mMediaType).setSelectedList(mSelectedList).setFilterSize(sFilterSize).setFilterDuration(sFilterDuration).setFilterMimeType(sFilterMimeType).setDisplayInvalid(mDisplayInvalid).setOnTaskListener(new OnTaskListener<MediaResult>() {
            @Override
            public void result(@Nullable MediaResult result) {
                if (null == result) return;
                mMediaFolders = result.mList;
                mSelectedList.clear();
                mSelectedList.addAll(result.mSelected);
                displayMedia(true, Math.min(mMediaFolders.size() - 1, mDisplayPosition));
                refreshCount();
            }
        });
        mMediaLeaderTask.execute();

    }


    /*初始化菜单按钮 多选显示阅览按钮*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_menu_album, menu);
        album_menu_preview = menu.findItem(R.id.album_menu_preview);
        album_menu_finish = menu.findItem(R.id.album_menu_finish);
        album_menu_preview.setVisible(mMultipleMode);
        album_menu_finish.setVisible(mMultipleMode);
        return mMultipleMode;
    }

    /*菜单按钮点击*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.album_menu_preview) {
            preview(mSelectedList, 0);
            return true;
        }
        if (itemId == R.id.album_menu_finish) {
            callbackResult();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /*界面销毁 存储变量*/
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.MEDIA_TITLE, mTitle);
        outState.putInt(Constants.MEDIA_TYPE, mMediaType);
        outState.putInt(Constants.MEDIA_CAMERA_FACING, mFacing);
        outState.getInt(Constants.MEDIA_LIST_COLUMN, mColumn);
        outState.putInt(Constants.MEDIA_LIMIT_COUNT, mLimitCount);
        outState.getInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
        outState.putLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        outState.putLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
        outState.putBoolean(Constants.MEDIA_MULTIPLE_MODE, mMultipleMode);
        outState.putBoolean(Constants.MEDIA_DISPLAY_CAMERA, mDisplayCamera);
        outState.putBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        outState.putBoolean(Constants.MEDIA_DISPLAY_INVALID, mDisplayInvalid);
        outState.putParcelableArrayList(Constants.MEDIA_SELECTED_LIST, mSelectedList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_MEDIA:
                if (PermissionUtil.isGranted(grantResults)) loadData(true);
                else dialog(requestCode, R.string.media_permission_storage, permissions);
                break;
            case Constants.REQUEST_IMAGE:
                if (PermissionUtil.isGranted(grantResults)) takeImage();
                else dialog(requestCode, R.string.media_permission_camera_image, permissions);
                break;
            case Constants.REQUEST_AUDIO:
                if (PermissionUtil.isGranted(grantResults)) takeAudio();
                else dialog(requestCode, R.string.media_permission_camera_audio, permissions);
                break;
            case Constants.REQUEST_VIDEO:
                if (PermissionUtil.isGranted(grantResults)) takeVideo();
                else dialog(requestCode, R.string.media_permission_camera_video, permissions);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }

    /*初始化配置*/
    private void initExtras(@NonNull Bundle bundle) {
        mTitle = bundle.getString(Constants.MEDIA_TITLE, mTitle);
        mMediaType = bundle.getInt(Constants.MEDIA_TYPE, mMediaType);
        mFacing = bundle.getInt(Constants.MEDIA_CAMERA_FACING, mFacing);
        mColumn = bundle.getInt(Constants.MEDIA_LIST_COLUMN, mColumn);
        mLimitCount = bundle.getInt(Constants.MEDIA_LIMIT_COUNT, mLimitCount);
        mLimitQuality = bundle.getInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
        mLimitSize = bundle.getLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        mLimitDuration = bundle.getLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
        mMultipleMode = bundle.getBoolean(Constants.MEDIA_MULTIPLE_MODE, mMultipleMode);
        mDisplayCamera = bundle.getBoolean(Constants.MEDIA_DISPLAY_CAMERA, mDisplayCamera);
        mNeedCrop = bundle.getBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        mDisplayInvalid = bundle.getBoolean(Constants.MEDIA_DISPLAY_INVALID, mDisplayInvalid);
        mSelectedList = bundle.getParcelableArrayList(Constants.MEDIA_SELECTED_LIST);
        if (null == mSelectedList) mSelectedList = new ArrayList<>();
    }

    private void selectedMedia(int position, MediaFile mediaFile) {
        //无效
        if (!mediaFile.mInvalid) {
            ToastUtil.show(R.string.media_item_invalid);
            return;
        }
        //取消选中
        if (mediaFile.mSelected) {
            mediaFile.mSelected = false;
            mSelectedList.remove(mediaFile);
            mAdapter.notifyItemChanged(position);
            refreshCount();
            return;
        }
        //限制选择数量
        if (mSelectedList.size() >= mLimitCount) {
            ToastUtil.show(getString(R.string.media_selected_limit, mLimitCount));
            mAdapter.notifyItemChanged(position);
            return;
        }
        //选中
        mediaFile.mSelected = true;
        mSelectedList.add(mediaFile);
        mAdapter.notifyItemChanged(position);
        refreshCount();
    }

    private void chooseCamera(@NonNull View view) {
        //限制选择数量
        if (mSelectedList.size() >= mLimitCount) {
            ToastUtil.show(getString(R.string.media_selected_limit, mLimitCount));
            return;
        }
        if (mMediaType == MediaType.IMAGE) takeImage();
        else if (mMediaType == MediaType.AUDIO) takeAudio();
        else if (mMediaType == MediaType.VIDEO) takeVideo();
        else chooseCameraPopup(view);
    }


    private void chooseMedia(@NonNull final View view, int position, @NonNull final MediaFile mediaFile) {
        if (!mediaFile.mInvalid) {
            ToastUtil.show(R.string.media_item_invalid);
            return;
        }
        if (!mMultipleMode) {//单选直接回调
            mediaFile.mSelected = true;
            mSelectedList.clear();
            mSelectedList.add(mediaFile);
            callbackResult();
        } else preview(view, position, mMediaFolders.get(mDisplayPosition).mList);

    }


    private void preview(View view, int position, List<MediaFile> list) {

    }

    private void takeImage() {
        if (!PermissionUtil.request(this, Constants.REQUEST_IMAGE, Constants.PERMISSION_IMAGE)) {
            return;
        }
        MediaHelper.getInstance().camera().image(MediaUtil.createImageUri(this)).crop(mNeedCrop).facing(mFacing).result(this::cameraResult).cancel(ToastUtil::show).start(this);
    }

    private void takeAudio() {
        if (!PermissionUtil.request(this, Constants.REQUEST_AUDIO, Constants.PERMISSION_AUDIO)) {
            return;
        }
        MediaHelper.getInstance().camera().audio(MediaUtil.createAudioUri(this)).size(mLimitSize).duration(mLimitDuration).result(this::cameraResult).cancel(ToastUtil::show).start(this);
    }

    private void takeVideo() {
        if (!PermissionUtil.request(this, Constants.REQUEST_VIDEO, Constants.PERMISSION_VIDEO)) {
            return;
        }
        MediaHelper.getInstance().camera().video(MediaUtil.createVideoUri(this)).facing(mFacing).size(mLimitSize).quality(mLimitQuality).duration(mLimitDuration).result(this::cameraResult).cancel(ToastUtil::show).start(this);
    }

    private void cameraResult(@NonNull MediaFile mediaFile) {
        mediaFile.mInvalid = MediaUtil.filter(mediaFile, sFilterMimeType, sFilterSize, sFilterDuration);
        if (!mediaFile.mInvalid && !mDisplayInvalid) {
            ToastUtil.show(R.string.media_item_invalid);
            return;
        }
        if (mediaFile.mInvalid) {
            if (null == mSelectedList) mSelectedList = new ArrayList<>();
            mediaFile.mSelected = true;
            mSelectedList.add(mediaFile);
            refreshCount();
            if (!mMultipleMode) {
                callbackResult();
                return;
            }
        }
        //刷新
        loadData(true);
    }


    /*选择 拍照还是 视频*/
    private void chooseCameraPopup(View view) {
        if (null == mCameraPopupMenu) mCameraPopupMenu = new PopupMenu(this, view);
        mCameraPopupMenu.getMenuInflater().inflate(R.menu.album_menu_item_camera, mCameraPopupMenu.getMenu());
        mCameraPopupMenu.setOnMenuItemClickListener(item -> {
            final int id = item.getItemId();
            if (id == R.id.album_menu_camera_image) takeImage();
            else if (id == R.id.album_menu_camera_audio) takeAudio();
            else if (id == R.id.album_menu_camera_video) takeVideo();
            return true;
        });
        mCameraPopupMenu.show();
    }


    private void displayMedia(boolean refresh, int position) {
        if (!refresh && mDisplayPosition == position) return;
        if (null == mMediaFolders || mMediaFolders.size() <= position) return;
        this.mDisplayPosition = position;
        final MediaFolder mediaFolder = mMediaFolders.get(position);
        final List<MediaFile> list = mediaFolder.mList;
        mAdapter.setData(list);
        mAdapter.notifyItemRangeChanged(0, list.size());
        recycler_list.scrollToPosition(0);
        text_media_folder.setText(mediaFolder.mName);
    }

    private void refreshCount() {
        if (null == toolbar || !mMultipleMode) return;
        int count = mSelectedList.size();
        toolbar.setTitle(R.string.media_preview_order);
        if (album_menu_preview != null)
            album_menu_preview.setTitle(getString(R.string.media_preview, count));
        if (album_menu_finish != null)
            album_menu_finish.setTitle(getString(R.string.media_count, count));
    }

    private String getMediaTitle() {
        if (!StringUtil.isEmpty(mTitle)) return mTitle;
        if (mMediaType == MediaType.IMAGE) return getString(R.string.media_image_title);
        else if (mMediaType == MediaType.VIDEO) return getString(R.string.media_video_title);
        else if (mMediaType == MediaType.AUDIO) return getString(R.string.media_audio_title);
        else return getString(R.string.media_title);
    }


    private void dialog() {
        if (null == mFolderDialog) {
            mFolderDialog = FolderDialog.getInstance().setAlbumFolders(mMediaFolders).setSelectedPosition(mDisplayPosition).setOnItemClickListener((view, position, object) -> displayMedia(false, position));
        }
        if (null != mFolderDialog && !mFolderDialog.isShowing()) mFolderDialog.show(this);
    }

    private void dialog(final int request, @StringRes final int res, final String... permissions) {
        final List<String> list = PermissionUtil.getSetting(this, permissions);
        final boolean setting = list.size() > 0;
        final boolean cancel = !(setting || request == Constants.REQUEST_MEDIA);
        new AlertDialog.Builder(this).setTitle(R.string.media_permission_title_tips).setMessage(res).setCancelable(cancel).setNegativeButton(R.string.media_cancel, (dialog, which) -> {
            dialog.dismiss();
            if (!cancel) callbackCancel(R.string.media_refuse_permission);
        }).setPositiveButton(setting ? R.string.media_setting : R.string.media_ok, (dialog, which) -> {
            dialog.dismiss();
            if (setting) IntentUtil.toPermissionView(MediaActivity.this);
            else PermissionUtil.request(MediaActivity.this, request, permissions);
        }).show();
    }

    /*点击图片或者预览按钮 预览 */
    private void preview(List<MediaFile> list, final int position) {
        if (null == list || list.size() == 0) return;
    }

    private void callbackResult() {
        if (null == mSelectedList || mSelectedList.size() == 0) {
            callbackCancel(R.string.media_choose_empty);
        } else {
            if (sResultSingle != null) sResultSingle.onAction(mSelectedList.get(0));
            if (sResultMultiple != null) sResultMultiple.onAction(mSelectedList);
            finish();
        }
    }


    private void callbackCancel(@StringRes int res) {
        if (sCancel != null) sCancel.onAction(getString(res));
        finish();
    }
}
