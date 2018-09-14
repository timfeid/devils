package com.timfeid.devils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewsActivity extends AppCompatActivity {
    protected NewsDataItem dataItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dataItem = getIntent().getParcelableExtra("news");
        actionBar.setTitle(dataItem.getKicker());

//        WebView wv = findViewById(R.id.webview);
//        wv.loadData(dataItem.getBody(), "text/html; charset=utf-8", "UTF-8");
        WebView wv = findViewById(R.id.webview);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new NewsActivity.WebAppInterface(this), "webConnector");
        wv.loadUrl("file:///android_asset/player-stats/index.html");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String newsId() {
            Helpers.d(dataItem.getId().toString());
            return dataItem.getId().toString();
        }

        @JavascriptInterface
        public String component() {
            return "news";
        }

        @JavascriptInterface
        public void viewVideo(String videoId) {
            Helpers.d(videoId);

            Intent intent = new Intent(Intent.ACTION_VIEW );
            intent.setDataAndType(Uri.parse(dataItem.getPlaybackUrl()), "video/*");
            startActivity(intent);
        }
    }
}
