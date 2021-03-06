package com.example.openglsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
	
	// スレッドクラス
	Thread mainLoop = null;
	// 描画用
	Paint paint = null;

	// 円のX,Y座標
	private int circleX = 0;
	private int circleY = 0;
	// 円の移動量
	private int circleVx = 5;
	private int circleVy = 5;

	public DrawSurfaceView(Context context) {
		super(context);
		// SurfaceView描画に用いるコールバックを登録する。
		getHolder().addCallback(this);
		// 描画用の準備
		paint = new Paint();
		paint.setColor(Color.BLACK);
		// スレッド開始
		mainLoop = new Thread(this);
		mainLoop.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO 今回は何もしない。
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// SurfaceView生成時に呼び出されるメソッド。
		// 今はとりあえず背景を白にするだけ。
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.RED);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 今回は何もしない。
	}

	@Override
	public void run() {
		// Runnableインターフェースをimplementsしているので、runメソッドを実装する
		// これは、Threadクラスのコンストラクタに渡すために用いる。
		while (true) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null)
			{
				canvas.drawColor(Color.WHITE);
				// 円を描画する
				canvas.drawCircle(circleX, circleY, 10, paint);
				getHolder().unlockCanvasAndPost(canvas);
				// 円の座標を移動させる
				circleX += circleVx;
				circleY += circleVy;
				// 画面の領域を超えた？
				if (circleX < 0 || getWidth() < circleX)  circleVx *= -1;
				if (circleY < 0 || getHeight() < circleY) circleVy *= -1;
			}
		}
	}

}