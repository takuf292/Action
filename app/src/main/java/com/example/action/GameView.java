package com.example.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final long DRAW_INTERVAL = 1000 / 100;

    private class DrawThread extends Thread{
        private final AtomicBoolean isFinished = new AtomicBoolean(false);
        public void finish(){
            isFinished.set(true);
        }
        @Override
        public void run() {
            SurfaceHolder holder = getHolder();

            while (!isFinished.get()) {
                if (holder.isCreating()) {
                    continue;
                }
                Canvas canvas = holder.lockCanvas();
                if (canvas == null) {
                    continue;
                }

                drawGame(canvas);

                holder.unlockCanvasAndPost(canvas);
                synchronized (this) {
                    try {
                        wait(DRAW_INTERVAL);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }


    }

    private DrawThread drawThread;

    public void startDrawThread() {
        stopDrawThread();

        drawThread = new DrawThread();
        drawThread.start();
    }

    public boolean stopDrawThread() {
        if (drawThread == null) {
            return false;
        }
        drawThread.finish();
        drawThread = null;

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder, int format, int width, int height){
        startDrawThread();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        stopDrawThread();
    }

    private static final int GROUND_MOVE_TO_LEFT = 10;
    private static final int GROUND_HEIGHT = 50;
    private Ground ground;

    private Bitmap droidBitmap;
    private Droid droid;

    private final Droid.CallBack droidCallBack = new Droid.CallBack() {
        @Override
        public int getDistanceFromGround(Droid droid) {
            // 自機が地面の上に存在するかを判別している。
            // ドロイドの左が、地面の右より大きくかつ　ドロイドの右が地面の左より大きい時の反対
            boolean horizontal = !(droid.rect.left >= ground.rect.right)
                    || droid.rect.right <= ground.rect.left;
            // 自機の下に地面が無ければ戻り値に最大値を返す
            if(!horizontal){
                return Integer.MAX_VALUE;
            }

            return ground.rect.top - droid.rect.bottom;
        }
    };

    public GameView(Context context){
        super(context);

        droidBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.droid);
        // droidインスタンス作成時に droid.pngのbitmapを渡す
        droid = new Droid(droidBitmap, 0, 0, droidCallBack);

        getHolder().addCallback(this);
    }

    protected void drawGame(Canvas canvas){
        canvas.drawColor(Color.WHITE);

        int width = canvas.getWidth();
        int height = canvas.getHeight();

        if (ground == null){
            int top = height - GROUND_HEIGHT;
            ground = new Ground(0, top, width, height);
        }

        droid.move();
        ground.move(GROUND_MOVE_TO_LEFT);
        droid.draw(canvas);
        ground.draw(canvas);

        // これを実行するとonDrawを再度実行する。
        // 通常onDrawはライフサイクルに従って一度だけ実行されるがこれ使うと無限ループ作れる
    }

    private static final long MAX_TOUCH_TIME = 500;  //　ミリ秒
    private long touchDownStartTime;

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDownStartTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                float time = System.currentTimeMillis() - touchDownStartTime;
                jumpDroid(time);
                touchDownStartTime = 0;
                break;
        }
        return super.onTouchEvent(event);
    }

    private void jumpDroid(float time){
        if (droidCallBack.getDistanceFromGround(droid) > 0){
            return;
        }

        droid.jump(Math.min(time, MAX_TOUCH_TIME )/MAX_TOUCH_TIME);
    }
}
