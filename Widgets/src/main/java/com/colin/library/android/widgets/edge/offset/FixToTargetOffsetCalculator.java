package com.colin.library.android.widgets.edge.offset;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.edge.Edge;

public class FixToTargetOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        final int abs = Math.abs(offset);
        if (abs < edge.getTargetOffset()) return abs + edge.getStartOffset();
        return edge.getTargetOffset() + edge.getStartOffset();
    }
}
