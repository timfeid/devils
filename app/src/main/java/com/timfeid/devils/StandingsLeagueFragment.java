package com.timfeid.devils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.TreeMap;

import static com.timfeid.devils.Standings.*;

/**
 * Created by Tim on 3/10/2018.
 */

public class StandingsLeagueFragment extends StandingsFragment implements Listener {
    protected View rootView;
    League league;

    public StandingsLeagueFragment() {
        Team.getInstance().withStandings(this);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_standings_wildcard, container, false);

        populateView();

        return rootView;
    }

    void populateView() {
        if (league == null) {
            return;
        }
        rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        LinearLayout standings = rootView.findViewById(R.id.standings);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            View header = inflater.inflate(R.layout.standings_conference_header, null);
            TextView headerTxt = header.findViewById(R.id.name);
            headerTxt.setText(R.string.league);
            standings.addView(header);

            addDivision(inflater, standings, league);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void handle(Standings request) {
        league = request.getLeague();
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                populateView();
            }
        });
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof Standings) {
            handle((Standings) observable);
        }
    }
}
