package com.timfeid.devils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlayerActivity extends AppCompatActivity implements Listener {
    private Person person;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int personId = getIntent().getIntExtra("personId", 0);
        PersonGetter personGetter = new PersonGetter();
        personGetter.setPersonId(personId);
        personGetter.addListener(this);
        Thread thread = new Thread(personGetter);
        thread.start();
    }

    private void handle(PersonGetter personGetter) {
        person = personGetter.getPerson();

        final Context ctx = this;

        runOnUiThread(new Runnable() {
            @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
            @Override
            public void run() {
                ImageView actionShot = findViewById(R.id.action_photo);
                ImageView photo = findViewById(R.id.player_photo);
                TextView name = findViewById(R.id.name);
                TextView info = findViewById(R.id.info);
                Transformation circle = new CircleTransform();
                TextView birthDate = findViewById(R.id.birthday);
                TextView birthPlace = findViewById(R.id.birthplace);
                TextView shoots = findViewById(R.id.shoots);
                TextView draft = findViewById(R.id.draft);

                WebView wv = findViewById(R.id.webview);
                WebSettings webSettings = wv.getSettings();
                webSettings.setJavaScriptEnabled(true);
                wv.addJavascriptInterface(new WebAppInterface(ctx), "webConnector");
                wv.loadUrl("file:///android_asset/player-stats/index.html");

                try {
                    getSupportActionBar().setTitle(String.format(Locale.US, "#%s %s", person.getNumber(), person.getFullName()));
                    Picasso.get().load(person.getActionPhotoUrl()).into(actionShot);
                    Picasso.get().load(person.getImageUrl()).transform(circle).into(photo);
                    name.setText(String.format(Locale.US, "%s | #%s", person.getFullName(), person.getNumber()));
                    info.setText(String.format(Locale.US, "%s   |   %s   |   %slbs   |   Age: %s",
                            person.getPositionAbbreviation(),
                            person.getHeight(),
                            person.getWeight(),
                            person.getAge()));

                    SpannableStringBuilder birthday = new SpannableStringBuilder("Born: " + person.getBirthday());
                    birthday.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, "Born: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableStringBuilder place = new SpannableStringBuilder("Birthplace: " + person.getBirthPlace());
                    place.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, "Birthplace: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableStringBuilder shootsStr = new SpannableStringBuilder("Shoots: " + person.getShootsCatches());
                    shootsStr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, "Shoots: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableStringBuilder draftStr = new SpannableStringBuilder("Draft: " + person.getDraft());
                    draftStr.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, "Draft: ".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    birthDate.setText(birthday);
                    birthPlace.setText(place);
                    shoots.setText(shootsStr);
                    draft.setText(draftStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof PersonGetter) {
            handle((PersonGetter) observable);
        }
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public int load() {
            return getIntent().getIntExtra("personId", 0);
        }

        @JavascriptInterface
        public String component() {
            return "player-stats";
        }
    }
}
