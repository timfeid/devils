package com.timfeid.devils;

import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 2/14/2018.
 */

public class GameContent {
    private JSONObject content;
    private static Map<Integer, Getter> map = new ArrayMap<>();
    GameContent(JSONObject content) {
        this.content = content;
    }

    public static Getter forGame(int id) {
        if (map.containsKey(id)) {
            return map.get(id);
        }

        Getter value = new Getter(id);
        map.put(id, value);

        return value;
    }

    public Content findHighlightById(int id) throws JSONException {
        JSONArray highlights = this.content.getJSONObject("highlights")
                .getJSONObject("scoreboard").getJSONArray("items");
        for (int i = 0; i < highlights.length(); i++) {
            JSONObject highlight = highlights.getJSONObject(i);
            JSONArray keywords = highlight.getJSONArray("keywords");
            for (int j = 0; j < keywords.length(); j++) {
                JSONObject keyword = keywords.getJSONObject(j);
                if (keyword.getString("type").equals("statsEventId")
                        && Integer.parseInt(keyword.getString("value")) == id
                        && highlight.getString("type").equals("video")) {
                    return new Content(highlight);
                }
            }
        }

        return null;
    }

    public class Content {
        private static final String TAG = "GAMECONTENT.CONTENT";

        JSONObject highlight;
        public Content(JSONObject highlight) {
            this.highlight = highlight;
        }


        public String getMobileUrl() {
            try {
                return highlight.getJSONArray("playbacks").getJSONObject(0).getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "http://md-akc.med.nhl.com/hls/nhl/2018/01/31/e14b836f-1f45-401d-8a16-7bd6a72494d5/1517365281979/master_mobile.m3u8";
        }
    }

    public static class Getter extends Observable implements Listener {
        private GameContent contents;
        private Getter(int gameId) {
            ApiRequest request = new ApiRequest("game/"+gameId+"/content");
            request.addListener(this);

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(request, 0, 120, TimeUnit.SECONDS);
        }

        public GameContent getContents() {
            return contents;
        }

        private void handle(ApiRequest getter) {
            try {
                this.contents = new GameContent(new JSONObject(getter.getOutput()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handle(Observable observable) {
            if (observable instanceof ApiRequest) {
                handle((ApiRequest) observable);
            }
        }
    }
}
