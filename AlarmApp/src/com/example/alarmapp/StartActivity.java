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
	private int selectAlarm;
	private int selectSound;
	
    private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		// �{�^���ݒ�
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);
		alarmYesButton.setOnClickListener(this);
		alarmNoButton.setOnClickListener(this);
		
		// ��ʐݒ�
		settingDisplayParts();
	}
	
	private void settingDisplayParts(){
		TextView nowSet = (TextView)findViewById(R.id.text_set_time);
		Button alarmYesButton = (Button)findViewById(R.id.buttonYes);
		Button alarmNoButton = (Button)findViewById(R.id.buttonNo);

		// ���ݎ��Ԃ̎擾
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		cal_hour = calendar.get(Calendar.HOUR);
		cal_minute = calendar.get(Calendar.MINUTE);
		
		// �ۑ����ꂽ�������擾 
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        cal_hour = prefs.getInt("cal_hour", cal_hour);
        cal_minute = prefs.getInt("cal_minute", cal_minute);        
		
		// TimePicker �ɔ��f 
		timePicker = (TimePicker)findViewById(R.id.time_picker);
		timePicker.setIs24HourView(false);
		timePicker.setCurrentHour(cal_hour);
		timePicker.setCurrentMinute(cal_minute);
		
		// RadioButton�̐ݒ�
		selectSound = prefs.getInt("checked_sound", -1);
        selectAlarm = prefs.getInt("checked_mode", -1);
        if(selectSound != -1){
        	RadioButton rb = (RadioButton)findViewById(selectSound);
        	rb.setChecked(true);
        }
        if(selectAlarm != -1){
        	RadioButton rb = (RadioButton)findViewById(selectAlarm);
        	rb.setChecked(true);
        }
        
		if(prefs.getBoolean("alarm_on", false)){
			// �ݒ莞������
			nowSet.setText(getMessageTime());
			alarmYesButton.setText("�㏑��");
			alarmNoButton.setEnabled(true);
		}else{
			// �ݒ莞���Ȃ�
			nowSet.setText("�o�^����Ă��܂���");
			alarmYesButton.setText("�o�^");
			alarmNoButton.setEnabled(false);
		}
	}
	
	private String getMessageTime(){
		return 
			String.valueOf(cal_hour) + "��" +
			String.valueOf(cal_minute) + "��";
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonYes:
			setAlarm();
			// ��ʐݒ�
			settingDisplayParts();
			break;
		case R.id.buttonNo:
			removeAlarm();
			 // alarm��off
		    SharedPreferences.Editor editor = prefs.edit();
		    editor.putBoolean("alarm_on", false);
		    editor.commit();
			// ��ʐݒ�
			settingDisplayParts();
			break;
		}
	}
	
	public void setAlarm(){
		Log.d("AlarmApp","setAlarm");

		// ���ݎ������擾���J�����_�[�ɕۑ�
		calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		// TimePicker �őI�����ꂽ�������擾 
		timePicker = (TimePicker)findViewById(R.id.time_picker);
		cal_hour = timePicker.getCurrentHour();
		cal_minute = timePicker.getCurrentMinute();
		
		// �ݒ莞�Ԃ��ߋ��Ȃ��24���ԕ�����
		long millis24hour = 24*60*60*1000;
		if(cal_hour > calendar.get(Calendar.HOUR_OF_DAY)){
	    	// �ݒ�́u���v������
	    	millis24hour = 0;
	    }else if((cal_hour == calendar.get(Calendar.HOUR_OF_DAY))
	    		&& (cal_minute >= calendar.get(Calendar.MINUTE))){
	    	// �ݒ�́u���v�������ŁA�u���v������
	    	millis24hour = 0;
	    }else{
	    	Toast.makeText(this, "3", Toast.LENGTH_SHORT).show();
	    }
		// �擾�����������J�����_�[�ɐݒ� 
		calendar.set(Calendar.HOUR_OF_DAY, cal_hour);
		calendar.set(Calendar.MINUTE, cal_minute );
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	       
		// RadioButton�őI�����ꂽ���ƃ��[�h���擾
	    RadioGroup radioGroupSound = (RadioGroup)findViewById(R.id.radiogroup_sound);
	    selectSound = radioGroupSound.getCheckedRadioButtonId();
	    RadioGroup radioGroupMode = (RadioGroup)findViewById(R.id.radiogroup_mode);
	    selectAlarm = radioGroupMode.getCheckedRadioButtonId();
	    
	    // �L�^�O�ɑO�̃A���[��������
	    removeAlarm();
	    
	    // �A���[���ɓo�^ 
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + millis24hour, getPendingIntent());
	    
	    // ������ۑ� 
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putInt( "cal_hour", cal_hour );
	    editor.putInt( "cal_minute", cal_minute );
	    editor.putInt( "checked_sound", selectSound);
	    editor.putInt( "checked_mode", selectAlarm);
	    editor.putBoolean("alarm_on", true);
	    editor.commit();
	}

	public void removeAlarm(){
       Log.d("AlarmTestActivity", "removeAlarm()");
        
        // �o�^���Ă���A���[�������� 
       alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
       alarmManager.cancel(getPreviousPendingIntent());
	}
	
    private PendingIntent getPendingIntent() {
        // �N������A�v���P�[�V������o�^ 
        // ���ݑI�΂�Ă��郂�[�h
	    RadioGroup radioGroupMode = (RadioGroup)findViewById(R.id.radiogroup_mode);
	    selectAlarm = radioGroupMode.getCheckedRadioButtonId();
	    
	    Intent intent = null;
    	switch(selectAlarm){
    	case R.id.radioButton_mode1:
    		intent = new Intent( getApplicationContext(), AlarmActivity.class );
    		break;
    	case R.id.radioButton_mode2:
    		intent = new Intent( getApplicationContext(), ShakeAlarmActivity.class );
    		break;
    	}
    	RadioGroup radioGroupSound = (RadioGroup)findViewById(R.id.radiogroup_sound);
	    selectSound = radioGroupSound.getCheckedRadioButtonId();
	    intent.putExtra("sound", selectSound);
    	PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
       return pendingIntent;
    }
	
    private PendingIntent getPreviousPendingIntent() {
        // �N������A�v���P�[�V������o�^ 
        // �ݒ肵�����[�h
    	selectAlarm = prefs.getInt("checked_mode", -1);
	    Intent intent = null;
    	switch(selectAlarm){
    	case R.id.radioButton_mode1:
    		intent = new Intent( getApplicationContext(), AlarmActivity.class );
    		break;
    	case R.id.radioButton_mode2:
    		intent = new Intent( getApplicationContext(), ShakeAlarmActivity.class );
    		break;
    	}
    	selectSound = prefs.getInt("checked_sound", -1);
    	intent.putExtra("sound", selectSound);
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
}
