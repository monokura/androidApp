package com.example.openglsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	// �A�N�e�B�r�e�B���������ꂽ�Ƃ��ɌĂяo����郁�\�b�h
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new DrawSurfaceView(this));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Thread.interrupted();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
