package com.example.alarmapp;

import java.util.Calendar;
import java.util.Map;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener,SharedPreferences.OnSharedPreferenceChangeListener{
	private AlarmManager alarmManager;
	private Calendar calendar;
	private TimePicker timePicker;
	
	private int cal_hour;
	private int cal_minute;
	private int selectMode;
	private int selectSound;
	
    private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		// ボタン設定
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);
		alarmYesButton.setOnClickListener(this);
		alarmNoButton.setOnClickListener(this);
		
		// 画面設定
		settingDisplayParts();
	}
	
	private void settingDisplayParts(){
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);
				
		// 現在時間の取得
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		cal_hour = calendar.get(Calendar.HOUR);
		cal_minute = calendar.get(Calendar.MINUTE);
		
		// 保存された時刻を取得 
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        cal_hour = prefs.getInt("cal_hour", cal_hour);
        cal_minute = prefs.getInt("cal_minute", cal_minute);        
		
		// TimePicker に反映 
		timePicker = (TimePicker)findViewById(R.id.time_picker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(cal_hour);
		timePicker.setCurrentMinute(cal_minute);
		
		//resetPreference();
		// RadioButtonの設定
		selectSound = prefs.getInt("checked_sound", R.id.radioButton_sound1);
        selectMode = prefs.getInt("checked_mode", R.id.radioButton_mode1);
        
        Log.d("Check","point1");
        Log.d("Check",String.valueOf(selectSound));
        Log.d("Check",String.valueOf(R.id.radioButton_mode1));
        Log.d("Check","point2");
        Log.d("Check",String.valueOf(selectMode));
        Log.d("Check",String.valueOf(R.id.radioButton_sound1));
        
        
        RadioButton rbSound = (RadioButton)findViewById(selectSound);
        RadioButton rbMode = (RadioButton)findViewById(selectMode);
        rbSound.setChecked(true);
        rbMode.setChecked(true);

        Log.d("Check","point3");
        
        TextView set_alarm = (TextView)findViewById(R.id.text_set_alarm);
		TextView set_time = (TextView)findViewById(R.id.text_set_time);
		TextView set_sound = (TextView)findViewById(R.id.text_set_sound);
		TextView set_mode = (TextView)findViewById(R.id.text_set_mode);

		if(prefs.getBoolean("alarm_on", false)){
			// 設定時刻あり
			set_alarm.setText("登録されているアラーム");
			set_time.setText(getMessageTime());
			set_sound.setText(rbSound.getText());
			set_mode.setText(rbMode.getText());
			alarmYesButton.setText("上書き");
			alarmNoButton.setEnabled(true);
		}else{
			// 設定時刻なし
			set_alarm.setText("現在登録されているアラームはありません");
			set_time.setText("");
			set_sound.setText("");
			set_mode.setText("");
			alarmYesButton.setText("登録");
			alarmNoButton.setEnabled(false);
		}		
	}
	
	private String getMessageTime(){
		return 
			String.valueOf(cal_hour) + "時" +
			String.valueOf(cal_minute) + "分";
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonYes:
			setAlarm();
			// 画面設定
			settingDisplayParts();
			break;
		case R.id.buttonNo:
			removeAlarm();
			 // alarmをoff
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putBoolean("alarm_on", false);
		    editor.commit();
			// 画面設定
			settingDisplayParts();
			break;
		}
	}
	
	public void setAlarm(){
		Log.d("AlarmApp","setAlarm");

		// 現在時刻を取得しカレンダーに保存
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		
		// TimePicker で選択された時刻を取得 
		timePicker = (TimePicker)findViewById(R.id.time_picker);
		cal_hour = timePicker.getCurrentHour();
		cal_minute = timePicker.getCurrentMinute();
		
		// 設定時間が過去ならば24時間分足す
		long millis24hour = 24*60*60*1000;
		if(cal_hour > calendar.get(Calendar.HOUR_OF_DAY)){
	    	// 設定の「時」が未来
	    	millis24hour = 0;
	    }else if((cal_hour == calendar.get(Calendar.HOUR_OF_DAY))
	    		&& (cal_minute >= calendar.get(Calendar.MINUTE))){
	    	// 設定の「時」が同じで、「分」が未来
	    	millis24hour = 0;
	    }else{
	    	Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
	    }
		// 取得した時刻をカレンダーに設定 
		calendar.set(Calendar.HOUR_OF_DAY, cal_hour);
		calendar.set(Calendar.MINUTE, cal_minute );
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	       
		// RadioButtonで選択された音とモードを取得
	    RadioGroup radioGroupSound = (RadioGroup)findViewById(R.id.radiogroup_sound);
	    selectSound = radioGroupSound.getCheckedRadioButtonId();
	    RadioGroup radioGroupMode = (RadioGroup)findViewById(R.id.radiogroup_mode);
	    selectMode = radioGroupMode.getCheckedRadioButtonId();
	    
	    // 記録前に前のアラームを消去
	    removeAlarm();
	    
	    // アラームに登録 
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + millis24hour, getPendingIntent(selectMode,selectSound));
	    
	    // 時刻を保存 
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putInt( "cal_hour", cal_hour );
	    editor.putInt( "cal_minute", cal_minute );
	    editor.putInt( "checked_sound", selectSound);
	    editor.putInt( "checked_mode", selectMode);
	    editor.putBoolean("alarm_on", true);
	    editor.commit();
	}

	public void removeAlarm(){
       Log.d("AlarmTestActivity", "removeAlarm()");
        
        // 登録してあるアラームを解除 
       alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
       alarmManager.cancel(getPendingIntent(
    		   prefs.getInt("checked_mode", -1),
    		   prefs.getInt("checked_sound", -1)));
	}
	
    private PendingIntent getPendingIntent(int mode, int sound) {
        // 起動するアプリケーション
	    Intent intent = null;
    	switch(mode){
    	case R.id.radioButton_mode1:
    		intent = new Intent( getApplicationContext(), NormalAlarmActivity.class );
    		break;
    	case R.id.radioButton_mode2:
    		intent = new Intent( getApplicationContext(), ShakeAlarmActivity.class );
    		break;
    	case R.id.radioButton_mode3:
    		intent = new Intent( getApplicationContext(), LightAlarmActivity.class );
    		break;
    	case R.id.radioButton_mode4:
    		intent = new Intent( getApplicationContext(), VoiceAlarmActivity.class );
    		break;
    	case R.id.radioButton_mode5:
    		intent = new Intent( getApplicationContext(), ButtonAlarmActivity.class );
    		break;
    	default:
    		intent = new Intent( getApplicationContext(), NormalAlarmActivity.class );
    		break;
    	}
    	intent.putExtra("sound", sound);
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
		settingDisplayParts();
	}
	
	private void resetPreference(){
		Log.d("debug", "RESET Preference");
		SharedPreferences.Editor editor = prefs.edit();
		Map<String, ?> keys = prefs.getAll();
		if (keys.size() > 0) {
			for (String key : keys.keySet()) {
				editor.remove(key);
			}
			editor.commit();
		}
	}
}
