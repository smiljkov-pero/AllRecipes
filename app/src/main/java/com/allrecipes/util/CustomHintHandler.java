package com.allrecipes.util;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.allrecipes.R;
import com.allrecipes.model.HintHandlerSavedState;

/**
 * <p/>
 * handler used to display the hint for text views above. The classes using this should call the methods
 * {@link CustomHintHandler#drawHint(Canvas)} inside onDraw,
 * {@link CustomHintHandler#onTextChanged(CharSequence)} inside onTextChanged and
 * it should add {@link CustomHintHandler#getHintTopHeight()} to getCompoundPaddingTop
 * You can also save / restore the state by using {@link CustomHintHandler#getState()} and {@link CustomHintHandler#updateUsingSavedState(HintHandlerSavedState)}
 */
public class CustomHintHandler {

    private final ColorStateList mHintColors;
    private final int mAnimationSteps;
    private final float mHintScale;
    private final TextView mTextView;
    private int mHintLeftPadding, mHintBottomPadding;
    private boolean mWasEmpty, mDrawAtStartOfParent = false;
    private int mAnimationFrame;
    private Animation mAnimation = Animation.NONE;
    private boolean mIsHintImprovementUsed = false;
    private boolean mShouldKeepSpaceForHintAlways = false;
    private Paint mHintPaint;

    public CustomHintHandler(TextView textView) {
        mTextView = textView;
        mHintScale = 0.8f;
        mAnimationSteps = 6;

        mHintColors = textView.getHintTextColors();
        init();
    }

    private void init() {
        mHintBottomPadding = 0;
        mHintPaint = new Paint(mTextView.getPaint());
        mHintPaint.setTypeface(mTextView.getTypeface());
        mHintPaint.setColor(mTextView.getResources().getColor(R.color.grey_cart_number));
        mHintPaint.setTextSize(mTextView.getPaint().getTextSize() * mHintScale);
        mWasEmpty = TextUtils.isEmpty(mTextView.getText());
    }

    public void onTypefaceUpdate(Typeface typeface) {
        if (mHintPaint != null) {
            mHintPaint.setTypeface(typeface);
        }
    }

    public int getHintTopHeight() {
        if (!TextUtils.isEmpty(mTextView.getHint()) && mIsHintImprovementUsed) {
            return getHintHeight();
        }
        return 0;
    }

    public void onTextChanged(CharSequence newText) {
        boolean isEmpty = TextUtils.isEmpty(newText);
        // The empty state hasn't changed, so the hint stays the same.
        if (mWasEmpty == isEmpty) {
            return;
        }

        mWasEmpty = isEmpty;
        if (!mIsHintImprovementUsed) {
            return;
        }
        // Don't animate if we aren't visible.
        if (!mTextView.isShown()) {
            return;
        }

        if (isEmpty) {
            mAnimation = Animation.GROW;
            mTextView.setHintTextColor(Color.TRANSPARENT);
        } else {
            mAnimation = Animation.SHRINK;
        }
    }

    private int getHintLeftPadding() {
        int leftPadding = 0;
        if (mDrawAtStartOfParent) {
            leftPadding -= mTextView.getLeft() - ((ViewGroup) mTextView.getParent()).getPaddingLeft();
        }
        leftPadding += mHintLeftPadding;
        return leftPadding;
    }

