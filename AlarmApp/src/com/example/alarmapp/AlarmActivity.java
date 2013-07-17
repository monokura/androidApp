package com.example.alarmapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AlarmActivity extends Activity{
	private SoundPool soundPool;
	private int soundIndex;
    //private Button startButton;

    private SharedPreferences prefs;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_alarm);
		// Show the Up button in the action bar.
		setupActionBar();

        // プリファレンスの設定
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // sound読み込み
		soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
		
		// intentの受信
        Intent intent = getIntent();
        int selectSound = intent.getIntExtra("sound", -1);
        
        switch(selectSound){
        case R.id.radioButton_sound1:
        	Toast.makeText(this, "Sound1", Toast.LENGTH_SHORT).show();
        	soundIndex = soundPool.load(this, R.raw.sound1, 1);
    		break;
        case R.id.radioButton_sound2:
        	Toast.makeText(this, "Sound2", Toast.LENGTH_SHORT).show();
        	soundIndex = soundPool.load(this, R.raw.sound2, 1);
    		break;
        case R.id.radioButton_sound3:
        	Toast.makeText(this, "Sound3", Toast.LENGTH_SHORT).show();
        	soundIndex = soundPool.load(this, R.raw.sound3, 1);
        	break;
        case R.id.radioButton_sound4:
        	Toast.makeText(this, "Sound4", Toast.LENGTH_SHORT).show();
        	soundIndex = soundPool.load(this, R.raw.sound4, 1);
        	break;
        default:
        	Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
        	soundIndex = soundPool.load(this, R.raw.sound1, 1);
        	break;	
        }
        
		// soundが読み込まれた時の処理
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (0 == status) {
                    Toast.makeText(getApplicationContext(), "LoadComplete", Toast.LENGTH_LONG).show();
                    //soundPool.play(soundIndex, 0.5F, 0.5F, 0, -1, 1.0F);
                }
            }
        });
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		soundPool.release();
	    setAlarmOff();
		super.onDestroy();
	}
	
	protected void setAlarmOff(){
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean("alarm_on", false);
	    editor.commit();
	}

	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "finish", Toast.LENGTH_SHORT).show();
		super.finish();
		soundPool.release();
		setAlarmOff();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alarm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
