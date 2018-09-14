package com.timfeid.devils;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Tim on 3/19/2018.
 * News data item for the media recycler
 */

public class NewsDataItem implements MediaDataset.DataItem, Parcelable {
    private JSONObject item;

    NewsDataItem(JSONObject item) {
        this.item = item;
        try {
            Helpers.d(item.getString("headline"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private NewsDataItem(Parcel in) {
        try {
            this.item = new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static final Creator<NewsDataItem> CREATOR = new Creator<NewsDataItem>() {
        @Override
        public NewsDataItem createFromParcel(Parcel in) {
            return new NewsDataItem(in);
        }

        @Override
        public NewsDataItem[] newArray(int size) {
            return new NewsDataItem[size];
        }
    };

    @Override
    public String getKicker() {
        try {
            return item.getString("headline");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getPlaybackUrl() {
        try {
            return item.getJSONObject("media").getJSONArray("playbacks").getJSONObject(0).getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public String getBlurb() {
        try {
            return Html.fromHtml(item.getString("preview"), Html.FROM_HTML_MODE_COMPACT).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getBody() {
        try {
            return item.getString("body");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public String getImage() {
        try {
            return item.getJSONObject("media").getJSONObject("image").getJSONObject("cuts").getJSONObject("960x540").getString("src");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public View.OnClickListener getListener(final Activity activity) {
        final NewsDataItem that = this;
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), NewsActivity.class);
                intent.putExtra("news", that);
                activity.startActivity(intent);
            }
        };
    }

    @Override
    public BigInteger getId() {
        try {
            return new BigInteger(item.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(item.toString());
    }
}
