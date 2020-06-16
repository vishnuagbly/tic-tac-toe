package com.example.tictactoe;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridLayout;

public class SquareLayout extends GridLayout {

    public SquareLayout(Context context, AttributeSet attr, int defstyle){
        super(context, attr, defstyle);
    }

    public SquareLayout(Context context, AttributeSet attr){
        super(context, attr);
    }

    public SquareLayout(Context context){
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int temp = widthMeasureSpec;
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if(size == 0)
            temp = heightMeasureSpec;
        super.onMeasure(temp, temp);
    }
}
