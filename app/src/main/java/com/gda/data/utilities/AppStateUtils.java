package com.gda.data.utilities;

public final class AppStateUtils {
    public static boolean isItemDeletionState;

    public static void setItemDeletionState(boolean itemDeletionState) {
        isItemDeletionState = itemDeletionState;
    }

    private AppStateUtils() {
    }
}
