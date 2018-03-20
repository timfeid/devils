package com.timfeid.devils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * LiveGameLayout
 * Created by Tim on 2/10/2018.
 */

class LiveGameLayout extends PreviousGameLayout {

    private ScheduledExecutorService timer;
    protected Map<Integer, BoxscoreLayout> boxscores = new ArrayMap<>();

    LiveGameLayout(GameInterface game, View rootView, Activity activity) {
        super(game, rootView, activity);
    }

    void updateWithLiveData(LiveGame game) {
        this.game = game;
        try {
            fill();

            gameDate.setText(game.getCurrentPeriodName());
            gameTime.setText(game.getCurrentPeriodTimeRemaining());
            populateOnIce();
            populateScoringSummary();
            fillBoxscore();
            startTimeCountdown(game);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startTimeCountdown(LiveGame game) throws JSONException {
        if (!game.isTimeRunning()) {
            stopTimeThread();
        } else {
            String timeRemaining = game.getCurrentPeriodTimeRemaining();
            int seconds = Helpers.stringTimeToSeconds(timeRemaining);
            if (seconds <= 0) {
                stopTimeThread();
                return;
            }
            startTimeThread(seconds);
        }
    }

    private void startTimeThread(int time) {
        stopTimeThread();
        timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(new Timer(time), 1, 1200, TimeUnit.MILLISECONDS);
    }

    private void stopTimeThread() {
        if (timer != null) {
            timer.shutdown();
        }
    }

    @Override
    protected void initView() {
        super.initView();
        this.populateBoxscore();

        try {
            if (!game.isPregame() && !game.isFinal()) {
                onIceBox.setVisibility(View.VISIBLE);
            } else {
                onIceBox.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void populateOnIce() throws JSONException {
        if (game.isPregame() || game.isFinal()) {
            return;
        }

        for (String team : teams) {
            LinearLayout layout = (LinearLayout) findViewById(findId(team+"_on_ice"));
            layout.removeAllViews();
            JSONArray players = game.getPlayersOnIce(team);
            for (int i = 0; i < players.length(); i++) {
                int playerId = players.getInt(i);
                createOnIceBoxForPlayer(game.getRosterForTeam(team).getPersonById(playerId), layout);
            }
        }
    }

    private void createOnIceBoxForPlayer(Person person, LinearLayout theLayout) throws JSONException {
        LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(onIceBoxId, null);
        ImageView photo = layout.findViewById(R.id.on_ice_photo);
        TextView name = layout.findViewById(R.id.on_ice_name);

        if (person != null) {
            String scorerText = person.getNumber();
            if (!Objects.equals(person.getPerson().getJSONObject("primaryPosition").getString("code"), "G")) {
                theLayout.addView(layout);
                imageCircleUrl(photo, getImageFor(person.getId()));
                name.setText(scorerText);
            }
        }
    }

    public void fill() throws JSONException {
        threeStarsBox.setVisibility(View.GONE);
        populateLineScore();
        populateScoringSummary();
    }

    class Timer implements Runnable {
        int secondsLeft;
        Timer(int secondsLeft) {
            this.secondsLeft = secondsLeft;
        }
        @Override
        public void run() {
            if (--secondsLeft < 0) {
                secondsLeft = 0;
            }
            final int secondsLeft = this.secondsLeft;
            Helpers.d(Helpers.secondsToString(secondsLeft));
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameTime.setText(Helpers.secondsToString(secondsLeft));
                }
            });
        }
    }
    void fillBoxscore() throws JSONException {

        for (Map.Entry<Integer, BoxscoreLayout> entry : boxscores.entrySet()) {
            BoxscoreLayout layout = entry.getValue();
            switch (entry.getKey()) {
                case R.string.shots:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getShots()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getShots()));
                    break;
                case R.string.pim:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getPim()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getPim()));
                    break;
                case R.string.pp_ops:
                    layout.setHome(String.format(Locale.US, "%d/%d",
                            game.getBoxscore("home").getPowerPlayGoals(),
                            game.getBoxscore("home").getPowerPlayOpportunities()));
                    layout.setAway(String.format(Locale.US, "%d/%d",
                            game.getBoxscore("away").getPowerPlayGoals(),
                            game.getBoxscore("away").getPowerPlayOpportunities()));
                    break;
                case R.string.hits:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getHits()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getHits()));
                    break;
                case R.string.blocks:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getBlocked()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getBlocked()));
                    break;
                case R.string.faceoff_p:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getFaceOffWinPercentage()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getFaceOffWinPercentage()));
                    break;
                case R.string.giveaways:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getGiveaways()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getGiveaways()));
                    break;
                case R.string.takeaways:
                    layout.setHome(String.format(Locale.US, "%d", game.getBoxscore("home").getTakeaways()));
                    layout.setAway(String.format(Locale.US, "%d", game.getBoxscore("away").getTakeaways()));
                    break;
            }
        }

    }

    protected void populateBoxscore() {
        LinearLayout boxscore = (LinearLayout) findViewById(R.id.boxscore);
        findViewById(R.id.boxscore_box).setVisibility(View.VISIBLE);

        View line = new View(getActivity());
        for (Integer statResource : boxscoreStats) {
            BoxscoreLayout stat = new BoxscoreLayout(getActivity());
            stat.setStat(getResources().getString(statResource));
            boxscores.put(statResource, stat);
            line = new View(getActivity());
            line.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.transparentMax));
            line.setMinimumHeight(2);
            boxscore.addView(stat);
            boxscore.addView(line);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)line.getLayoutParams();
            params.setMargins(0, 10, 0, 10); //substitute parameters for left, top, right, bottom
            line.setLayoutParams(params);
        }
        boxscore.removeView(line);
    }

    protected class BoxscoreLayout extends LinearLayout {

        TextView stat;
        TextView home;
        TextView away;

        public BoxscoreLayout(Context context) {
            super(context);
            setWeightSum(3);
            home = new TextView(context);
            stat = new TextView(context);
            away = new TextView(context);
            setPadding(0,5,0,5);
            addView(home);
            addView(stat);
            addView(away);
            int white = ContextCompat.getColor(getContext(), R.color.colorAccent);
            home.setTextColor(white);
            stat.setTextColor(ContextCompat.getColor(getContext(), R.color.transparentIsh));
//            stat.setTypeface(stat.getTypeface(), Typeface.BOLD);
            away.setTextColor(white);
            home.setTextSize(16);
            stat.setTextSize(16);
            away.setTextSize(16);
            home.setAllCaps(true);
            stat.setAllCaps(true);
            away.setAllCaps(true);
            home.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            away.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            stat.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f));
            stat.setGravity(Gravity.CENTER);
            away.setGravity(Gravity.END);
//            LayoutParams params = (LayoutParams) stat.getLayoutParams();
//            params.width = ViewGroup.LayoutParams.FILL_PARENT;
//            stat.setLayoutParams(params);
        }

        public BoxscoreLayout(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public BoxscoreLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        public BoxscoreLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        public void setHome(String text) {
            home.setText(text);
        }

        public void setAway(String text) {
            away.setText(text);
        }

        public void setStat(String text) {
            stat.setText(text);
        }
    }

}