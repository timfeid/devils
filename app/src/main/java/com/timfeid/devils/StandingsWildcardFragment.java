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

/**
 * Created by Tim on 3/10/2018.
 */

public class StandingsWildcardFragment extends StandingsFragment implements Listener {
    protected View rootView;
    TreeMap<String, Standings.Conference> conferences = new TreeMap<>();
    JSONObject wildcard;
    Collection<Standings.Conference> getConferences() {
        return conferences.values();
    }

    private void parseWildcard() throws JSONException {
        JSONArray records = wildcard.getJSONArray("records");
        for (int i = 0; i < records.length(); i++) {
            JSONObject record = records.getJSONObject(i);
            JSONObject conference = record.getJSONObject("conference");
            String conferenceName = conference.getString("name");

            Standings.Conference conf;
            if (!conferences.containsKey(conferenceName)) {
                conf = new Standings.Conference(conference);
                conferences.put(conferenceName, conf);
            } else {
                conf = conferences.get(conferenceName);
            }

            if (record.getString("standingsType").equals("divisionLeaders")) {
                conf.addDivision(record);
            } else {
                conf.setWildcard(record);
            }


        }
    }

    public StandingsWildcardFragment() {
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
        if (wildcard == null) {
            return;
        }
        rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        LinearLayout standings = rootView.findViewById(R.id.standings);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (Standings.Conference conference : getConferences()) {
            try {
                View header = inflater.inflate(R.layout.standings_conference_header, (ViewGroup) rootView, false);
                TextView headerTxt = header.findViewById(R.id.name);
                headerTxt.setText(String.format("%s %s", conference.getName(), getString(R.string.conference)));
                standings.addView(header);

                for (Standings.Division division : conference.getDivisions()) {
                    addDivision(inflater, standings, division);
                }
                addDivision(inflater, standings, conference.getWildcard());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void handle(Standings request) {
        wildcard = request.getWildcard();
        try {
            parseWildcard();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
