package com.timfeid.devils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tim on 2/10/2018.
 */

public class LiveGameThread extends Observable implements Runnable, Listener {
    private int gameId;
    private LiveGame liveGame;
    private Schedule schedule = null;
    private String output = null;

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    @Override
    public void run() {
        Team.getInstance().withSchedule(this);

        ApiRequest request = new ApiRequest("game/"+gameId+"/feed/live");
        request.addListener(this);

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(request, 0, 15, TimeUnit.SECONDS);
    }

    public LiveGame getGame() {
        try {
            JSONObject obj = new JSONObject(output);
            int gameId = LiveGame.findId(obj);

            return new LiveGame(schedule.findGameById(gameId), obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void handle(Schedule schedule) {
        this.schedule = schedule;
        if (output != null) {
            this.notifyListeners();
        }
    }

    public void handle(Runnable runner) {
        ApiRequest myRequest = (ApiRequest) runner;
        output = myRequest.getOutput();
        if (schedule != null) {
            notifyListeners();
        }
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof Schedule) {
            handle((Schedule) observable);
        }

        if (observable instanceof Runnable) {
            handle((Runnable) observable);
        }
    }
}
