package com.example.nwtest1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

    TextView outputText;
    EditText urlInput;
    ProgressDialog dlDialog;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView) this.findViewById(R.id.output_text);
        urlInput = (EditText) this.findViewById(R.id.url_input);
        this.findViewById(R.id.get_button).setOnClickListener(this);
        this.findViewById(R.id.clear_button).setOnClickListener(this);

        dlDialog = new ProgressDialog(this);
        dlDialog.setTitle("Downloading");
        dlDialog.setCancelable(false);

        handler = new Handler();
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
            ConnectivityManager connMgr = (ConnectivityManager) this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = connMgr.getActiveNetworkInfo();
            if (ni != null && ni.isConnected()) {
                final String url = urlInput.getText().toString();
                dlDialog.setMessage("Downloading contents from " + url);
                dlDialog.show();
                new DLTask(url).start();
            }
            else
                Toast.makeText(this, "No network connection", Toast.LENGTH_LONG)
                        .show();
            break;
        case R.id.clear_button:
            outputText.setText("");
            break;
        }
    }

    private class DLTask extends Thread {
        String url;

        public DLTask(String url) {
            this.url = url;
        }

        public void run() {
            String result1;
            try {
                result1 = download(url);
            }
            catch (IOException e) {
                result1 = "Unable to retrieve content. URL may be invalid.";
            }
            final String result = result1;
            handler.post(new Runnable() {
                public void run() {
                    dlDialog.dismiss();
                    outputText.setText(result);

                }
            });
        }

        private String download(String url) throws IOException {
            InputStream instream = null;
            try {
                URLConnection uconn = new URL(url).openConnection();
                if (!(uconn instanceof HttpURLConnection))
                    throw new IOException("Invalid protocol");
                HttpURLConnection conn = (HttpURLConnection) uconn;
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                int resCode = conn.getResponseCode();
                if (resCode == 200) {
                    instream = conn.getInputStream();
                    return getContent(instream);
                }
                else {
                    String resMesg = conn.getResponseMessage();
                    return "HTTP Response: " + resCode
                            + (resMesg != null ? " " + resMesg : "");
                }
            }
            finally {
                if (instream != null)
                    instream.close();
            }
        }

        private String getContent(InputStream is) throws IOException {
            final int BUFFLEN = 256;
            Reader reader = new InputStreamReader(is, "UTF-8");
            char[] cbuff = new char[BUFFLEN];
            StringBuffer sb = new StringBuffer();
            int n;
            while ((n = reader.read(cbuff)) > 0)
                sb.append(cbuff, 0, n);
            return sb.toString();
        }
    }
}
