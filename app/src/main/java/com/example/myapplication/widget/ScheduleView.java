package com.example.myapplication.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Created by Tult
 * @email:53153451@qq.com
 * @time Create on 2020/3/7 14:31
 */
public class ScheduleView extends View {

    private Paint outLinePain = new Paint();// 最外层黑色框线
    private Paint inLinePain = new Paint();// 内部轴线
    private Paint textPain = new Paint();//    课程文字

    private static int COLUMN_NUM = 6;  //列数
    private static int ROW_NUM = 10;    // 行数
    private static int textSize = ConvertUtils.sp2px(14);//字体大小
    private static int textSize2 = ConvertUtils.sp2px(11);//字数大于5时的字体大小

    private int viewWidth; //控件宽度
    private int barWidth; //单元格宽度


    private int transLateDistance = ConvertUtils.dp2px(25); //手指两次点击的距离差

    //    private int barHeigth = ConvertUtils.dp2px(50); //单元高度
    private int barHeigth = ConvertUtils.dp2px(40); //单元高度

    private int topHeight = ConvertUtils.dp2px(80); //顶部图片的高度

    private int viewPadding = ConvertUtils.dp2px(4); // 整个vie的padding

    private int leftColumnWidth = ConvertUtils.dp2px(25);  //最左侧单元格的宽度

    private List<RectF> rectFList = new ArrayList<>();

    private List<String> scheduleList = Arrays.asList(
            "", "星期一", "星期二", "星期三", "星期四", "星期五",
            "1", "", "", "", "", "",
            "2", "语文", "", "", "", "",
            "3", "", "", "", "", "",
            "4", "", "", "数学", "", "",
            "5", "", "", "", "", "",
            "6", "", "", "数学", "", "",
            "7", "", "", "数学", "", "",
            "8", "语文", "", "", "", "",
            "9", "", "", "", "语文", ""
    );

    private int x_down = 0;
    private int y_down = 0; //前一次点击的x,y坐标
    private String mText = "";  //选择需要填入的课程


    public ScheduleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPain(context);
    }

    private void initPain(Context context) {

        outLinePain.setColor(Color.BLACK);
        outLinePain.setStyle(Paint.Style.STROKE);
        outLinePain.setStrokeWidth(ConvertUtils.dp2px(1.5f));
        outLinePain.setAntiAlias(true); //抗锯齿

        inLinePain.setColor(Color.GRAY);
        inLinePain.setStyle(Paint.Style.STROKE);
        inLinePain.setStrokeWidth(ConvertUtils.dp2px(1));
        inLinePain.setAntiAlias(true); //抗锯齿

        textPain.setColor(Color.BLACK);
        textPain.setStyle(Paint.Style.FILL);
        textPain.setStrokeWidth(ConvertUtils.dp2px(1));
        textPain.setAntiAlias(true); //抗锯齿
        textPain.setTextAlign(Paint.Align.CENTER);//文字对齐
        textPain.setTextSize(textSize);  //文字大小

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        barWidth = (viewWidth - viewPadding * 2 - leftColumnWidth) / (COLUMN_NUM - 1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                x_down = (int) event.getX();
                y_down = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                int x_translate = (int) event.getX() - x_down;
                int y_translate = (int) event.getY() - y_down;
                if (x_translate < transLateDistance && y_translate < transLateDistance) {
                    if (rectFList != null && rectFList.size() > 0) {
                        for (int i = 0; i < rectFList.size(); i++) {
                            if (i < COLUMN_NUM) {//星期几这一行不替换
                                continue;
                            }
                            if (i % COLUMN_NUM == 0) {//最左侧一列 1-9 数字不替换
                                continue;
                            }
                            RectF rectF = rectFList.get(i);
                            if (rectF.contains(x_down, y_down)) {
                                scheduleList.set(i, mText);
                            }
                        }
                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //测量总体单元格
        initRectF();

        //绘制顶部图
        Bitmap bitmapTop = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_shedule_top_bg1)).getBitmap();
        RectF rectTop = new RectF();
        rectTop.left = viewPadding + 5;
        rectTop.top = viewPadding + 15;
        rectTop.right = viewWidth - viewPadding * 2 - 10;
        rectTop.bottom = topHeight - viewPadding - 15;
        canvas.drawBitmap(bitmapTop, null, rectTop, textPain);

        //布局绘制
        for (int i = 0; i < rectFList.size(); i++) {
            RectF rectF = rectFList.get(i);
            //绘制单元格
            canvas.drawRect(rectF, inLinePain);
            if (i == 0) {
                //画图
                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.mipmap.ic_clock)).getBitmap();
                canvas.drawBitmap(bitmap, null, rectF, textPain);
            }
            if (i == rectFList.size() - 1) {
                //画最外框
                canvas.drawRect(rectF, outLinePain);
                return;
            }
            String text = scheduleList.get(i);

            if (text.length() < 5) {
                textPain.setTextSize(textSize);
            } else {
                textPain.setTextSize(textSize2);
            }
            Paint.FontMetrics fontMetrics = textPain.getFontMetrics();
            float disTance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            float baseLine = rectF.centerY() + disTance;
            if (text.length() < 6) {
                canvas.drawText(text, rectF.centerX(), baseLine, textPain);
            } else {
                //名字长度大于6则分开两行显示
                int index = text.length() / 2;
                String text1 = text.substring(0, index);
                String text2 = text.substring(index);
                baseLine = rectF.centerY() + disTance - barHeigth / 4 + viewPadding;

                canvas.drawText(text1, rectF.centerX(), baseLine, textPain);
                baseLine = rectF.centerY() + disTance + barHeigth / 4 - viewPadding;
                canvas.drawText(text2, rectF.centerX(), baseLine, textPain);
            }
        }
    }

    private void initRectF() {
        rectFList.clear();
        int barPaddingTop = viewPadding + topHeight;//单元格距顶部高度

        for (int j = 0; j < ROW_NUM; j++) {
            for (int i = 0; i < COLUMN_NUM; i++) {

                LogUtils.eTag("TAG", "initRectF===j =" + j + "==i=" + i);
                RectF rectF = new RectF();

                if (j == 0) {
                    //todo 第一行单独处理
                    rectF.top = barPaddingTop;
                    rectF.bottom = leftColumnWidth + barPaddingTop;
                } else {
                    rectF.top = barHeigth * (j - 1) + leftColumnWidth + barPaddingTop;
                    rectF.bottom = barHeigth * j + leftColumnWidth + barPaddingTop;
                }
                if (i == 0) {
                    //todo 第一列单独处理
                    rectF.left = viewPadding;
                    rectF.right = leftColumnWidth + viewPadding;
                } else {
                    rectF.left = barWidth * (i - 1) + leftColumnWidth + viewPadding;
                    rectF.right = barWidth * i + leftColumnWidth + viewPadding;
                }
                rectFList.add(rectF);
            }
        }
        //最外框的线
        RectF rectFOut = new RectF();
        rectFOut.top = viewPadding;
        rectFOut.bottom = barHeigth * (ROW_NUM - 1) + leftColumnWidth + barPaddingTop;
        rectFOut.left = viewPadding;
        rectFOut.right = barWidth * (COLUMN_NUM - 1) + leftColumnWidth + viewPadding;
        rectFList.add(rectFOut);
    }

    public void setcurrentText(String text) {
        this.mText = text;
    }
}
