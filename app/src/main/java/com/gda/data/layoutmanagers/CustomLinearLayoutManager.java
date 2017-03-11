package com.gda.data.layoutmanagers;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

public class CustomLinearLayoutManager extends LinearLayoutManager {
    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public CustomLinearLayoutManager(Context context) {
        super(context);
    }
}
