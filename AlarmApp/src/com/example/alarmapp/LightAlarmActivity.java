package com.example.alarmapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class LightAlarmActivity extends AlarmActivity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	
	private final float mLightMin = 900.0f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_light_alarm);
		
		// �Z���T�[�}�l�[�W������
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// �Z���T�[�̎擾
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mSensor != null) {
        	// �Z���T�[�ւ̃C�x���g���X�i�[��ݒ�
        	mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// �Z���T�[�ւ̃C�x���g���X�i�[�̉���
		if(mSensorManager != null){
			mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.light_alarm, menu);
		return true;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// �l�ύX���̏���
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
        	float light = event.values[0];
        	TextView text = (TextView)findViewById(R.id.text_light);
        	text.setText(String.valueOf(light));
    		if(light >= mLightMin){
    			finish();
    		}
        }
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// nothing to do
	}
}
