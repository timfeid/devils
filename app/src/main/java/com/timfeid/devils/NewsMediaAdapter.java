package com.timfeid.devils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Tim on 3/19/2018.
 */

public class NewsMediaAdapter extends MediaAdapter implements Listener {
    public NewsMediaAdapter(Activity activity) {
        super(activity);
        Team.getInstance().withNews(this);
    }

    private void parse(String output) {
        try {
            JSONObject mDataset = new JSONObject(output);
            JSONArray items = mDataset.getJSONArray("docs");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                ApiRequest request = new ApiRequest("web-v1.json");
                request.setBaseUrl("https://nhl.bamcontent.com/nhl/id/v1/"+item.getString("asset_id")+"/details/");
                request.addListener(this);

                Thread thread = new Thread(request);
                thread.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof News) {
            News news = (News) observable;
            parse(news.getOutput());
        }

        if (observable instanceof ApiRequest) {
            parse((ApiRequest) observable);
        }
    }

    private void parse(ApiRequest observable) {
        try {
            mediaDataset.addItem(new NewsDataItem(new JSONObject(observable.getOutput())));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
