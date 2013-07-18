package com.example.alarmapp;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VoiceAlarmActivity extends AlarmActivity{

    private static final int REQUEST_CODE = 0;
	private String answer = "おはようございます";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_alarm);
		
		inputVoice();
	}
	


	protected void inputVoice(){
		Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // ACTION_WEB_SEARCH
        intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "「おはようございます」と言ってください"); 
        
        // インテント発行
        startActivityForResult(intent, REQUEST_CODE);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voice_alarm, menu);
		return true;
	}	
	
	// アクティビティ終了時に呼び出される
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 自分が投げたインテントであれば応答する
    	boolean successFlag = false;

    	if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

    		// 結果文字列リスト
    		ArrayList<String> results = data.getStringArrayListExtra(
    				RecognizerIntent.EXTRA_RESULTS);

    		for (int i = 0; i< results.size(); i++) {

    			Log.d("VoiceInput", results.get(i));
    			if(answer.equals(results.get(i))){
    				successFlag = true;
    				break;
    			}
    		}

    	}

    	super.onActivityResult(requestCode, resultCode, data);

    	if(successFlag){
    		finish();
    	}else{
    		inputVoice();
    	}
    }
}
