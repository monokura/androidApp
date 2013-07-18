package com.example.alarmapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class ShakeAlarmActivity extends AlarmActivity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private int mShakeCounter = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shake_alarm);
		
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		
		if(savedInstanceState != null){
			mShakeCounter = savedInstanceState.getInt("mShakeCounter");
		}
		
		TextView numText = (TextView)findViewById(R.id.shake_num);
		numText.setText(String.valueOf(mShakeCounter));
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// センサーの取得
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (mSensor != null) {
        	// センサーへのイベントリスナーを設定
        	mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// センサーへのイベントリスナーの解除
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
		// 精度変更時の処理
		// nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// 値変更時の処理
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
    		if(shakeDetector(event)){
    			//Toast.makeText(this, "shake", Toast.LENGTH_SHORT).show();
        		
    			mShakeCounter--;
    			TextView numText = (TextView)findViewById(R.id.shake_num);
    			numText.setText(String.valueOf(mShakeCounter));
    		}
    		if(mShakeCounter <= 0){
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
        
		// 傾き（ハイカット）
        lowValues[0] = values[0] * 0.1f + lowValues[0] * (1.0f - 0.1f);
        lowValues[1] = values[1] * 0.1f + lowValues[1] * (1.0f - 0.1f);
        lowValues[2] = values[2] * 0.1f + lowValues[2] * (1.0f - 0.1f);
        // 加速度（ローカット）
        highValues[0] = values[0] - lowValues[0];
        highValues[1] = values[1] - lowValues[1];
        highValues[2] = values[2] - lowValues[2];
        
        accelLowValuesText.setText(convertFloatsToString(highValues));
        
        // 振動判定　絶対値（あるいは２乗の平方根）の合計と比較
        //　float shakeValue = 
        //	Math.abs(highValues[0]) + 
        //	Math.abs(highValues[1]) +
        //	Math.abs(highValues[2]);
       
        // 振動判定　ベクトルの長さと比較
        double shakeValue = Math.sqrt(
        		Math.pow(highValues[0],2) + 
        		Math.pow(highValues[1],2) +
        		Math.pow(highValues[2],2));
        
        // しきい値（適当）を超えるとShakeを感知
        if(shakeValue > 18.0f){
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
