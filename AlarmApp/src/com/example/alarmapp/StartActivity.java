package com.example.alarmapp;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class StartActivity extends Activity implements OnClickListener,SharedPreferences.OnSharedPreferenceChangeListener{
	private AlarmManager alarmManager;
	private Calendar calendar;
	private TimePicker timePicker;
	
	private int cal_hour;
	private int cal_minute;
	
    private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		// 現在時間の取得
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		cal_hour = calendar.get(Calendar.HOUR);
		cal_minute = calendar.get(Calendar.MINUTE);
		
		// ボタン設定
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);
		
		alarmYesButton.setOnClickListener(this);
		alarmNoButton.setOnClickListener(this);
		
		// 保存された時刻を取得 
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        getSharedPreferences();
        
        // TimePicker に反映 
       timePicker = (TimePicker)findViewById(R.id.time_picker);
       timePicker.setIs24HourView(true);
       timePicker.setCurrentHour(cal_hour);
       timePicker.setCurrentMinute(cal_minute);
	}
	
    private void getSharedPreferences() {
        // 保存されていた時刻を取得 
       cal_hour = prefs.getInt("cal_hour", cal_hour);
       cal_minute = prefs.getInt("cal_minute", cal_minute);
    }
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);
		
		switch(v.getId()){
		case R.id.buttonYes:
			// ボタン設定
			alarmYesButton.setEnabled(false);
			alarmNoButton.setEnabled(true);
			
			startAlarm();
			break;
		case R.id.buttonNo:
			// ボタン設定
			alarmYesButton.setEnabled(true);
			alarmNoButton.setEnabled(false);
						
			stopAlarm();
			break;
		}
	}
	
	public void startAlarm(){
		Log.d("AlarmApp","startAlarm");
		
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
        // TimePicker で選択された時刻を取得 
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        cal_hour = timePicker.getCurrentHour();
        cal_minute = timePicker.getCurrentMinute();
        
        // 取得した時刻をカレンダーに設定 
       calendar.set(Calendar.HOUR_OF_DAY, cal_hour);
       calendar.set(Calendar.MINUTE, cal_minute );
       calendar.set(Calendar.SECOND, 0);
       calendar.set(Calendar.MILLISECOND, 0);
       
       // アラームに登録 
       alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent());
       
        // 時刻を保存 
       SharedPreferences.Editor editor = prefs.edit();
       editor.putInt( "cal_hour", cal_hour );
       editor.putInt( "cal_minute", cal_minute );
       editor.commit();
	}

	public void stopAlarm(){
       Log.d("AlarmTestActivity", "stopAlarm()");
        
        // 登録してあるアラームを解除 
       alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
       alarmManager.cancel(getPendingIntent());
	}
	
    private PendingIntent getPendingIntent() {
        // 起動するアプリケーションを登録 
    	//Intent intent = new Intent( getApplicationContext(), AlarmActivity.class );
        Intent intent = new Intent( getApplicationContext(), ShakeAlarmActivity.class );
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
       return pendingIntent;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
		// TODO Auto-generated method stub
		getSharedPreferences();
	}
}
