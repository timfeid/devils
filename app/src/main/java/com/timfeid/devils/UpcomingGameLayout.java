package com.timfeid.devils;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Tim on 2/10/2018.
 */

class UpcomingGameLayout extends GameLayout {
    private TextView broadcastInfo;

    public UpcomingGameLayout(GameInterface game, View rootView, Activity activity) {
        super(game, rootView, activity);
    }

    @Override
    int getLayoutId() {
        return R.layout.game_upcoming;
    }

    @Override
    void initView() {
        broadcastInfo = (TextView) findViewById(R.id.broadcastInfo);
    }

    private void populateTopScorers(String team) throws JSONException {
        Roster roster = game.getRosterForTeam(team);
        LinearLayout topScorersLayout = rootView.findViewById(getResources().getIdentifier("top_"+team+"_scorers_layout",
                "id", getActivity().getPackageName()));
        for (int i = 0; i < 3; i++) {
            Person person = roster.getPerson(i);
            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.top_scorer_box, topScorersLayout, false);

            ImageView photo = layout.findViewById(R.id.top_scorer_photo);
            TextView scorer = layout.findViewById(R.id.top_scorer_name);
            TextView goals = layout.findViewById(R.id.top_scorer_goals);
            TextView assists = layout.findViewById(R.id.top_scorer_assists);

            scorer.setText(person.getShortName());
            Person.Stats currentStats = person.getCurrentStats();
            goals.setText(MessageFormat.format("{0}G", currentStats == null ? 0 : currentStats.goals()));
            assists.setText(MessageFormat.format("{0}A", currentStats == null ? 0 : currentStats.assists()));
            imageCircleUrl(photo, getImageFor(person.getId()));

            topScorersLayout.addView(layout);
        }
    }

    @Override
    public void fill() throws JSONException {
        populateTopScorers("home");
        populateTopScorers("away");
        broadcastInfo.setText(game.getBroadcastInfo(game.getAppTeamSide()));
    }
}

