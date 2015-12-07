package com.example.violet.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.example.violet.demo.R;

/**
 * Created by violet on 2015/11/25.
 */
public class MyView extends View{
    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        int myColor = ta.getColor(R.styleable.MyView_mycolor, Color.RED);
        setBackgroundColor(myColor);
        ta.recycle();
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
