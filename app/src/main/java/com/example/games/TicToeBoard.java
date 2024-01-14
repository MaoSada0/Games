package com.example.games;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TicToeBoard extends View {

    private final int boardColor;
    private final int OColor;
    private final int XColor;
    private final int winningLineColor;

    public TicToeBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TicToeBoard,0, 0);
        try{
            boardColor = a.getInteger(R.styleable.TicToeBoard_boardColor, 0);
            OColor = a.getInteger(R.styleable.TicToeBoard_OColor, 0);
            XColor = a.getInteger(R.styleable.TicToeBoard_XColor, 0);
            winningLineColor = a.getInteger(R.styleable.TicToeBoard_winningLineColor, 0);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);

        int dimensions = Math.min(getMeasuredWidth(), getMeasuredHeight());

        setMeasuredDimension(dimensions, dimensions);
    }
}
