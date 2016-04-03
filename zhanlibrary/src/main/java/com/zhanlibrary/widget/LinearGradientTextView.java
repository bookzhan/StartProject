package com.zhanlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @Created by zhangyakai
 * @Time 16/3/5 16:41
 */
public class LinearGradientTextView extends TextView {
    private int mViewWidth;
    private Paint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;
    private int mTranslate;
    private boolean isStartAnimation;

    public LinearGradientTextView(Context context) {
        super(context);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                    new int[]{Color.WHITE, Color.WHITE}, new float[]{0, 0}, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMatrix != null && isStartAnimation) {
            mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                new int[]{Color.WHITE, Color.LTGRAY, Color.WHITE, Color.WHITE, Color.WHITE}, new float[]{0, 0.5f, 0, 0, 0}, Shader.TileMode.CLAMP);
            mPaint.setShader(mLinearGradient);

            mTranslate += mViewWidth / 10;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mMatrix);
            postInvalidateDelayed(200);
        } else if (mMatrix != null && !isStartAnimation) {
            mLinearGradient = new LinearGradient(0, 0, getWidth(), 0,
                new int[]{Color.WHITE, Color.WHITE}, new float[]{0, 0}, Shader.TileMode.CLAMP);
            mPaint.setShader(mLinearGradient);
            postInvalidateDelayed(100);
        }
    }

    public boolean isStartAnimation() {
        return isStartAnimation;
    }

    public void startAnimation() {
        this.isStartAnimation = true;
        postInvalidate();
    }

    public void stopAnimation() {
        this.isStartAnimation = false;
    }
}