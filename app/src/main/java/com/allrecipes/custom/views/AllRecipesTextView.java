package com.allrecipes.custom.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.allrecipes.R;
import com.allrecipes.model.HintHandlerSavedState;
import com.allrecipes.util.Constants;
import com.allrecipes.util.CustomHintHandler;
import com.allrecipes.util.TypefaceFactory;

public class AllRecipesTextView extends AppCompatTextView {

    // used because inside onTextChanged we only wanna clear the error if the
    // text actually changed
    private String localText;
    private final CustomHintHandler hintHandler;

    public AllRecipesTextView(Context context) {
        this(context, null);
    }

    public AllRecipesTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AllRecipesTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        hintHandler = new CustomHintHandler(this);
        if (attrs != null) {
            setCustomFont(context, attrs);
        }
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        if (!isInEditMode()) {
            TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.custom_font);
            String customFont = a.getString(R.styleable.custom_font_customFont);
            if (customFont != null) {
                setCustomFont(ctx, customFont);
            } else {
                setCustomFont(ctx, Constants.DEFAULT_FONT);
            }
            a.recycle();
        }
    }

    private boolean setCustomFont(Context ctx, String asset) {
        if (!isInEditMode()) {
            Typeface tf = TypefaceFactory.getInstance().getFont(ctx, asset);
            setTypeface(tf);
        }
        return true;
    }

    public void setError(int errorResource) {
        setError(getResources().getString(errorResource));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.hintHandlerSavedState = hintHandler.getState();
        savedState.localText = localText;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState savedState = (SavedState) state;
            hintHandler.updateUsingSavedState(savedState.hintHandlerSavedState);
            localText = savedState.localText;
            super.onRestoreInstanceState(savedState.getSuperState());
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        private HintHandlerSavedState hintHandlerSavedState;
        String localText;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            hintHandlerSavedState = in.readParcelable(HintHandlerSavedState.class.getClassLoader());
            localText = in.readString();
        }

        @Override
        public void writeToParcel(@NonNull Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(hintHandlerSavedState, flags);
            out.writeString(localText);
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        hintHandler.drawHint(canvas);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (!TextUtils.equals(text, localText)) {
            localText = text.toString();

            if (hintHandler != null) {
                hintHandler.onTextChanged(text);
            }
        }
    }

    @Override
    public void setTypeface(Typeface tf) {
        super.setTypeface(tf);
        if (hintHandler != null) {
            hintHandler.onTypefaceUpdate(tf);
        }
    }

    @Override
    public int getCompoundPaddingTop() {
        return hintHandler.getHintTopHeight() + super.getCompoundPaddingTop();
    }
}
