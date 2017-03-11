package com.gda.data.utilities;

import android.content.res.Resources;
import android.util.TypedValue;

public final class DimensionsUtils {
    public final static int dialogYOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, Resources.getSystem().getDisplayMetrics());

    public final static float smallDecoViewLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, Resources.getSystem().getDisplayMetrics());
    public final static float largeDecoViewLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, Resources.getSystem().getDisplayMetrics());

    private DimensionsUtils() {
    }
}
