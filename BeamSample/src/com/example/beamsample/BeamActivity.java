package com.example.beamsample;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BeamActivity extends Activity implements CreateNdefMessageCallback,OnNdefPushCompleteCallback{

	NfcAdapter mNfcAdapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beam);
		
		// Check for available NFC Adapter
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
	    if (mNfcAdapter == null) {
	        Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
	        finish();
	        return;
	    }
	    // Register callback
	    mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
	    mNfcAdapter.setNdefPushMessageCallback(this, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beam, menu);
		return true;
	}


	@Override
    public NdefMessage createNdefMessage(NfcEvent event) {
		// 送信するNdef(NFC Data exchange format)を作成する部分
		// return されたものが送信される
        TextView textViewName = (TextView)findViewById(R.id.textView_send_name);
        TextView textViewAge = (TextView)findViewById(R.id.textView_send_age);
		String name = textViewName.getText().toString();
		String age  = textViewAge.getText().toString();
		
		Resources r = getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(r, R.drawable.ic_launcher);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
		NdefMessage msg = new NdefMessage(
                new NdefRecord[] {
                		createMimeRecord("application/com.example.beamsample", name.getBytes()),
                		createMimeRecord("application/com.example.beamsample",  age.getBytes()),
                		createMimeRecord("application/com.example.beamsample",  data)
        });
        return msg;
    }
	
    @Override
    public void onNewIntent(Intent intent) {
        // インテントを処理するために、この後でonResumeが呼ばれる
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // アクティビティがAndroid Beamによって開始されたことをチェックする
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
        	// ビーム後、受信側の処理
        	processIntent(getIntent());
        }
    }

    /**
     * インテントからのNDEFメッセージのパースとTextViewへの表示
     */
    void processIntent(Intent intent) {
        TextView textViewName = (TextView) findViewById(R.id.textView_receive_name);
        TextView textViewAge = (TextView) findViewById(R.id.textView_receive_age);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // ビームの送信中は一つだけのメッセージ
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        textViewName.setText(new String(msg.getRecords()[0].getPayload()));
        textViewAge.setText(new String(msg.getRecords()[1].getPayload()));
        
        // 画像をエンコードして設定
        byte[] data = msg.getRecords()[2].getPayload();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        ImageView imageView = (ImageView)findViewById(R.id.imageView_receive);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {
    	// ビーム後、送信側で行われる処理
    }
    
    /**
     * NDEFレコード内にカスタムMIMEタイプをカプセル化して生成する
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
}
