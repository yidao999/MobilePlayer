package com.example.argent.mobileplayer.activity.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.argent.mobileplayer.activity.domain.Lyric;
import com.example.argent.mobileplayer.activity.utils.DensityUtil;

import java.util.ArrayList;

/**
 * author: 小川
 * Date: 2019/1/29
 * Description:
 */
@SuppressLint("AppCompatCustomView")
public class ShowLyricView extends TextView {

    /**
     * 歌词列表
     */
    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint whitePaint;

    private int width;
    private int height;
    /**
     * 歌词列表索引
     */
    private int index;
    /**
     * 每行的高
     */
    private float textHeight;

    /**
     * 当前进度
     */
    private float currentPosition;
    private float sleepTime;
    private float timePoint;

    /**
     * 设置歌词列表
     *
     * @return
     */
    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    private void initView(Context context) {
        //转换对应像素
        textHeight = DensityUtil.dip2px(context,18);
        //创建画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(DensityUtil.dip2px(context,16));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setAntiAlias(true);
        whitePaint.setTextSize(DensityUtil.dip2px(context,16));
        whitePaint.setTextAlign(Paint.Align.CENTER);

//        lyrics = new ArrayList<>();
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setTimePoint(1000 * i);
//            lyric.setSleepTime(1500 * i);
//            lyric.setContent(i + "aaaaaaaaaaaaaa" + i);
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //往上推移

        float plush = 0;
        if(sleepTime == 0){
            plush = 0;
        }else{
            //平移
            //float delta = ((currentPosition - timePoint)/sleepTime)*textHeight;
            plush = textHeight + ((currentPosition-timePoint)/sleepTime )*textHeight;

        }
        canvas.translate(0,- plush);

        //绘制歌词:
        if (lyrics != null && lyrics.size() > 0) {
            //当前部分:
            String currentText = lyrics.get(index).getContent();
            canvas.drawText(currentText, width / 2, height / 2, paint);
            //前面部分:
            float tempY = height / 2;
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY == 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitePaint);
            }
            //后面部分:

            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitePaint);
            }

        } else {
            //没有歌词
            canvas.drawText("没有歌词", width / 2, height / 2, paint);
        }
    }

    public void setshowNextLyric(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {

            if (currentPosition < lyrics.get(i).getTimePoint()) {

                int tempIndex = i - 1;

                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {
                    index = tempIndex;
                    sleepTime = lyrics.get(index).getSleepTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }

            }
        }
        //重新绘制
        invalidate();//在主线程中
        //子线程
//        postInvalidate();

    }
}
