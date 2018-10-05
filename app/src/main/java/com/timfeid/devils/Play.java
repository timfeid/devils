package com.timfeid.devils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 2/14/2018.
 * Play object from NHL api
 */

public class Play {
    private JSONObject play;
    private Game game;
    Play(Game game, JSONObject play) {
        this.game = game;
        this.play = play;
    }

    String getStrength() throws JSONException {
        return play.getJSONObject("result").getJSONObject("strength").getString("code");
    }

    ScoringPlayer getScorer() throws JSONException {
        JSONArray players = play.getJSONArray("players");
        for (int j = 0; j < players.length(); j++) {
            JSONObject player = players.getJSONObject(j);

            if (player.getString("playerType").equals(Game.PLAYER_TYPE_SCORER)) {
                return new ScoringPlayer(player);
            }
        }

        return null;
    }

    private String getSide() throws JSONException {
        return game.getTeamSideById(play.getJSONObject("team").getInt("id"));
    }

    private List<ScoringPlayer> getAssisters() throws JSONException {
        List<ScoringPlayer> list = new ArrayList<>();
        JSONArray players = play.getJSONArray("players");
        for (int j = 0; j < players.length(); j++) {
            JSONObject player = players.getJSONObject(j);

            if (player.getString("playerType").equals(Game.PLAYER_TYPE_ASSIST)) {
                list.add(new ScoringPlayer(player));
            }
        }

        return list;
    }

    public String getAssists() throws JSONException {
        StringBuilder assists = new StringBuilder();
        for (ScoringPlayer player : getAssisters()) {
            if (player.getPerson() != null) {
                assists.append(player.getPerson().getShortName());
            } else {
                assists.append("Unknown");
            }
            assists.append(" (");
            assists.append(player.getSeasonTotal());
            assists.append("), ");
        }

        if (assists.length() == 0) {
            return "Unassisted";
        }

        return assists.toString().substring(0, assists.length() - 2);
    }

    String getScore() throws JSONException {
        JSONObject goals = play.getJSONObject("about").getJSONObject("goals");

        return goals.getInt("away") + "-" + goals.getInt("home") + " " + game.getWinningTeamAbbreviation(goals);
    }


    String getScoreType() throws JSONException {
        return play.getJSONObject("result").getString("secondaryType");
    }

    public String getTime() throws JSONException {
        JSONObject about = play.getJSONObject("about");

        return about.getString("periodTime") + " " + about.getString("ordinalNum");
    }

    public JSONObject getPlay() {
        return play;
    }

    public int getId() throws JSONException {
        return play.getJSONObject("about").getInt("eventId");
    }

    class ScoringPlayer {
        JSONObject scoringPlayer;
        Person person;
        ScoringPlayer(JSONObject obj) throws JSONException {
            scoringPlayer = obj;
            person = game.getRosterForTeam(getSide()).getPersonById(obj.getJSONObject("player").getInt("id"));
        }

        public Person getPerson() {
            return person;
        }

        int getSeasonTotal() throws JSONException {
            return scoringPlayer.getInt("seasonTotal");
        }
    }
}
