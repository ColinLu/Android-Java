package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

public class FixToTargetOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        if (offset < edge.getTargetOffset()) return offset + edge.getStartOffset();
        return edge.getTargetOffset() + edge.getStartOffset();
    }
}
