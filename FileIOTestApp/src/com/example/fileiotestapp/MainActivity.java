package com.example.fileiotestapp;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String fileName1 = "sample1.txt";
		String saveText1 = "サンプル1";

		Button btn1 = (Button) findViewById(R.id.button1);
		Button btn2 = (Button) findViewById(R.id.button2);

		btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tempSave();
			}
		});

		btn2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				tempLoad();
			}
		});
		
		Log.d("hoge", this.getFilesDir().getPath());

		// ファイル書き込み
		FileUtils.writeStringFile(this, saveText1, fileName1);
		// ファイル読み込み
		String str1 = FileUtils.readStringFile(this, fileName1);
		// TextViewに読み込んだ文字列を設定
		((TextView) findViewById(R.id.textView2)).setText(str1);

	}

	private void tempSave() {
		String fileName2 = "sample2.txt";
		String saveText2 = "サンプル2";
		String bitmap2 = "test.jpg";

		ImageView img = (ImageView)findViewById(R.id.imageView1);
		Bitmap bmp = ((BitmapDrawable)img.getDrawable()).getBitmap();
		
		// ファイル書き込み
		FileUtils.writeStringFile(this, saveText2, fileName2);
		FileUtils.writeBitmapFile(this, bmp, bitmap2);
	}

	private void tempLoad() {
		String fileName2 = "sample2.txt";
		String bitmap2 = "test.jpg";

		// ファイルパス生成
		File bitmapFile = new File(this.getFilesDir().getPath() + File.separator
				+ "test.jpg");
		Uri uri = Uri.fromFile(bitmapFile);

		Log.d("hogehoge", uri.getPath());
		
		
		// ファイル読み込み
		String str2 = FileUtils.readStringFile(this, fileName2);
		Bitmap bmp = FileUtils.readBitmapFile(this, bitmap2);
		// TextViewに読み込んだ文字列を設定
		((TextView) findViewById(R.id.textView3)).setText(str2);
		ImageView imgView1 = (ImageView)findViewById(R.id.imageView2);
		imgView1.setImageBitmap(bmp);
		ImageView imgView2 = (ImageView)findViewById(R.id.imageView3);
		imgView2.setImageURI(uri);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
