package com.colin.library.android.utils;

import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.helper.UtilHelper;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 21:34
 * <p>
 * 描述： android 系统存储工具类
 * 内部存储 - 私有目录
 * 从上图可以看到，/data/data 目录是按照应用的包名来组织的，每个应用在安装成功后，会自动创建新的目录（data/data/package-name），并且目录名称就是该应用的包名，所以每个应用都有专属的内部存储目录。
 * 当应用被卸载后，该目录都会被系统自动删除。所以，如果你将数据存储于内部存储中，其实就是把数据存储到自己应用包名对应的内部存储目录中。
 * 每个应用的内部存储目录都是私有的，也就是说内部存储目录下的文件只能被宿主应用访问到，其他应用是没有权限访问的。
 * 宿主应用访问自己的内部存储目录时不需要申请任何权限。因此这部分的存储也被称为：内部存储私有目录。
 * app_webview：用于存储webview加载过程中的数据，如Cookie，LocalStorage等。
 * cache：用于存储使用应用过程中产生的缓存数据。
 * code_cache：存放运行时代码优化等产生的缓存。
 * databases：主要用于存储数据库类型的数据。
 * files：可以在该目录下存储文件。
 * lib：存放App依赖的so库。
 * shared_prefs：用于存储SharedPreference文件
 * <p>
 * 内部存储 - 私有目录特点
 * 与宿主 App 的生命周期相同，应用卸载时，会被系统自动删除。
 * 宿主 App 可以直接访问，无需权限；
 * 其他应用无权访问；
 * 用户访问需 Root 权限。
 * 适合存储与应用直接相关，隐私性或敏感性高的数据。
 * <p>
 * 外部存储
 * 通俗来说，外部存储空间就是我们打开手机系统“文件管理”后看到的内容，外部存储的最外层目录是 storage 文件夹，也可以是 mnt 文件夹，这个厂家不同也会有不同的结果。
 * 一般来说，在 storage 文件夹中有一个 sdcard 文件夹，和内部存储不同的是，外部存储根据存储特点的不同可分为三种类型：私有目录、公共目录、其他目录。
 * 其中，“私有目录”属于外部存储的“私有存储空间”，“公共目录”和“其他目录”属于外部存储的“共享空间”。
 * 通常来说，应用涉及到的持久化数据分为两类：应用相关数据和应用无关数据。前者是指专供宿主 App 使用的数据信息，比如一些应用的配置信息，数据库信息，缓存文件等。
 * 当应用被卸载，这些信息也应该被随之删除，避免存储空间产生不必要的占用，适合放到（内部存储或外部存储）“私有目录”。
 * 后者更偏向于这类信息：当应用被卸载，用户仍然希望保留于设备当中的信息。常见如，拍照类应用的图片文件，用户是使用浏览器手动下载的文件等。
 * 应用无关数据应该是宿主应用希望与其他应用共享的数据，适合存放在外部存储空间的“公共目录”或“其他目录”。
 * <p>
 * 私有目录：上图中的 Android 文件夹，这个文件夹打开之后里边有一个 data 文件夹，打开这个 data 文件夹，里边有许多包名组成的文件夹，这些文件夹是应用的私有目录。
 * 公共目录：DCIM、Download、Music、Movies、Pictures、Ringtones 等这种系统为我们创建的文件夹；这些目录里的文件所有应用可以分享。
 * 其他目录：除私有目录和公共目录之外的部分。比如各个 App 在 /sdcard/ 目录下创建的目录，如支付宝创建的目录：alipy/，微博创建的目录：com.sina.weibo/，qq创建的目录：com.tencent.mobileqq/等。
 * <p>
 * 外部存储 - 私有目录
 * 与宿主 App 的生命周期相同，应用卸载时，会被系统自动删除。
 * 宿主 App 可以直接访问，无需权限。（备注：从 4.4 版本开始，宿主 App 可以直接读写外部存储空间中的应用私有目录， 4.4 版本之前，开发人员需在 Manifest 申请外部存储空间的文件读写权限。）
 * 其他 App 可以访问。（备注：自 Android 7.0 开始，系统对应用私有目录的访问权限进一步限制。其它App无法通过 file:// 这种形式的 Uri 直接读写该目录下的文件内容，需通过 FileProvider 访问。）
 * 用户可直接访问，无需权限。
 * 适合存储与应用直接相关，隐私性或敏感性都不高的数据。
 * <p>
 * 外部存储 - 公共目录
 * 与宿主 App 生命周期无关，应用卸载后，数据仍然保留；
 * 所有的App都需要申请 EXTERNAL_STORAGE 权限，Android 6.0 开始需申请动态权限；
 * 用户访问，无需权限。
 * 适合存储不敏感的数据，且希望与其他应用共享的数据。
 */
