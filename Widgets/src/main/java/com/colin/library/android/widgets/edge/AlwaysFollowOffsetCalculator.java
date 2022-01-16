package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

public class AlwaysFollowOffsetCalculator implements IEdgeOffsetCalculator {
    @Override
    public int calculator(@NonNull Edge edge, @Px int offset) {
        return offset + edge.getStartOffset();
    }
}
