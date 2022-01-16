package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.annotation.Direction;


public class CenterOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        if (offset < edge.getTargetOffset()) return offset + edge.getStartOffset();
        if (edge.getDirection() == Direction.LEFT || edge.getDirection() == Direction.RIGHT) {
            return offset - edge.getView().getWidth() >> 1;
        } else return offset - edge.getView().getHeight() >> 1;

    }
}
