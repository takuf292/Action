


package com.example.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Droid {

    private static final float GRAVITY = 10.0f;
    // 重さは重力の６０倍？
    private static final float WEIGHT = GRAVITY *60;

    private final Paint paint = new Paint();

    private Bitmap bitmap;

    final Rect rect;
    // 速度
    private float velocity = 0;

    public void jump(float power){
        velocity = (power * WEIGHT);
    }

    // コールバックインターフェース　
    public interface CallBack {
        int getDistanceFromGround(Droid droid);
    }

    private final CallBack callBack;

    public Droid(Bitmap bitmap, int left, int top, CallBack callBack){
        int right = left + bitmap.getWidth();
        int bottom = top + bitmap.getHeight();
        this.rect = new Rect(left, top, right, bottom);
        this.bitmap = bitmap;
        // droidCallBackオブジェクトを受け取る
        this.callBack = callBack;
    }

    // 指定されたBitmapオブジェクトを値に応じて描画するメソッド
    public void draw(Canvas canvas){
        canvas.drawBitmap(bitmap, rect.left, rect.top, paint);
    }


    public void move(){
        // 地面との距離を表す
        int distanceFromGround = callBack.getDistanceFromGround(this);

        // 地面との距離が落下の速度より小さい場合は、自機が地面を突き抜けるので、地面との距離だけ移動するようにする
        if (velocity < 0 && velocity < -distanceFromGround){
            velocity = -distanceFromGround;
        } //moveメソッドで調整済みの速度分、縦方向に移動する。0より大きい時上向き、小さい時下向き
        rect.offset(0, Math.round(-1 * velocity));

        if (distanceFromGround == 0){
            // 落下停止
            return;
        } else if (distanceFromGround < 0){
            // 0になるように上方向に位置を補正する
            rect.offset(0,distanceFromGround);
            return;
        }
        // 現在の速度から定数GRAVITYを減産する　velocityがマイナス値になると時期の移動が
        //　上昇から下降に変わる
        velocity =- GRAVITY;
    }
}
