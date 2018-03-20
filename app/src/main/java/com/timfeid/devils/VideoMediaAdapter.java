package com.timfeid.devils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;

/**
 * Created by Tim on 3/19/2018.
 * Video media adapter for media fragment
 */

public class VideoMediaAdapter extends MediaAdapter implements Listener {
    VideoMediaAdapter(Activity activity) {
        super(activity);
        Team.getInstance().withVideos(this);
    }

    private void parse(String output) {
        try {
            JSONObject mDataset = new JSONObject(output);
            JSONArray items = mDataset.getJSONArray("docs");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                mediaDataset.addItem(new VideoDataItem(item));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof Videos) {
            Videos videos = (Videos) observable;
            parse(videos.getOutput());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }

        if (observable instanceof ApiRequest) {
            final ApiRequest video = (ApiRequest) observable;
            try {
                final JSONObject videoInfo = new JSONObject(video.getOutput());
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Intent.ACTION_VIEW );
                        try {
                            intent.setDataAndType(Uri.parse(videoInfo.getJSONArray("playbacks").getJSONObject(0).getString("url")), "video/*");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        activity.startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class VideoDataItem implements MediaDataset.DataItem {
        JSONObject item;

        VideoDataItem(JSONObject item) {
            this.item = item;
        }

        @Override
        public String getKicker() {
            try {
                return item.getString("title");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        public String getBlurb() {
            try {
                return item.getString("blurb");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        public String getImage() {
            try {
                return item.getJSONObject("image").getJSONObject("cuts").getJSONObject("960x540").getString("src");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        public BigInteger getId() {
            try {
                return new BigInteger(item.getString("asset_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public View.OnClickListener getListener(final Activity activity) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        showVideo(item.getString("asset_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
    }

    private void showVideo(String id) {
//        https://nhl.bamcontent.com/nhl/id/v1/58912103/details/web-v1.json
        ApiRequest request = new ApiRequest("web-v1.json");
        request.setBaseUrl("https://nhl.bamcontent.com/nhl/id/v1/"+id+"/details/");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }
}
