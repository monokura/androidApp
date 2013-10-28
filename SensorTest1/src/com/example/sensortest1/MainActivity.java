package com.example.sensortest1;

import java.util.List;

import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
    
    ScrollView outputScroller;
    TextView outputText;

    SensorManager sensorManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        outputScroller = (ScrollView)findViewById(R.id.output_scroller);
        outputText = (TextView)findViewById(R.id.output_text);
        findViewById(R.id.get_button).setOnClickListener(this);
        
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()) {
       case R.id.get_button:
           outputText.setText("");
           List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
           for (Sensor sensor : sensors) {
               Log.i("Info", sensor.toString());
               outputText.append(sensor.toString() + "\n");
           }
       }
        
    }

}
