package com.example.alarmapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeAlarmActivity extends Activity implements SensorEventListener{
	private SoundPool soundPool;
	private int bell;
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mShakeCounter = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake_alarm);

		// sound�ǂݍ���
		soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
		bell = soundPool.load(this, R.raw.bell, 1);
		// sound���ǂݍ��܂ꂽ���̏���
		soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if (0 == status) {
						Toast.makeText(getApplicationContext(), "LoadComplete", Toast.LENGTH_LONG).show();
						soundPool.play(bell, 1.0F, 1.0F, 0, -1, 1.0F);
				}
			}
		});
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		if(savedInstanceState != null){
			mShakeCounter = savedInstanceState.getInt("mShakeCounter");
		}
		
		TextView numText = (TextView)findViewById(R.id.shake_num);
		numText.setText(String.valueOf(mShakeCounter));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// �Z���T�[�̎擾
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensor != null) {
        	// �Z���T�[�ւ̃C�x���g���X�i�[��ݒ�
        	mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		// �Z���T�[�ւ̃C�x���g���X�i�[�̉���
		if(mSensorManager != null){
			mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
        outState.putInt("mShakeCounter", mShakeCounter);
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.shake_alarm, menu);
		return true;
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// ���x�ύX���̏���
		// nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// �l�ύX���̏���
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
    		if(shakeDetector(event)){
    			Toast.makeText(this, "shake", Toast.LENGTH_SHORT).show();
        		
    			mShakeCounter--;
    			TextView numText = (TextView)findViewById(R.id.shake_num);
    			numText.setText(String.valueOf(mShakeCounter));
    		}
    		if(mShakeCounter <= 0){
    			soundPool.release();
    			finish();
    		}
        }
	}
	

    private float[] lowValues = {0.0f, 0.0f, 0.0f};
    private float[] highValues = {0.0f, 0.0f, 0.0f};
	
	public boolean shakeDetector(SensorEvent event){
		float[] values = new float[3];
		values[0] = event.values[0];
		values[1] = event.values[1];
		values[2] = event.values[2];
		
		TextView accelValuesText = (TextView)findViewById(R.id.shake_values);
		TextView accelLowValuesText = (TextView)findViewById(R.id.shake_values_low);
		accelValuesText.setText(convertFloatsToString(values));
        
		// �X���i�n�C�J�b�g�j
        lowValues[0] = values[0] * 0.1f + lowValues[0] * (1.0f - 0.1f);
        lowValues[1] = values[1] * 0.1f + lowValues[1] * (1.0f - 0.1f);
        lowValues[2] = values[2] * 0.1f + lowValues[2] * (1.0f - 0.1f);
        // �����x�i���[�J�b�g�j
        highValues[0] = values[0] - lowValues[0];
        highValues[1] = values[1] - lowValues[1];
        highValues[2] = values[2] - lowValues[2];
        
        accelLowValuesText.setText(convertFloatsToString(highValues));
        
        // �U������@��Βl�i���邢�͂Q��̕������j�̍��v�Ɣ�r
        //�@float shakeValue = 
        //	Math.abs(highValues[0]) + 
        //	Math.abs(highValues[1]) +
        //	Math.abs(highValues[2]);
       
        // �U������@�x�N�g���̒����Ɣ�r
        double shakeValue = Math.sqrt(
        		Math.pow(highValues[0],2) + 
        		Math.pow(highValues[1],2) +
        		Math.pow(highValues[2],2));
        
        // �������l�i�K���j�𒴂����Shake�����m
        if(shakeValue > 15.0f){
        	return true;
        }else{
        	return false;
        }
	}

	private String convertFloatsToString(float[] values) {
        return 
        String.valueOf(values[0]) + ", " + 
        String.valueOf(values[1]) + ", " + 
        String.valueOf(values[2]);
    }
}
