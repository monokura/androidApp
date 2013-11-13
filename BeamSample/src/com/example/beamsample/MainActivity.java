package com.example.beamsample;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener(){
			@Override
            public void onClick(View v) {
				// インテントのインスタンス生成
				Intent intent = new Intent(MainActivity.this, BeamActivity.class);
				// 次画面のアクティビティ起動
				startActivity(intent);
            }
		});
		Log.d("DEBUG","main onCreate");
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		Log.d("DEBUG","main onDestroy");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		Log.d("DEBUG","main onPause");
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();

		Log.d("DEBUG","main onRestart");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		Log.d("DEBUG","main onResume");
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Log.d("DEBUG","main onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

		Log.d("DEBUG","main onStop");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
