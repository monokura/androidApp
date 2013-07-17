package com.example.alarmapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ButtonAlarmActivity extends AlarmActivity implements OnClickListener{

	private int counter = 1;

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;
	private Button button7;
	private Button button8;
	private Button button9;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_button_alarm);

		Log.d("ButtonAlarmActivity", "check");
		button1 = (Button)findViewById(R.id.button_nine1);
		button2 = (Button)findViewById(R.id.button_nine2);
		button3 = (Button)findViewById(R.id.button_nine3);
		button4 = (Button)findViewById(R.id.button_nine4);
		button5 = (Button)findViewById(R.id.button_nine5);
		button6 = (Button)findViewById(R.id.button_nine6);
		button7 = (Button)findViewById(R.id.button_nine7);
		button8 = (Button)findViewById(R.id.button_nine8);
		button9 = (Button)findViewById(R.id.button_nine9);
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		button4.setOnClickListener(this);
		button5.setOnClickListener(this);
		button6.setOnClickListener(this);
		button7.setOnClickListener(this);
		button8.setOnClickListener(this);
		button9.setOnClickListener(this);
		Log.d("ButtonAlarmActivity", "check");
		setButton();

		Log.d("ButtonAlarmActivity", "check");
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.button_nine1:
		case R.id.button_nine2:
		case R.id.button_nine3:
		case R.id.button_nine4:
		case R.id.button_nine5:
		case R.id.button_nine6:
		case R.id.button_nine7:
		case R.id.button_nine8:
		case R.id.button_nine9:
			Button button = (Button)findViewById(v.getId());
			int num = Integer.valueOf((String)button.getText());
			
			if(counter == num){
				button.setEnabled(false);
				counter++;
			}else{
				resetButton();
			}
			
			if(counter > 9){
				finish();
			}
		}
	}
	
	private void setButton(){
		int random[] = {1,2,3,4,5,6,7,8,9};
		shuffle(random);

		Log.d("ButtonAlarmActivity", "check");
		button1.setText(String.valueOf(random[0]));
		button2.setText(String.valueOf(random[1]));
		button3.setText(String.valueOf(random[2]));
		button4.setText(String.valueOf(random[3]));
		button5.setText(String.valueOf(random[4]));
		button6.setText(String.valueOf(random[5]));
		button7.setText(String.valueOf(random[6]));
		button8.setText(String.valueOf(random[7]));
		button9.setText(String.valueOf(random[8]));
	}

	private void shuffle(int[] arr){
		for(int i = arr.length-1;i > 0;i--){
			int t = (int)(Math.random()*i);
			
			int tmp = arr[i];
			arr[i] = arr[t];
			arr[t] = tmp;
		}
	}

	private void resetButton(){
		
		button1.setEnabled(true);
		button2.setEnabled(true);
		button3.setEnabled(true);
		button4.setEnabled(true);
		button5.setEnabled(true);
		button6.setEnabled(true);
		button7.setEnabled(true);
		button8.setEnabled(true);
		button9.setEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.button_alarm, menu);
		return true;
	}


}
