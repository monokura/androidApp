package com.example.openglsmaple4;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	MyGLView myGLView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		myGLView = new MyGLView(this);
		setContentView(myGLView);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		myGLView.onResume();
	}

	@Override
	protected void onPause(){
		super.onPause();
		myGLView.onPause();
	}

}
