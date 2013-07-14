package com.example.alarmapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class AlarmActivity extends Activity implements OnClickListener{
	private SoundPool soundPool;
	private int bell;
    private Button startButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		// Show the Up button in the action bar.
		setupActionBar();

		startButton = (Button) this.findViewById(R.id.alarm_button);
        startButton.setOnClickListener(this);

        // sound“Ç‚Ýž‚Ý
		soundPool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
		bell = soundPool.load(this, R.raw.bell, 1);
		// sound‚ª“Ç‚Ýž‚Ü‚ê‚½Žž‚Ìˆ—
        soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (0 == status) {
                    Toast.makeText(getApplicationContext(), "LoadComplete", Toast.LENGTH_LONG).show();
                    soundPool.play(bell, 1.0F, 1.0F, 0, -1, 1.0F);
                }
            }
        });
        
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		soundPool.release();
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.alarm_button:
			soundPool.release();
			finish();
			break;
		}
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
