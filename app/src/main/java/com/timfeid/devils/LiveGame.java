package com.timfeid.devils;

import android.text.SpannableStringBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Tim on 2/10/2018.
 */

class LiveGame implements GameInterface {
    public static final String TEAM_NAME = "name";
    private JSONObject liveGame;
    private JSONObject liveData;
    private Game game;

    public LiveGame(Game game, JSONObject liveGame) {
        this.liveGame = liveGame;
        this.game = game;
        try {
            liveData = liveGame.getJSONObject("liveData");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Integer getId() throws JSONException {
        return liveGame.getJSONObject("gameData").getJSONObject("game").getInt("pk");
    }

    public JSONObject getBoxscoreFor(String team) throws JSONException {
        return liveData.getJSONObject("boxscore").getJSONObject("teams").getJSONObject(team);
    }

    public JSONObject getScoringInfoFor(String team) throws JSONException {
        return getBoxscoreFor(team).getJSONObject("teamStats").getJSONObject("teamSkaterStats");
    }

    public String getGoalsFor(String team) throws JSONException {
        int goals = getScoringInfoFor(team).getInt("goals");
        if (isFinal()) {
            if (hasShootout()) {
                String otherTeam = team.equals("away") ? "home" : "away";
                if (getShootoutGoals(team) > getShootoutGoals(otherTeam)) {
                    goals ++;
                }
            }
        }

        return String.valueOf(goals);
    }

    public boolean isFinal() throws JSONException {
        return getCodedGameState().equals(Game.CODE_FINAL);
    }

    @Override
    public boolean periodHasBeenPlayed(int period) throws JSONException {
        JSONArray periods = liveData.getJSONObject("linescore").getJSONArray("periods");
        for (int i = 0; i < periods.length(); i++) {
            JSONObject periodObj = periods.getJSONObject(i);
            if (periodObj.getInt("num") == period) {
                return true;
            }
        }

        return false;
    }

    @Override
    public JSONArray getPlayersOnIce(String team) throws JSONException {
        return liveData.getJSONObject("boxscore").getJSONObject("teams").getJSONObject(team).getJSONArray("onIce");
    }

    @Override
    public boolean isPregame() throws JSONException {
        return liveGame.getJSONObject("gameData").getJSONObject("status").getString("codedGameState").equals(Game.CODE_PREGAME);
    }

    @Override
    public boolean isTimeRunning() throws JSONException {
        String event = liveData.getJSONObject("plays")
                .getJSONObject("currentPlay")
                .getJSONObject("result")
                .getString("eventTypeId");

        return !event.equals(Game.EVENT_TYPE_STOP)
                && !event.equals(Game.EVENT_TYPE_GOAL)
                && !event.equals(Game.EVENT_TYPE_END_PERIOD)
                && !event.equals(Game.EVENT_PERIOD_READY);
    }

    public int getShotsOnGoal(String team) throws JSONException {
        return getScoringInfoFor(team).getInt("shots");
    }

    @Override
    public int getFirstStarId() throws JSONException {
        return game.getFirstStarId();
    }

    @Override
    public int getSecondStarId() throws JSONException {
        return game.getSecondStarId();
    }

    @Override
    public int getThirdStarId() throws JSONException {
        return game.getThirdStarId();
    }

    @Override
    public JSONObject getFirstStar() throws JSONException {
        return game.getFirstStar();
    }

    @Override
    public JSONObject getSecondStar() throws JSONException {
        return game.getSecondStar();
    }

    @Override
    public JSONObject getThirdStar() throws JSONException {
        return game.getThirdStar();
    }

    @Override
    public String getPlayerStats(int firstStarId) throws JSONException {
        return game.getPlayerStats(firstStarId);
    }

    @Override
    public List<Play> getScoringPlays() throws JSONException {
        List<Play> mPlays = new ArrayList<>();
        JSONObject plays = liveData.getJSONObject("plays");
        JSONArray r = plays.getJSONArray("scoringPlays");
        for (int i = 0; i < r.length(); i++) {
            mPlays.add(new Play(this.game, plays.getJSONArray("allPlays").getJSONObject(r.getInt(i))));
        }

        return mPlays;
    }

    @Override
    public String getCodedGameState() throws JSONException {
        return game.getCodedGameState();
    }

    @Override
    public Roster getRosterForTeam(String team) throws JSONException {
        return game.getRosterForTeam(team);
    }

    @Override
    public String getAppTeamSide() throws JSONException {
        return game.getAppTeamSide();
    }

    @Override
    public String getBroadcastInfo(String appTeamSide) throws JSONException {
        return game.getBroadcastInfo(appTeamSide);
    }

    @Override
    public Date getDate() throws JSONException {
        return game.getDate();
    }

    public String getPeriod() throws JSONException {
        return "3rd";
    }

    public JSONObject getTeam(String team) throws JSONException {
        return game.getTeam(team);
    }

    @Override
    public String getAwayTeamName() throws JSONException {
        return game.getAwayTeamName();
    }

    @Override
    public Integer getAwayId() throws JSONException {
        return game.getAwayId();
    }

    @Override
    public String getAwayTeamCity() throws JSONException {
        return game.getAwayTeamCity();
    }

    @Override
    public SpannableStringBuilder getFormattedRecord(String team) throws JSONException {
        return game.getFormattedRecord(team);
    }

    @Override
    public String getHomeTeamName() throws JSONException {
        return game.getHomeTeamName();
    }

    @Override
    public Integer getHomeId() throws JSONException {
        return game.getHomeId();
    }

    @Override
    public String getHomeTeamCity() throws JSONException {
        return game.getHomeTeamCity();
    }

    @Override
    public int getPeriodScore(String team, int period) throws JSONException {
        JSONArray periods = liveData.getJSONObject("linescore").getJSONArray("periods");
        for (int i = 0; i < periods.length(); i++) {
            JSONObject periodObj = periods.getJSONObject(i);
            if (periodObj.getInt("num") == period) {
                return periodObj.getJSONObject(team).getInt("goals");
            }
        }

        return 0;
    }

    public static int findId(JSONObject obj) throws JSONException {
        return obj.getJSONObject("gameData").getJSONObject("game").getInt("pk");
    }

    public String getCurrentPeriodName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.US);
        try {
            return liveData.getJSONObject("linescore").getString("currentPeriodOrdinal");
        } catch (JSONException e) {
            try {
                return dateFormat.format(game.getDate());
            } catch (JSONException e1) {
                return "";
            }
        }
    }

    public String getCurrentPeriodTimeRemaining() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        try {
            if (isPregame()) {
                return "Pre-Game";
            } else {
                return liveData.getJSONObject("linescore").getString("currentPeriodTimeRemaining");
            }
        } catch (JSONException e) {
            try {
                return timeFormat.format(game.getDate());
            } catch (JSONException e1) {
                return "";
            }
        }
    }

    @Override
    public boolean hasShootout() throws JSONException {
        return liveData.getJSONObject("linescore").getBoolean("hasShootout");
    }

    @Override
    public int getShootoutGoals(String team) throws JSONException {
        return liveData.getJSONObject("linescore").getJSONObject("shootoutInfo").getJSONObject(team).getInt("scores");
    }

    @Override
    public GameContent getGameContent() {
        try {
            return GameContent.forGame(getId()).getContents();
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Boxscore getBoxscore(String team) throws JSONException {
        return new Boxscore(liveData.getJSONObject("boxscore").getJSONObject("teams").getJSONObject(team));
    }

    @Override
    public String getTeamSideById(int id) throws JSONException {
        return game.getTeamSideById(id);
    }

    @Override
    public boolean hasOt() throws JSONException {
        return liveData.getJSONObject("linescore").getJSONArray("periods").length() > 3;
    }
}
