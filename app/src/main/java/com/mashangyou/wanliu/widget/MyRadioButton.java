package com.mashangyou.wanliu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.blankj.utilcode.util.ConvertUtils;
import com.mashangyou.wanliu.R;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

/**
 * Created by Administrator on 2020/9/14.
 * Des:
 */
public class MyRadioButton extends AppCompatRadioButton {

    private String mText;
    private Paint mPaint;
    /**
     * 底部画笔
     */
    private Paint mCirclePaint;
    private int mBgColor;
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;
    private float mBgHeight;
    private float mBgWidth;
    private int mTextSize;
    private Paint mTextPaint;
    private int mTextColor;
    private int mTextColorSel;
    private int mTextSizeSel;
    private boolean isCheck;
    private Paint mTextSelPaint;

    public MyRadioButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyRadioButton);
        mText = typedArray.getString(R.styleable.MyRadioButton_text);
        mBgColor = typedArray.getColor(R.styleable.MyRadioButton_bg_color, Color.YELLOW);
        mTextColor = typedArray.getColor(R.styleable.MyRadioButton_text_color, Color.GRAY);
        mTextColorSel = typedArray.getColor(R.styleable.MyRadioButton_text_color_sel, Color.BLACK);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.MyRadioButton_text_size, ConvertUtils.sp2px(12));
        mTextSizeSel = typedArray.getDimensionPixelOffset(R.styleable.MyRadioButton_text_size_sel, ConvertUtils.sp2px(15));
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mBgColor);
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStrokeWidth(5f);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mTextSelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setButtonDrawable(null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mWidth = w;
            mHeight = h;
            mCenterX = w / 2;
            mCenterY = h / 2;
            mBgWidth = mWidth;
            mBgHeight = mBgWidth / 5;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth <= 0 || mHeight <= 0) {
            return;
        }
        if (isCheck){
            drawBg(canvas);
            drawTextSel(canvas);
        }else{
            drawText(canvas);
        }

    }

    private void drawTextSel(Canvas canvas) {
        canvas.save();
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                mTextSizeSel, getResources().getDisplayMetrics());
        mTextSelPaint.setAntiAlias(true);
        mTextSelPaint.setStyle(Paint.Style.FILL);
        mTextSelPaint.setTextSize(scaledSizeInPixels);
        mTextSelPaint.setTextAlign(Paint.Align.CENTER);
        mTextSelPaint.setFakeBoldText(true);
        mTextSelPaint.setColor(mTextColorSel);
        Paint.FontMetricsInt fontMetrics = mTextSelPaint.getFontMetricsInt();
        float baseline = (mHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(mText, mCenterX, baseline, mTextSelPaint);
        canvas.restore();
    }
    private void drawText(Canvas canvas) {
        canvas.save();
        float scaledSizeInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                mTextSize, getResources().getDisplayMetrics());
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(scaledSizeInPixels);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setColor(mTextColor);
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        float baseline = (mHeight - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(mText, mCenterX, baseline, mTextPaint);
        canvas.restore();
    }

    /**
     * 画底部的选中圆点
     */
    private void drawBg(Canvas canvas) {
        canvas.save();
        canvas.drawRoundRect(new RectF(0, mHeight - mBgHeight, mWidth, mHeight),
                mBgHeight / 2, mBgHeight / 2, mCirclePaint);
        canvas.restore();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        isCheck = checked;
        postInvalidate();
    }
}