    public void drawHint(Canvas canvas) {
        if (TextUtils.isEmpty(mTextView.getHint()) || !mIsHintImprovementUsed) {
            return;
        }

        final boolean isAnimating = mAnimation != Animation.NONE;

        // The large hint is drawn by Android, so do nothing.
        if (!isAnimating && TextUtils.isEmpty(mTextView.getText())) {
            return;
        }

        int scrollY = mTextView.getScrollY();
        // FontMetrics m = getPaint().getFontMetrics();
        final float normalHintPosY = mTextView.getBaseline();
        final float hintPosX = mTextView.getCompoundPaddingLeft() + mTextView.getScrollX();
        final float floatingHintPosY = normalHintPosY - getHintHeight() + scrollY;
        final float normalHintSize = mTextView.getTextSize();
        final float floatingHintSize = normalHintSize * mHintScale;

        mHintPaint.setColor(mHintColors.getColorForState(mTextView.getDrawableState(), mHintColors.getDefaultColor()));
        // If we're not animating, we're showing the floating hint, so draw it
        // and bail.
        int hintLeftPadding = getHintLeftPadding();
        if (!isAnimating) {
            canvas.drawText(mTextView.getHint().toString(), hintPosX + hintLeftPadding, floatingHintPosY, mHintPaint);
            return;
        }

        if (mAnimation == Animation.SHRINK) {
            drawAnimationFrame(canvas, normalHintSize, floatingHintSize, hintPosX, hintPosX + hintLeftPadding, normalHintPosY, floatingHintPosY);
        } else {
            drawAnimationFrame(canvas, floatingHintSize, normalHintSize, hintPosX + hintLeftPadding, hintPosX, floatingHintPosY, normalHintPosY);
        }
        mAnimationFrame++;

        if (mAnimationFrame == mAnimationSteps) {
            if (mAnimation == Animation.GROW) {
                mTextView.setHintTextColor(mHintColors);
            }
            mAnimation = Animation.NONE;
            mAnimationFrame = 0;
        }

        mTextView.requestLayout();
        customInvalidate();
    }

    private int getHintHeight() {
        int hintSize = 0;
        if (!TextUtils.isEmpty(mTextView.getHint())) {
            if (mShouldKeepSpaceForHintAlways) {
                final Paint.FontMetricsInt metrics = mTextView.getPaint().getFontMetricsInt();
                hintSize = metrics.bottom - metrics.top + mHintBottomPadding;
            } else {
                if (!TextUtils.isEmpty(mTextView.getText()) || mAnimation != Animation.NONE) {
                    final Paint.FontMetricsInt metrics = mTextView.getPaint().getFontMetricsInt();
                    hintSize = metrics.bottom - metrics.top + mHintBottomPadding;

                    if (mAnimation == Animation.SHRINK) {
                        hintSize = (int) calculateIntermediateValue(0, hintSize);
                    } else if (mAnimation == Animation.GROW) {
                        hintSize = (int) calculateIntermediateValue(hintSize, 0);
                    }
                }
            }
        }
        return hintSize;
    }

    private void drawAnimationFrame(Canvas canvas, float fromSize, float toSize, float fromHintPosX, float toHintPosX, float fromY, float toY) {
        final float textSize = calculateIntermediateValue(fromSize, toSize);
        float hintPosY;
        if (mShouldKeepSpaceForHintAlways) {
            hintPosY = calculateIntermediateValue(fromY, toY);
        } else {
            hintPosY = Math.min(fromY, toY);
        }
        final float hintPosX = calculateIntermediateValue(fromHintPosX, toHintPosX);
        mHintPaint.setTextSize(textSize);
        canvas.drawText(mTextView.getHint().toString(), hintPosX, hintPosY, mHintPaint);
    }

    private float calculateIntermediateValue(float from, float to) {
        final float alpha = (float) mAnimationFrame / (mAnimationSteps - 1);
        return from * (1 - alpha) + to * alpha;
    }

    private void customInvalidate() {
        if (Utils.isOldVersion()) {
            if (mDrawAtStartOfParent) {
                ((ViewGroup) mTextView.getParent()).invalidate();
            } else {
                mTextView.invalidate();
            }
        } else {
            mTextView.invalidate();
        }
    }

    public void updateUsingSavedState(HintHandlerSavedState savedState) {
        if (savedState != null) {
            mHintBottomPadding = savedState.hintBottomPadding;
            mHintLeftPadding = savedState.hintLeftPadding;
            mShouldKeepSpaceForHintAlways = savedState.shouldKeepSpaceForHint;
            mIsHintImprovementUsed = savedState.hintImprovementUsed;
            mDrawAtStartOfParent = savedState.drawAtStartOfParent;
            mTextView.requestLayout();
            customInvalidate();
        }
    }

    public HintHandlerSavedState getState() {
        return new HintHandlerSavedState(mHintLeftPadding, mHintBottomPadding, mShouldKeepSpaceForHintAlways, mIsHintImprovementUsed, mDrawAtStartOfParent);
    }

    private enum Animation {
        NONE, SHRINK, GROW
    }

}
