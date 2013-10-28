package com.example.jsonexample;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	private Button btn = null;
	private TextView tv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
	    btn = (Button)findViewById(R.id.btn1);
	    tv = (TextView)findViewById(R.id.tv1);

	    btn.setOnClickListener(this);
		
	}
	
	  @Override
	  public void onClick(View v) {
	    // �{�^��������
	    if( v == btn )
	    {
	    	Log.d("push","push���܂���");
	      exec_post();
	    }
	  }
	  
	  // POST�ʐM�����s�iAsyncTask�ɂ��񓯊��������g���o�[�W�����j
	  private void exec_post() {

	    // �񓯊��^�X�N���`
	    HttpPostTask task = new HttpPostTask(this,"http://10.0.2.2:3000/login_post",
	      // �^�X�N�������ɌĂ΂��UI�̃n���h��
	      new HttpPostHandler(){

	        @Override
	        public void onPostCompleted(String response) {
	          // ��M���ʂ�UI�ɕ\��
	          tv.setText( response );
	        }

	        @Override
	        public void onPostFailed(String response) {
	          tv.setText( response );
	          Toast.makeText(
	            getApplicationContext(), 
	            "�G���[���������܂����B", 
	            Toast.LENGTH_LONG
	          ).show();
	        }
	      }
	    );
	    task.addPostParam( "post_1", "���[�UID" );
	    task.addPostParam( "post_2", "�p�X���[�h" );

	    // �^�X�N���J�n
	    task.execute();

	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
