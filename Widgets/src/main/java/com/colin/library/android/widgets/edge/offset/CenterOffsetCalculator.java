package com.colin.library.android.widgets.edge.offset;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.edge.Edge;


public class CenterOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        final int abs = Math.abs(offset);
        if (abs < edge.getTargetOffset()) return edge.getStartOffset() + abs;
        if (edge.getDirection() == Direction.LEFT || edge.getDirection() == Direction.RIGHT) {
            return abs - edge.getView().getWidth() >> 1;
        } else return abs - edge.getView().getHeight() >> 1;

    }
}
