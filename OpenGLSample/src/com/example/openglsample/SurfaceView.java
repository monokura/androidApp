package com.example.openglsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class DrawSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable{
	
	// �X���b�h�N���X
	Thread mainLoop = null;
	// �`��p
	Paint paint = null;

	// �~��X,Y���W
	private int circleX = 0;
	private int circleY = 0;
	// �~�̈ړ���
	private int circleVx = 5;
	private int circleVy = 5;

	public DrawSurfaceView(Context context) {
		super(context);
		// SurfaceView�`��ɗp����R�[���o�b�N��o�^����B
		getHolder().addCallback(this);
		// �`��p�̏���
		paint = new Paint();
		paint.setColor(Color.BLACK);
		// �X���b�h�J�n
		mainLoop = new Thread(this);
		mainLoop.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO ����͉������Ȃ��B
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// SurfaceView�������ɌĂяo����郁�\�b�h�B
		// ���͂Ƃ肠�����w�i�𔒂ɂ��邾���B
		Canvas canvas = holder.lockCanvas();
		canvas.drawColor(Color.RED);
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO ����͉������Ȃ��B
	}

	@Override
	public void run() {
		// Runnable�C���^�[�t�F�[�X��implements���Ă���̂ŁArun���\�b�h����������
		// ����́AThread�N���X�̃R���X�g���N�^�ɓn�����߂ɗp����B
		while (true) {
			Canvas canvas = getHolder().lockCanvas();
			if (canvas != null)
			{
				canvas.drawColor(Color.WHITE);
				// �~��`�悷��
				canvas.drawCircle(circleX, circleY, 10, paint);
				getHolder().unlockCanvasAndPost(canvas);
				// �~�̍��W���ړ�������
				circleX += circleVx;
				circleY += circleVy;
				// ��ʂ̗̈�𒴂����H
				if (circleX < 0 || getWidth() < circleX)  circleVx *= -1;
				if (circleY < 0 || getHeight() < circleY) circleVy *= -1;
			}
		}
	}

}