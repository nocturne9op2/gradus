package com.gda.data.utilities;

import android.graphics.Color;

public final class ColorUtils {
    public static int changeColor(int color, float factor) {
        return Color.argb(Color.alpha(color),
                Math.min(Math.round(Color.red(color) * factor), 255),
                Math.min(Math.round(Color.green(color) * factor), 255),
                Math.min(Math.round(Color.blue(color) * factor), 255));
    }

    private ColorUtils() {
    }
}
