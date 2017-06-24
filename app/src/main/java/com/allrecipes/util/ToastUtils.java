package com.allrecipes.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author alessandro.balocco
 */
public class ToastUtils {

    private ToastUtils() {
        // No instance
    }

    public static Toast showToast(final Context context, int textResource, Object... params) {
        if (context == null) {
            return null;
        }
        return showToast(context.getApplicationContext(), context.getString(textResource, params));
    }

    public static Toast showToast(final Context context, String translatedText) {
        if (context == null) {
            return null;
        }
        Toast toast = Toast.makeText(context.getApplicationContext(), translatedText, Toast.LENGTH_SHORT);
        toast.show();
        return toast;
    }

    public static void showToast(final Context context, int yOffset, int textResource, Object... params) {
        if (context == null) {
            return;
        }
        showToast(context.getApplicationContext(), yOffset, context.getString(textResource, params));
    }

    public static void showToast(final Context context, int yOffset, String translatedText) {
        if (context == null) {
            return;
        }
        Toast toast = Toast.makeText(context.getApplicationContext(), translatedText, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, yOffset);
        toast.show();
    }

}
