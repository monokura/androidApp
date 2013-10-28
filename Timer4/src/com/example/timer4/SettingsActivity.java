package com.example.timer4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity implements OnClickListener {
    EditText minutesText;
    Button okButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        minutesText = (EditText)this.findViewById(R.id.minutes);
        okButton = (Button)this.findViewById(R.id.ok_button);
        okButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.ok_button:
            String minutes = minutesText.getText().toString();
            Intent result = new Intent();
            result.putExtra("minutes", minutes);
            setResult(Activity.RESULT_OK, result);
            finish();
            break;
        default:
            assert false : "Unknown button";
        }
    }
}
