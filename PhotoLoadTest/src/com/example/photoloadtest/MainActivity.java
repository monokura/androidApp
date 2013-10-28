package com.example.photoloadtest;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	final static protected int REQUEST_CODE_CAMERA  = 0x00000001;
	final static protected int REQUEST_CODE_GALLERY = 0x00000002;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picture(v);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void picture(View v){
	    //選択項目を準備する。
	    String[] str_items = {"カメラで撮影", "ギャラリーから選択", "キャンセル"};
	    new AlertDialog.Builder(MainActivity.this)
	        .setTitle("選択")
	        .setItems(str_items, new DialogInterface.OnClickListener(){
	            public void onClick(DialogInterface dialog, int which) {
	                switch(which){
	                case 0:
	                    wakeupCamera(); // カメラ起動
	                    break;
	                case 1:
	                    wakeupGallery(); // ギャラリー起動
	                    break;
	                default:
	                    // カメラ・ギャラリー以外(キャンセル)を想定
	                    break;
	                }
	            }
	        }
	    ).show();
	}

	File bitmapFile;
	Uri bitmapUri;
	
	protected void wakeupCamera(){
        Log.d("TAG", "Wake up Camera");
	    File mediaStorageDir = new File(
	        Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES
	        ), "PictureSaveDir"
	    );
	    Log.d("DEBUG_CAMERA", mediaStorageDir.getPath());
	    if(! mediaStorageDir.exists()) {
	    	// ファイルが存在しない場合
	    	Log.d("DEBUG_CAMERA", "File is not exist!");
	    	if(! mediaStorageDir.mkdir()){
		    	Log.d("TAG", "Failed to create directory");
		        if(isExternalStorageWritable()){
		        	Log.d("DEBUG_CAMERA","Writable!");
		        }
		        if(isExternalStorageReadable()){
		        	Log.d("DEBUG_CAMERA","Readable!");
		        }
		        return;
		    }
	    }
	    
	    Log.d("TAG", "Success to create directory");
	    
	    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	    bitmapFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".JPG");
	    bitmapUri = Uri.fromFile(bitmapFile);
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, bitmapUri);
	    startActivityForResult(intent, REQUEST_CODE_CAMERA);
	}
	
	protected void wakeupGallery() {
	    Intent intent = new Intent();
	    intent.setType("image/*");
	    intent.setAction(Intent.ACTION_GET_CONTENT);
	    startActivityForResult(intent, REQUEST_CODE_GALLERY);
	}
	
	Bitmap bitmap;
	
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
	    if(RESULT_OK == resultCode){
	        if(null != bitmap){
	            bitmap.recycle();
	        }
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inSampleSize = 4;
	        switch(requestCode){
	            case REQUEST_CODE_CAMERA:
	            	Log.d("DEBUG_CAMERA", "Get Image from Camera!");
	            	bitmap = BitmapFactory.decodeFile(bitmapUri.getPath(), options);
	                // 撮影した画像をギャラリーのインデックスに追加されるようにスキャンさせる
	                String[] paths = {bitmapUri.getPath()};
	                String[] mimeTypes = {"image/*"};
	                MediaScannerConnection.scanFile(
	                    getApplicationContext(), paths, mimeTypes, new OnScanCompletedListener() {
	                        @Override
	                        public void onScanCompleted(String path, Uri uri) {
	                        }
	                    });
	                break;
	            case REQUEST_CODE_GALLERY:
	                try{
	                    ContentResolver cr = getContentResolver();
	                    String[] columns = { MediaStore.Images.Media.DATA };
	                    Cursor c = cr.query(data.getData(), columns, null, null, null);
	                    c.moveToFirst();
	                    bitmapUri = Uri.fromFile(new File(c.getString(0)));
	                    InputStream is = getContentResolver().openInputStream(data.getData());
	                    bitmap = BitmapFactory.decodeStream(is, null, options);
	                    is.close();
	                }catch(Exception e){
	                    e.printStackTrace();
	                }
	                break;
	        }
	        ImageView preview = (ImageView) findViewById(R.id.imageView1);
	        preview.setImageBitmap(bitmap);
	    }
	}
	
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}

	/* Checks if external storage is available to at least read */
	public boolean isExternalStorageReadable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state) ||
	        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
}
