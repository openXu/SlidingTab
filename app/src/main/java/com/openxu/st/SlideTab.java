package com.openxu.st;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

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

    private int mLineColor;
    private int mLineHight;

    private int mCircleHight;
    private int mCircleColorSelect;

    private String[] tabNames;   //需要绘制的文字|
    private int mTextSize;       //文本的大小
    private int mTextColorDef;   //默认文本的颜色
    private int mTextColorSelect;//选中文本的颜色

    private int mMarginTop;

    private int splitLengh;   //每一段横线长度

    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mTextPaint;
    private Paint mLinePaint;
    private Paint mCirclePaint;

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
        mTextColorDef = Color.BLACK;
        mTextColorSelect = Color.BLUE;
        mLineColor = Color.argb(0,234,234,234);   //#EAEAEA
        mTextColorSelect = Color.BLUE;
        mTextSize = 60;
        mLineHight = 10;
        mCircleHight = 30;
        mMarginTop = 30;

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.FILL);//设置填充
        mLinePaint.setStrokeWidth(mLineHight);//笔宽像素
        mLinePaint.setAntiAlias(true);//锯齿不显示

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mLineColor);
        mCirclePaint.setStyle(Paint.Style.FILL);//设置填充
        mCirclePaint.setStrokeWidth(mLineHight);//笔宽像素
        mCirclePaint.setAntiAlias(true);//锯齿不显示

        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColorDef);
        mLinePaint.setAntiAlias(true);
        //获得绘制文本的宽和高
        mBound = new Rect();
        mTextPaint.getTextBounds(tabNames[0], 0, tabNames[0].length(), mBound);
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
            float textHeight = mBound.height();
            height = (int) (textHeight + mCircleHight + mMarginTop + getPaddingTop() + getPaddingBottom());
            initConstant();
            Log.v(TAG, "文本的高度:"+textHeight + "控件的高度："+height);
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
    }


    private void initConstant(){
        int lineLengh = getWidth() - getPaddingLeft() - getPaddingRight() - mCircleHight;
        splitLengh = lineLengh/(tabNames.length-1);
    }

    public void setTabNames(String[] tabNames){
        if(tabNames!=null){
            if(tabNames.length<2){
                Log.e(TAG, "tabNames's length must be more then 2");
            }else{
                this.tabNames = tabNames;
            }
        }
        initConstant();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画直线
        canvas.drawLine(mCircleHight/2, mCircleHight/2, getWidth()-mCircleHight/2,mCircleHight/2 , mLinePaint);

        for(int i = 0; i<tabNames.length; i++){
            //float cx, float cy, float radius, @NonNull Paint paint
            //画小圆圈
            Log.v(TAG, "画圆：X:"+(mCircleHight/2+(i*splitLengh))+"  Y:"+mCircleHight/2);
            canvas.drawCircle(mCircleHight/2+(i*splitLengh),mCircleHight/2,mCircleHight/2,mCirclePaint);

        }

        //绘制文字
//        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }
}