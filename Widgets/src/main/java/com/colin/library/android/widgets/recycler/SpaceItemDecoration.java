package com.colin.library.android.widgets.recycler;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.widgets.annotation.Orientation;

/**
 * 作者： ColinLu
 * 时间： 2022-02-15 23:17
 * <p>
 * 描述： TODO
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    @Orientation
    public int mOrientation;
    @Px
    public int mSpace;
    public boolean mEdge;

    public SpaceItemDecoration(@Px int space) {
        this(Orientation.VERTICAL, space, false);
    }

    public SpaceItemDecoration(int orientation, @Px int space, boolean edge) {
        this.mOrientation = orientation;
        this.mSpace = space;
        this.mEdge = edge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (parent.getLayoutManager() == null) return;
        if (mOrientation == RecyclerView.HORIZONTAL) {
            getHorizontalItemOffsets(outRect, view, parent, state);
        } else if (mOrientation == RecyclerView.VERTICAL) {
            getVerticalItemOffsets(outRect, view, parent, state);
        } else {
            final int orientation = getManagerOrientation(parent);
            if (orientation == RecyclerView.HORIZONTAL) {
                getHorizontalItemOffsets(outRect, view, parent, state);
            } else if (orientation == RecyclerView.VERTICAL) {
                getVerticalItemOffsets(outRect, view, parent, state);
            }
        }
    }

    private int getManagerOrientation(@NonNull RecyclerView recyclerView) {
        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) manager).getOrientation();
        }
        return -1;
    }

    private void getHorizontalItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int itemCount = parent.getAdapter().getItemCount();
        if (itemCount == 0) return;
        final int position = parent.getChildAdapterPosition(view);
        final int size = mSpace >> 1;
        if (position == 0) {
            outRect.set(mEdge ? mSpace : 0, 0, size, 0);
        } else if (position == itemCount - 1) {
            outRect.set(size, 0, mEdge ? mSpace : 0, 0);
        } else outRect.set(size, 0, size, 0);
    }

    private void getVerticalItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        final int itemCount = parent.getAdapter().getItemCount();
        if (itemCount == 0) return;
        final int position = parent.getChildAdapterPosition(view);
        final int size = mSpace >> 1;
        if (position == 0) {
            outRect.set(0, mEdge ? mSpace : 0, 0, size);
        } else if (position == itemCount - 1) {
            outRect.set(0, size, 0, mEdge ? mSpace : 0);
        } else outRect.set(0, size, 0, size);
    }

}
