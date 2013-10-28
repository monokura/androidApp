package com.example.httpposttestapp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncPost extends AsyncTask<String, String, String> {
	ProgressDialog dialog;
	Context context;

	public AsyncPost(){}
	
	public AsyncPost(Context context){
		this.context = context;
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			Log.d("DEBUG_HTTP","Start async task!");
			String url =  "http://54.218.126.243:8080/addimage";
			//String url = "http://81.la";
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url); 
			
			Log.d("DEBUG_HTTP","Complete setting!");

			HttpResponse httpResponse = httpClient.execute(httpPost);
			Log.d("DEBUG_HTTP","Complete Transport!");
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				httpResponse.getEntity().writeTo(outputStream);
				Log.d("DEBUG_HTTP", outputStream.toString());
				return (outputStream.toString());
			} else {
				Log.d("DEBUG_HTTP", "response code is error");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.d("DEBUG_HTTP", "ClientProtocol Exception");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("DEBUG_HTTP", "IOException");
		}

		Log.d("DEBUG_HTTP", "error");
		return "error";
	}

	@Override
	protected void onPostExecute(String result) {
		if(dialog != null){
			dialog.dismiss();
		}
	}

	@Override
	protected void onPreExecute() {}  
}