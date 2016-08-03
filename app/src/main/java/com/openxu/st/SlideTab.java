package com.openxu.st;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * author : openXu
 * create at : 2016/8/3 16:37
 * blog : http://blog.csdn.net/xmxkf
 * gitHub : https://github.com/openXu
 * project : SlidingTab
 * class name : SlideTab
 * version : 1.0
 * class describe：
 */
public class SlideTab extends View {

    String TAG = "SlideTab";

    private int mColorTextDef;   // 默认文本的颜色
    private int mColorDef;       //
    private int mColorSel;

    private int mLineHight;
    private int mCircleHight;
    private int mCircleSelHight;
    private int mMarginTop;
    private int splitLengh;   //每一段横线长度
    private int textStartY;

    private int selectedIndex = 0;   //当前选中序号

    private String[] tabNames;   //需要绘制的文字|
    private int mTextSize;       //文本的大小

    private List<Rect> mBounds;   //绘制时控制文本绘制的范围

    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mCirclePaint;
    private Paint mCircleSelPaint;

    public SlideTab(Context context) {
        this(context, null);
    }
    public SlideTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SlideTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        tabNames = new String[]{"不推","推很重要的", "推重要的","全推"};
        mColorTextDef = Color.BLACK;
        mColorSel = Color.BLUE;
        mColorDef = Color.argb(255,234,234,234);   //#EAEAEA
        mTextSize = 45;
        mLineHight = 5;
        mCircleHight = 50;
        mCircleSelHight = 25;
        mMarginTop = 70;

        mLinePaint = new Paint();
        mCirclePaint = new Paint();
        mTextPaint = new Paint();

        mLinePaint.setColor(mColorDef);
        mLinePaint.setStyle(Paint.Style.FILL);//设置填充
        mLinePaint.setStrokeWidth(mLineHight);//笔宽像素
        mLinePaint.setAntiAlias(true);//锯齿不显示

        mCirclePaint.setColor(mColorDef);
        mCirclePaint.setStyle(Paint.Style.FILL);//设置填充
        mCirclePaint.setStrokeWidth(mLineHight);//笔宽像素
        mCirclePaint.setAntiAlias(true);//锯齿不显示
        mCircleSelPaint.setColor(mColorSel);
        mCircleSelPaint.setStyle(Paint.Style.STROKE);
        mCircleSelPaint.setStrokeWidth(mCircleSelHight);
        mCircleSelPaint.setAntiAlias(true);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColorTextDef);
        mLinePaint.setAntiAlias(true);

        measureText();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        Log.v("openxu", "宽的模式:"+widthMode);
        Log.v("openxu", "高的模式:"+heightMode);
        Log.v("openxu", "宽的尺寸:"+widthSize);
        Log.v("openxu", "高的尺寸:"+heightSize);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            width = widthSize;
        }
        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = mBounds.get(0).height();
            height = (int) (textHeight + mCircleHight + mMarginTop + getPaddingTop() + getPaddingBottom());
            initConstant();
            Log.v(TAG, "文本的高度:"+textHeight + "控件的高度："+height);
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
    }

    //获得绘制文本的宽和高
    private void measureText(){
        mBounds = new ArrayList<>();
        for(String name : tabNames){
            Rect mBound = new Rect();
            mTextPaint.getTextBounds(name, 0, name.length(), mBound);
            mBounds.add(mBound);
        }
    }
    private void initConstant(){
        int lineLengh = getWidth() - getPaddingLeft() - getPaddingRight() - mCircleHight;
        splitLengh = lineLengh/(tabNames.length-1);
        Log.v(TAG, "getPaddingTop()"+getPaddingTop());
        textStartY = mCircleHight + mMarginTop + getPaddingTop();
    }

    public void setTabNames(String[] tabNames){
        if(tabNames!=null){
            if(tabNames.length<2){
                Log.e(TAG, "tabNames's length must be more then 2");
            }else{
                this.tabNames = tabNames;
                measureText();
            }
        }
        initConstant();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画直线
        canvas.drawLine(mCircleHight/2, mCircleHight/2, getWidth()-mCircleHight/2,mCircleHight/2 , mLinePaint);

        for(int i = 0; i<tabNames.length; i++){

            int centerX = mCircleHight/2+(i*splitLengh);
            int centerY = mCircleHight/2;
            //float cx, float cy, float radius, @NonNull Paint paint
            //画小圆圈
            Log.v(TAG, "画圆：X:"+centerX+"  Y:"+centerY);
            canvas.drawCircle(centerX, centerY,mCircleHight/2,mCirclePaint);

            if(selectedIndex == i){
                //画选中圆圈
                canvas.drawCircle(centerX, centerY, mCircleHight/2-mCircleSelHight, mCircleSelPaint);
                mTextPaint.setColor(mColorSel);
            }else{
                mTextPaint.setColor(mColorTextDef);
            }

            //绘制文字
            int startX = 0;
            if(i == 0){
                startX = 0;
            }else if(i == tabNames.length-1){
                startX = getWidth()-mBounds.get(i).width();
            }else{
                startX = centerX-(mBounds.get(i).width()/2);
            }
            Log.v(TAG, "写字：X:"+startX+"  Y:"+textStartY +"  字宽度："+mBounds.get(i).width());
            canvas.drawText(tabNames[i], startX, textStartY, mTextPaint);

        }

        //绘制文字
//        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}