package com.openxu.st;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * author : openXu
 * create at : 2016/8/5 16:37
 * blog : http://blog.csdn.net/xmxkf
 * gitHub : https://github.com/openXu
 * project : SlidingTab
 * class name : SlideTab
 * version : 1.0
 * class describe：
 */
public class SlideTab extends View {

    String TAG = "SlideTab";

    /**
     * 下面属性可以设置
     */
    private int mColorTextDef;      // 默认文本的颜色
    private int mColorDef;          // 线段和圆圈颜色
    private int mColorSelected;     //选中的字体和圆圈颜色
    private int mLineHight;         //基准线高度
    private int mCircleHight;       //圆圈的高度（直径）
    private int mCircleSelStroke;   //被选中圆圈（空心）的粗细
    private int mMarginTop;         //圆圈和文字之间的距离
    private String[] tabNames;      //需要绘制的文字
    private int mTextSize;          //文本的字体大小

    /**
     * 下面需要初始化后计算
     */
    private float splitLengh;       //每一段横线长度
    private int textStartY;         //文本绘制的Y轴坐标
    private List<Rect> mBounds;     //保存文本的量的结果

    private Paint mTextPaint;      //绘制文字的画笔
    private Paint mLinePaint;      //绘制基准线的画笔
    private Paint mCirclePaint;    //绘制基准线上灰色圆圈的画笔
    private Paint mCircleSelPaint; //绘制被选中位置的蓝色圆圈的画笔

    private boolean isSliding = false;  //手指是否在拖动
    private float slidX, slidY;         //手指当前位置（相对于本控件左上角的坐标）
    private int selectedIndex = 0;      //当前选中序号



    public SlideTab(Context context) {
        this(context, null);
    }
    public SlideTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public SlideTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化属性
//        openXu:textColorDef = "#A4A4A4"
//        openXu:textSize = "16sp"
//        openXu:circleColor = "#EAEAEA"
//        openXu:selectedColor = "#5CBB8C"
//        openXu:lintHight = "5sp"
//        openXu:circleHight = "35dip"
//        openXu:circleSelStroke = "10sp"
//        openXu:mMarginTop = "20dip"
//        openXu:tabNames = "@array/tab_names"
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidTab);
        CharSequence[] names = ta.getTextArray(R.styleable.SlidTab_tabNames);
        if(null == names || names.length<=0){
            tabNames = new String[]{"tab1","tab2","tab3","tab4"};
        }else{
            tabNames = new String[names.length];
            for(int i = 0; i<names.length;i++){
                CharSequence name = names[i];
                tabNames[i] = name.toString();
            }
        }

        mColorTextDef = ta.getColor(R.styleable.SlidTab_textColorDef, Color.GRAY);
        mColorSelected = ta.getColor(R.styleable.SlidTab_selectedColor, Color.BLUE);
        mColorDef = ta.getColor(R.styleable.SlidTab_defColor, Color.argb(255,234,234,234));   //#EAEAEA
        mTextSize = ta.getDimensionPixelSize(R.styleable.SlidTab_android_textSize, 45);

        mLineHight = ta.getDimensionPixelSize(R.styleable.SlidTab_lintHight, 5);
        mCircleHight = ta.getDimensionPixelSize(R.styleable.SlidTab_circleHight, 20);
        mCircleSelStroke = ta.getDimensionPixelSize(R.styleable.SlidTab_circleSelStroke, 10);
        mMarginTop = ta.getDimensionPixelSize(R.styleable.SlidTab_mMarginTop, 50);

