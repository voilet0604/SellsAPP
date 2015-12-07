package com.example.violet.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.violet.demo.R;

public class RectView extends View{
    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RectView);

        int color = typedArray.getColor(R.styleable.RectView_mBackground_color, Color.RED);
        setBackgroundColor(color);
        typedArray.recycle();
    }

    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Paint paint;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        paint.setColor(Color.BLUE);

        canvas.translate(65, 65);
        canvas.rotate(der, 50, 50);
        canvas.drawRect(0,0,100, 100, paint);
        //让view失效，系统会自动重绘
        der++;
        invalidate();
    }

    private float der = 0;
}
