package com.timfeid.devils;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by Tim on 3/16/2018.
 */

public class StandingsFragment extends Fragment {
    protected void addDivision(LayoutInflater inflater, ViewGroup standings, Standings.TeamGetter division) throws JSONException {
        View table = inflater.inflate(R.layout.standings_division_table, standings, false);
        TextView divisionTxt = table.findViewById(R.id.division_name);

        String name = "";
        if (division instanceof Standings.NameAndTeamsGetter) {
            Standings.NameAndTeamsGetter nameGetter = (Standings.NameAndTeamsGetter) division;
            name = nameGetter.getName();
            divisionTxt.setText(nameGetter.getName());
        }
        standings.addView(table);
        TableLayout t = table.findViewById(R.id.table);

        JSONArray teams = division.getTeams();
        for (int i = 0; i < teams.length(); i++) {
            JSONObject team = teams.getJSONObject(i);
            View row = inflater.inflate(R.layout.standings_division_row, (ViewGroup) table, false);
            populateRow(row, team);
            if (team.getJSONObject("team").getInt("id") == Integer.parseInt(Config.getValue("team_id"))) {
                row.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightest, null));
                row.findViewById(R.id.points).setBackgroundColor(getResources().getColor(R.color.colorPrimaryLight, null));
            }
            TextView placeTxt = row.findViewById(R.id.place);
            placeTxt.setText(String.format(Locale.US, "%d", i+1));
            t.addView(row);
            View view = new View(getContext());
            view.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.myrect));
            int height = 1;
            if (name.equals("Wildcard") && i == 1) {
                height = 10;
            }
            view.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, height));
            t.addView(view);
        }
    }

    private void populateRow(View row, JSONObject team) throws JSONException {
        TextView teamTxt = row.findViewById(R.id.team);
        TextView gamesTxt = row.findViewById(R.id.games);
        TextView winsTxt = row.findViewById(R.id.wins);
        TextView lossesTxt = row.findViewById(R.id.losses);
        TextView otTxt = row.findViewById(R.id.otl);
        TextView pointsTxt = row.findViewById(R.id.points);
        TextView rowTxt = row.findViewById(R.id.row);
        TextView diffTxt = row.findViewById(R.id.diff);
        TextView streakTxt = row.findViewById(R.id.strk);
        ImageView logo = row.findViewById(R.id.logo);

        logo.setImageResource(getResources().getIdentifier("team_" + team.getJSONObject("team").getInt("id") + "_20172018_light",
                "drawable", getActivity().getPackageName()));
        teamTxt.setText(team.getJSONObject("team").getString("abbreviation"));
        gamesTxt.setText(String.format(Locale.US, "%d", team.getInt("gamesPlayed")));
        winsTxt.setText(String.format(Locale.US, "%d", team.getJSONObject("leagueRecord").getInt("wins")));
        lossesTxt.setText(String.format(Locale.US, "%d", team.getJSONObject("leagueRecord").getInt("losses")));
        otTxt.setText(String.format(Locale.US, "%d", team.getJSONObject("leagueRecord").getInt("ot")));
        pointsTxt.setText(String.format(Locale.US, "%d", team.getInt("points")));
        pointsTxt.setBackgroundColor(getResources().getColor(R.color.lightGrey, null));
        rowTxt.setText(String.format(Locale.US, "%d", team.getInt("row")));
        int diff = team.getInt("goalsScored") - team.getInt("goalsAgainst");
        diffTxt.setText(String.format(Locale.US, "%d", diff));
        if (diff >= 0) {
            diffTxt.setTextColor(getResources().getColor(R.color.diffPositive, null));
        } else {
            diffTxt.setTextColor(getResources().getColor(R.color.diffNegative, null));
        }
        streakTxt.setText(team.getJSONObject("streak").getString("streakCode"));
    }
}
