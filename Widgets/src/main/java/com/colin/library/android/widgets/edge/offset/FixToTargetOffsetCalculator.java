package com.colin.library.android.widgets.edge.offset;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.edge.Edge;

public class FixToTargetOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        if (offset < edge.getTargetOffset()) return offset + edge.getStartOffset();
        return edge.getTargetOffset() + edge.getStartOffset();
    }
}
