package com.allrecipes.custom.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.allrecipes.R;
import com.allrecipes.util.Constants;
import com.allrecipes.util.TypefaceFactory;

public class AllRecipesCheckbox extends AppCompatCheckBox {

    public AllRecipesCheckbox(Context context) {
        this(context, null);
    }

    public AllRecipesCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public AllRecipesCheckbox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }


    private void setCustomFont(Context ctx, AttributeSet attrs) {
        if (attrs == null) {
            setCustomFont(ctx, Constants.DEFAULT_FONT);
        }
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.custom_font);
        String customFont = a.getString(R.styleable.custom_font_customFont);
        if (customFont != null) {
            setCustomFont(ctx, customFont);
        } else {
            setCustomFont(ctx, Constants.DEFAULT_FONT);
        }
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = TypefaceFactory.getInstance().getFont(ctx, asset);

        setTypeface(tf);
        return true;
    }
}
