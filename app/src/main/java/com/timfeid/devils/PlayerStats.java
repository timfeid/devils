package com.timfeid.devils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Tim on 2/6/2018.
 * Get player stats from the NHL api
 */

public class PlayerStats extends Observable implements Listener {
    private List<Player> players = new ArrayList<>();
    private boolean complete = false;
    private JSONArray roster;

    public PlayerStats() {
        ApiRequest request = new ApiRequest("teams/1");
        request.addParam("hydrate", "franchise(roster(season=20172018,person(name,stats(splits=[yearByYear]))))");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }

    public void handle(Runnable runner) {
        ApiRequest request = (ApiRequest) runner;

        Log.d("GETTING_URL", "DONE WITH API REQUEST IN NEXTGAME");
        parseObject(request.getOutput());
        notifyListeners();
        complete = true;
    }

    private void parseObject(String output) {
        try {
            roster = new JSONObject(output)
                    .getJSONArray("teams")
                    .getJSONObject(0)
                    .getJSONObject("franchise")
                    .getJSONObject("roster")
                    .getJSONArray("roster");
            for (int i = 0; i < roster.length(); i++) {
                players.add(new Player(roster.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    Roster getRoster() {
        return new Roster(roster);
    }

    public boolean done() {
        return complete;
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof ObservableThread) {
            handle((Runnable) observable);
        }
    }
}
