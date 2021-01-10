package com.example.action;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Ground {

    public int COLOR = Color.rgb(153, 76, 0);
    private Paint paint = new Paint();

    final Rect rect;
    public Ground(int left, int top, int right, int bottom){
        rect = new Rect(left, top, right, bottom);

        paint.setColor(COLOR);
    }

    public void draw(Canvas canvas){
        canvas.drawRect(rect, paint);
    }

    // 地面を引数分だけ横移動させる
    public void move(int moveToleft) {
        rect.offset(-moveToleft, 0);
    }
}
