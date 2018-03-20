package com.timfeid.devils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONException;

public class HomeActivity extends HamburgerActivity implements Listener {
    private static final String TAG = "HomeActivity";
    private Game game;
    private LiveGameLayout liveGameLayout;
    private View homeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeLayout = findViewById(R.id.home_layout);
        Team.getInstance().withSchedule(this);
    }

    private void doneLoading() {
        findViewById(R.id.loading).setVisibility(View.GONE);
    }

    private void upcomingGame(final Activity activity) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doneLoading();
                new UpcomingGameLayout(game, homeLayout, activity).build();
            }
        });
    }

    private void liveGame(final Activity activity) throws JSONException {
        LiveGameThread liveGameThread = new LiveGameThread();
        liveGameThread.addListener(this);
        liveGameThread.setGameId(game.getId());
        Thread gameThread = new Thread(liveGameThread);
        gameThread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doneLoading();
                homeLayout = findViewById(R.id.home_layout);
                liveGameLayout = new LiveGameLayout(game, homeLayout, activity);
                liveGameLayout.build();
            }
        });
    }

    public void handle(Schedule schedule) {
        game = schedule.getNextGame();

        try {
            if (game.isGameLive()) {
                liveGame(this);
            } else {
                upcomingGame(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handle(final LiveGame game) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                liveGameLayout.updateWithLiveData(game);
            }
        });
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof LiveGameThread) {
            LiveGameThread t = (LiveGameThread) observable;
            handle(t.getGame());
        }

        if (observable instanceof Schedule) {
            handle((Schedule) observable);
        }
    }
}
