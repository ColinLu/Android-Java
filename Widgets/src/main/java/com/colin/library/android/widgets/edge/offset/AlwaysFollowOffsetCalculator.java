package com.colin.library.android.widgets.edge.offset;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.edge.Edge;

public class AlwaysFollowOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        return edge.getStartOffset() + offset;
    }
}
