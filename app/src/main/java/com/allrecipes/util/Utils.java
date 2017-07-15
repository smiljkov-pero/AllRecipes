package com.allrecipes.util;

public class Utils {

    /**
     * @return if os version is 2.3 or older
     */
    public static boolean isOldVersion() {
        return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB;
    }
}
