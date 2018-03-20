package com.timfeid.devils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

/**
 * Created by Tim on 3/16/2018.
 * Get standings from NHL api
 */

public class Standings extends Observable implements Listener {

    private static final String WILDCARD_ENDPOINT = "standings/wildCardWithLeaders";
    private static final String LEAGUE_ENDPOINT = "standings";

    private League league;
    private Boolean completeLeague = false;
    private Boolean complete = false;
    private Boolean completeWildcard = false;
    private JSONObject wildcard;
    private JSONObject leagueObj;

    public Standings() {
        startLeagueThread();
        startWildcardThread();
    }

    private void startWildcardThread() {
        ApiRequest request = new ApiRequest(WILDCARD_ENDPOINT);
        request.addParam("expand", "standings.record,standings.team,standings.division,standings.conference,team.schedule.next,team.schedule.previous");
        request.addParam("season", "20172018");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }

    private void startLeagueThread() {
        ApiRequest request = new ApiRequest(LEAGUE_ENDPOINT);
        request.addParam("expand", "standings.record,standings.team,standings.division,standings.conference,team.schedule.next,team.schedule.previous");
        request.addParam("season", "20172018");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }

    @Override
    public void handle(Observable observable) {
        ApiRequest request = (ApiRequest) observable;
        if (request.getEndpoint().equals(LEAGUE_ENDPOINT)) {
            league = new League();
            parseLeague(request.getOutput());

            completeLeague = true;
            complete = completeWildcard;
        }

        if (request.getEndpoint().equals(WILDCARD_ENDPOINT)) {
            league = new League();
            parseWildcard(request.getOutput());

            completeWildcard = true;
            complete = completeLeague;
        }

        if (complete) {
            notifyListeners();
        }
    }

    private void parseLeague(String output) {
        try {
            leagueObj = new JSONObject(output);
            JSONArray records = leagueObj.getJSONArray("records");
            league = new League();

            for (int i = 0; i < records.length(); i++) {
                JSONObject record = records.getJSONObject(i);
                JSONArray recordList = record.getJSONArray("teamRecords");
                for (int j = 0; j < recordList.length(); j++) {
                    league.addTeam(recordList.getJSONObject(j));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseWildcard(String output) {
        try {
            wildcard = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public League getLeague() {
        return league;
    }

    public JSONObject getWildcard() {
        return wildcard;
    }

    JSONObject getLeagueObj() {
        return leagueObj;
    }

    public boolean done() {
        return complete;
    }

    static class Conference {
        List<Division> divisions = new ArrayList<>();
        Wildcard wildcard;
        JSONObject conference;

        public Conference(JSONObject conference) {
            this.conference = conference;
        }

        void addDivision(JSONObject division) throws JSONException {
            divisions.add(new Division(division));
        }

        List<Division> getDivisions() {
            return divisions;
        }

        public Wildcard getWildcard() {
            return wildcard;
        }

        public void setWildcard(JSONObject wildcard) throws JSONException {
            this.wildcard = new Wildcard(wildcard);
        }

        public String getName() throws JSONException {
            return conference.getString("name");
        }
    }

    static class Division extends NameAndTeamsGetter {
        public Division(JSONObject division) throws JSONException {
            super(division);
        }

        public String getName() throws JSONException {
            return obj.getJSONObject("division").getString("name");
        }
    }

    static class Wildcard extends NameAndTeamsGetter {
        public Wildcard(JSONObject wildcard) throws JSONException {
            super(wildcard);
        }

        @Override
        String getName() throws JSONException {
            return "Wildcard";
        }
    }

    static class League implements TeamGetter {
        TreeMap<BigInteger, JSONObject> teams = new TreeMap<>();

        @Override
        public JSONArray getTeams() {
            JSONArray teamsJson = new JSONArray();
            for (BigInteger key : teams.descendingKeySet()) {
                JSONObject team = teams.get(key);
                teamsJson.put(team);
            }

            return teamsJson;
        }

        void addTeam(JSONObject team) {
            try {
                String teamName = team.getJSONObject("team").getString("name");
                String weight = String.format(Locale.US, "%d%03d%03d%03d%03d",
                        team.getInt("points"),
                        82 - team.getInt("gamesPlayed"),
                        team.getInt("row"),
                        (int) teamName.charAt(0),
                        (int) teamName.charAt(1));
                BigInteger weightTotal = new BigInteger(weight);

                teams.put(weightTotal, team);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    abstract static class NameAndTeamsGetter implements TeamGetter {
        JSONArray teams;
        JSONObject obj;

        NameAndTeamsGetter(JSONObject obj) throws JSONException {
            teams = obj.getJSONArray("teamRecords");
            this.obj = obj;
        }

        public JSONArray getTeams() {
            return teams;
        }

        abstract String getName() throws JSONException;
    }

    interface TeamGetter {
        JSONArray getTeams();
    }
}