//        Log.w(TAG, "tabNames :"+tabNames.length);
//        Log.w(TAG, "mColorTextDef :"+mColorTextDef);
//        Log.w(TAG, "mColorSelected :"+mColorSelected);
//        Log.w(TAG, "mColorDef :"+mColorDef);
//        Log.w(TAG, "mTextSize :"+mTextSize);
//        Log.w(TAG, "mLineHight :"+mLineHight);
//        Log.w(TAG, "mCircleHight :"+mCircleHight);
//        Log.w(TAG, "mCircleSelStroke :"+mCircleSelStroke);
//        Log.w(TAG, "mMarginTop :"+mMarginTop);

        mLinePaint = new Paint();
        mCirclePaint = new Paint();
        mTextPaint = new Paint();
        mCircleSelPaint = new Paint();

        mLinePaint.setColor(mColorDef);
        mLinePaint.setStyle(Paint.Style.FILL);//设置填充
        mLinePaint.setStrokeWidth(mLineHight);//笔宽像素
        mLinePaint.setAntiAlias(true);//锯齿不显示

        mCirclePaint.setColor(mColorDef);
        mCirclePaint.setStyle(Paint.Style.FILL);//设置填充
        mCirclePaint.setStrokeWidth(1);//笔宽像素
        mCirclePaint.setAntiAlias(true);//锯齿不显示
        mCircleSelPaint.setColor(mColorSelected);
        mCircleSelPaint.setStyle(Paint.Style.STROKE);
        mCircleSelPaint.setStrokeWidth(mCircleSelStroke);
        mCircleSelPaint.setAntiAlias(true);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColorTextDef);
        mLinePaint.setAntiAlias(true);

        measureText();
    }

    /**
     * measure the text bounds by paint
     */
    private void measureText(){
        mBounds = new ArrayList<>();
        for(String name : tabNames){
            Rect mBound = new Rect();
            mTextPaint.getTextBounds(name, 0, name.length(), mBound);
            mBounds.add(mBound);
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
//        Log.v("openxu", "宽的模式:"+widthMode);
//        Log.v("openxu", "高的模式:"+heightMode);
//        Log.v("openxu", "宽的尺寸:"+widthSize);
//        Log.v("openxu", "高的尺寸:"+heightSize);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            width = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = mBounds.get(0).height();
            height = (int) (textHeight + mCircleHight + mMarginTop);
//            Log.v(TAG, "文本的高度:"+textHeight + "控件的高度："+height);
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);
        initConstant();
    }

    private void initConstant(){
        int lineLengh = getWidth() - getPaddingLeft() - getPaddingRight() - mCircleHight;
        splitLengh = lineLengh/(tabNames.length-1);
        // FontMetrics对象
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textStartY = getHeight() - (int)fontMetrics.bottom;    //baseLine的位置
//        textStartY = mCircleHight + mMarginTop + getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画灰色基准线
        canvas.drawLine(mCircleHight/2, mCircleHight/2, getWidth()-mCircleHight/2,mCircleHight/2 , mLinePaint);

        float centerY = mCircleHight/2;
        for(int i = 0; i<tabNames.length; i++){
            float centerX = mCircleHight/2+(i*splitLengh);
            //float cx, float cy, float radius, @NonNull Paint paint
            //画基准线上灰色小圆圈
//            Log.v(TAG, "画圆：X:"+centerX+"  Y:"+centerY);
            canvas.drawCircle(centerX, centerY,mCircleHight/2,mCirclePaint);

            mTextPaint.setColor(mColorTextDef);
            if(selectedIndex == i){
                if(!isSliding){
                    //画选中位置的蓝色圆圈
                    mCircleSelPaint.setStrokeWidth(mCircleSelStroke);
                    mCircleSelPaint.setStyle(Paint.Style.STROKE);
//                    Log.v(TAG, "画圆：X:"+centerX+"  Y:"+centerY+"  半径："+(mCircleHight-mCircleSelHight)/2);
                    canvas.drawCircle(centerX, centerY, (mCircleHight-mCircleSelStroke)/2, mCircleSelPaint);
                }
                mTextPaint.setColor(mColorSelected);
            }

            //绘制文字
            float startX;
            if(i == 0){
                startX = 0;
            }else if(i == tabNames.length-1){
                startX = getWidth()-mBounds.get(i).width();
            }else{
                startX = centerX-(mBounds.get(i).width()/2);
            }
//            Log.v(TAG, "写字：X:"+startX+"  Y:"+textStartY +"  字宽度："+mBounds.get(i).width());
            canvas.drawText(tabNames[i], startX, textStartY, mTextPaint);
        }

        //画手指拖动位置圆圈,最后画，避免被其他圆圈覆盖
        if(isSliding){
//            Log.v(TAG, "手指拖动画圆：X:"+slidX+"  Y:"+centerY+"  半径："+mCircleHight/2);
            mCircleSelPaint.setStrokeWidth(1);
            mCircleSelPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(slidX, centerY, mCircleHight/2, mCircleSelPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        slidX = event.getX();   //以本控件左上角为坐标原点
        slidY = event.getY();
        //左右越界
        if(slidX< mCircleHight/2)
            slidX = mCircleHight/2;
        if(slidX>(getWidth() - mCircleHight/2))
            slidX = getWidth() - mCircleHight/2;
        Log.e(TAG, "手指位置:  getX:"+slidX+"  getY:"+slidY);
        float select = slidX/splitLengh;
        int xs = (int)(select*10)-(((int)select)*10);
        selectedIndex = (int)select +(xs>5?1:0);
//        Log.w(TAG, "手指位置在第"+select+"位置,小数为："+xs+" ,选中的序列为："+selectedIndex);
        //TODO 如果要求手指脱离了直线所在矩形之后停止滑动，放开下面代码
       /* if(slidY>mCircleHight || slidY < 0){
            Log.e(TAG, "手指落在外面了");
            if(isSliding){    //滑动到外面的，这时候需要重新绘制一次，其他事件不用重绘
                isSliding = false;
                invalidate();
            }
            isSliding = false;
            return super.onTouchEvent(event);
        }*/
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isSliding = true;
//                Log.e(TAG, "手指按下:  getX:"+slidX+"  getY:"+slidY);
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.i(TAG, "手指滑动:  getX:"+slidX+"  getY:"+slidY);
                break;
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "手指抬起:  getX:"+slidX+"  getY:"+slidY);
                isSliding = false;
                break;
        }
        invalidate();
        return true;
    }


    /***************************对外开放接口************************/
    /**
     * 获取当前选中的index,从0开始
     * @return 0-arry.leght-1
     */
    public int getSelectedIndex(){
        return selectedIndex;
    }

    /**
     * 设置当前选中的位置
     * @param selectedIndex start with 0
     */
    public void setSelectedIndex(int selectedIndex){
        if(selectedIndex < 0){
            Log.e(TAG, "selected index is error, "+selectedIndex+" is less 0, please be start with 0");
            return;
        }
        if(selectedIndex>=tabNames.length){
            Log.e(TAG, "selected index is error, the max index is "+(tabNames.length-1)+" ,but your's is "+selectedIndex);
            return;
        }
        this.selectedIndex = selectedIndex;
        invalidate();
    }

    /**
     * 设置下面的字
     * @param tabNames
     */
    public void setTabNames(String[] tabNames){
        if(tabNames!=null){
            if(tabNames.length<2){
                Log.e(TAG, "tabNames's length must be more then 2");
            }else{
                this.tabNames = tabNames;
                measureText();
            }
        }
        selectedIndex = 0;
        initConstant();
        invalidate();
    }

}