public final class StorageUtil {
    private StorageUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*判断是否存在外部存储卡*/
    public static boolean hasSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /*外部存储是否有写权限*/
    public static boolean canWrite() {
        return hasSDCard() && Environment.getExternalStorageDirectory().canWrite();
    }

    /*  /system */
    public static File getRootSystem() {
        return Environment.getRootDirectory();
    }

    /*  /data */
    public static File getRootData() {
        return Environment.getDataDirectory();
    }

    /*/data/user/0/package*/
    @NonNull
    public static File getUserData() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getDataDir();
    }

    /*/data/user/0/package/cache*/
    @NonNull
    public static File getUserCache() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getCacheDir();
    }

    /*/data/user/0/package/code_cache*/
    @NonNull
    public static File getUserCodeCache() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getCodeCacheDir();
    }

    @NonNull
    public static File getInternalDataDir() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getDataDir();
    }

    /*获取的目录是/data/data/package_name/files，即应用内部存储的files目录*/
    @NonNull
    public static File getInternalFilesDir() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getFilesDir();
    }

    /*获取的目录是/data/data/package_name/cache，即应用内部存储的cache目录*/
    @NonNull
    public static File getInternalCacheDir() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getCacheDir();
    }

    /*获取的目录是/data/data/package_name/shared_prefs，即应用内部存储的shared_prefs目录*/
    @NonNull
    public static File getInternalSpDir() {
        return new File(UtilHelper.getInstance().getUtilConfig().getApplication().getApplicationInfo().dataDir + File.separator + "shared_prefs");
    }

    /*获取的目录是/data/data/package_name/databases，即应用内部存储的databases目录*/
    @NonNull
    public static File getInternalDatabaseDir() {
        return new File(UtilHelper.getInstance().getUtilConfig().getApplication().getApplicationInfo().dataDir + File.separator + "databases");
    }

    /*获取的目录是/data/data/package_name/code_cache，即应用内部存储的code_cache目录*/
    @NonNull
    public static File getInternalCodeCacheDir() {
        return new File(UtilHelper.getInstance().getUtilConfig().getApplication().getApplicationInfo().dataDir + File.separator + "code_cache");
    }

    /*获取的目录是/data/data/package_name/name，如果该目录不存在，系统会自动创建该目录 MODE_APPEND：即向文件尾写入数据;MODE_PRIVATE：即仅打开文件可写入数据*/
    @NonNull
    public static File getInternalDir(@NonNull final String name, int mode) {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getDir(name, mode);
    }

    /*获取到的目录是 /storage/emulated/0/Android/data/package_name/cache*/
    @Nullable
    public static File getExternalCacheDir() {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getExternalCacheDir();
    }

    /*如果type为""，那么获取到的目录是 /storage/emulated/0/Android/data/package_name/files,  如果type为"test"，那么就会创建/storage/emulated/0/Android/data/package_name/files/test目录*/

    /**
     * type为""，那么获取到的目录是 /storage/emulated/0/Android/data/package_name/files
     * type为"test"，那么就会创建  /storage/emulated/0/Android/data/package_name/files/test目录
     *
     * @param type 文件夹名字
     * @return 返回指定文件夹
     */
    @Nullable
    public static File getExternalDir(@Nullable final String type) {
        return UtilHelper.getInstance().getUtilConfig().getApplication().getExternalFilesDir(type);
    }

    /*获取到的目录是/storage/emulated/0,这个也是外部存储的根目录*/
    @NonNull
    public static File getExternalPublicDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * String type
     * 1.如果type为""，那么获取到的目录是外部存储的根目录即  /storage/emulated/0
     * 2.如果type为"test"，那么就在外部存储根目录下创建test目录，android官方推荐使用以下的type类型，我们在Sdcar的根目录下也经常可以看到下面的某些目录:
     * public static String DIRECTORY_MUSIC = "Music";
     * public static String DIRECTORY_PODCASTS = "Podcasts";
     * public static String DIRECTORY_RINGTONES = "Podcasts";
     * public static String DIRECTORY_ALARMS = "Alarms";
     * public static String DIRECTORY_NOTIFICATIONS = "Notifications";
     * public static String DIRECTORY_PICTURES = "Pictures";
     * public static String DIRECTORY_MOVIES = "Movies";
     * public static String DIRECTORY_DOWNLOADS = "Download";
     * public static String DIRECTORY_DCIM = "DCIM";
     * public static String DIRECTORY_DOCUMENTS = "Documents";
     *
     * @return 返回指定文件夹
     */
    @NonNull
    public static File getExternalPublicDir(@NonNull final String type) {
        return Environment.getExternalStoragePublicDirectory(type);
    }

    /*创建缓存文件夹*/
    @NonNull
    public static File getCacheDir() {
        final File dir = getExternalCacheDir();
        return dir == null ? getInternalCacheDir() : dir;
    }


    @Nullable
    public static String getPath(@Nullable final File file) {
        return file == null ? null : file.getPath();
    }
}
