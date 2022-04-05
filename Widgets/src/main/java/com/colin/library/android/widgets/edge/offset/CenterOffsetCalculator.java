package com.colin.library.android.widgets.edge.offset;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.edge.Edge;


public class CenterOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        if (offset < edge.getTargetOffset()) return edge.getStartOffset() + offset;
        if (edge.getDirection() == Direction.LEFT || edge.getDirection() == Direction.RIGHT) {
            return offset - edge.getView().getWidth() >> 1;
        } else return offset - edge.getView().getHeight() >> 1;

    }
}
