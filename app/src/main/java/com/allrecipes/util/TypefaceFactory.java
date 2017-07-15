package com.allrecipes.util;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;

public class TypefaceFactory {

    private static TypefaceFactory instance = null;

    private HashMap<String, Typeface> fontMap = new HashMap<String, Typeface>();

    /**
     * Empty constructor
     *
     * @author sergiopereira
     */
    private TypefaceFactory() {
    }

    /**
     * Function that return the instance of the class
     *
     * @return TypefaceFactory
     * @author sergiopereira
     */
    public static TypefaceFactory getInstance() {
        if (instance == null)
            instance = new TypefaceFactory();
        return instance;
    }

    /**
     * Function used to get a typeface using font
     *
     * @param context
     * @param font
     * @return Typeface
     * @author sergiopereira
     */
    public Typeface getFont(Context context, String font) {
        // Get typeface and validate it
        Typeface typeface = fontMap.get(font);
        if (typeface == null) {
            // Create typeface and save it
            try {
                typeface = Typeface.createFromAsset(context.getResources().getAssets(), font);

                fontMap.put(font, typeface);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return typeface;
    }

}