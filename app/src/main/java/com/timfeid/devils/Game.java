package com.timfeid.devils;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Tim on 2/2/2018.
 */

public class Game implements GameInterface {
    public static final String PLAYER_TYPE_SCORER = "Scorer";
    public static final String PLAYER_TYPE_ASSIST = "Assist";
    public static final String STRENGTH_EVEN = "EVEN";
    public static final String CODE_FINAL = "6";
    public static final String CODE_PREGAME = "2";
    public static final String CODE_SCHEDULED = "1";
    public static final String TEAM_NAME = "teamName";
    public static final String EVENT_TYPE_STOP = "STOP";
    public static final String EVENT_TYPE_GOAL = "GOAL";
    public static final String EVENT_TYPE_END_PERIOD = "PERIOD_END";
    public static final String EVENT_PERIOD_READY = "PERIOD_READY";
    public static final String GAME_TYPE_PLAYOFF = "P";
    JSONObject game;
    private GameContent gameContent;
    private List<Play> plays;

    public Game(JSONObject game) {
        this.game = game;
    }

    public JSONObject getGame() {
        return game;
    }

    public JSONObject getAwayTeam() throws JSONException {
        return getTeam("away");
    }

    public JSONObject getTeam(String team) throws JSONException {
        return game.getJSONObject("teams")
                .getJSONObject(team);
    }

    public JSONObject getHomeTeam() throws JSONException {
        return getTeam("home");
    }

    public String getAwayTeamName() throws JSONException {
        return getAwayTeam()
                .getJSONObject("team")
                .getString(TEAM_NAME);
    }

    public String getAwayTeamCity() throws JSONException {
        return getAwayTeam()
                .getJSONObject("team")
                .getString("locationName");
    }

    public String getHomeTeamName() throws JSONException {
        return getHomeTeam()
                .getJSONObject("team")
                .getString("teamName");
    }

    public boolean isGameLive() throws JSONException {
        return !this.getCodedGameState().equals(CODE_SCHEDULED);
    }

    public String getHomeTeamCity() throws JSONException {
        return getHomeTeam()
                .getJSONObject("team")
                .getString("locationName");
    }

    public String getRecord(String team) throws JSONException {
        StringBuilder recordText = new StringBuilder();
        JSONObject record = getTeam(team)
                .getJSONObject("leagueRecord");

        recordText.append(record.getString("wins"));
        recordText.append("-");
        recordText.append(record.getString("losses"));

        if (record.has("ot")) {
            recordText.append("-");
            recordText.append(record.getString("ot"));
        }

        return recordText.toString();
    }

    public String getHomeTeamRecord() throws JSONException {
        return getRecord("home");
    }

    public String getAwayTeamRecord() throws JSONException {
        return getRecord("away");
    }

    public Integer getTeamPoints(String team) throws JSONException {
        Integer points = 0;
        JSONObject record = getTeam(team)
                .getJSONObject("leagueRecord");
        points += record.optInt("wins") * 2;
        points += record.optInt("ot");

        return points;
    }

    public Integer getHomeTeamPoints() throws JSONException {
        return getTeamPoints("home");
    }

    public Integer getAwayTeamPoints() throws JSONException {
        return getTeamPoints("away");
    }

    public SpannableStringBuilder getPlayoffSeriesFormattedRecord(String team) throws JSONException {
        String record = "Series "+getRecord(team);
        SpannableStringBuilder str = new SpannableStringBuilder(record);

        return str;
    }

    public SpannableStringBuilder getFormattedRecord(String team) throws JSONException {
        if (game.getString("gameType").equals(GAME_TYPE_PLAYOFF)) {
            return getPlayoffSeriesFormattedRecord(team);
        }
        String record = getRecord(team) + ", ";
        String points = getTeamPoints(team).toString() + "pts";
        String recordWithPoints = record + points;
        SpannableStringBuilder str = new SpannableStringBuilder(recordWithPoints);
        str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), record.length(), recordWithPoints.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return str;
    }

    public Integer getId(String team) throws JSONException {
        return getTeam(team).getJSONObject("team").getInt("id");
    }

    public Integer getAwayId() throws JSONException {
        return getId("away");
    }

    public Integer getHomeId() throws JSONException {
        return getId("home");
    }

    public Date getDate() throws JSONException {
        try {
            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            return utcFormat.parse(game.getString("gameDate"));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @Override
    public String getGoalsFor(String team) throws JSONException {
        return getTeam(team).getString("score");
    }

    @Override
    public boolean periodHasBeenPlayed(int period) throws JSONException {
        JSONArray periods = game.getJSONObject("linescore").getJSONArray("periods");
        for (int i = 0; i < periods.length(); i++) {
            JSONObject periodObj = periods.getJSONObject(i);
            if (periodObj.getInt("num") == period) {
                return true;
            }
        }

        return false;
    }

    @Override
    public JSONArray getPlayersOnIce(String team) {
        return null;
    }

    @Override
    public boolean isFinal() throws JSONException {
        return Integer.valueOf(getCodedGameState()) >= Integer.valueOf(CODE_FINAL);
    }

    public String getAppTeamSide() throws JSONException {
        if (getHomeId() == 1) {
            return "home";
        }

        return "away";
    }

    public String getBroadcastInfo(String team) throws JSONException {
        return getBroadcastInfo(team, "broadcasts") + ", "
                + getBroadcastInfo(team, "radioBroadcasts");
    }

    public String getBroadcastInfo(String team, String type) throws JSONException {
        if (!game.has(type)) {
            return "TBD";
        }

        JSONArray broadcasts = game.getJSONArray(type);
        for (int i = 0; i < broadcasts.length(); i++) {
            JSONObject broadcast = broadcasts.getJSONObject(i);
            if (broadcast.getString("type").equals(team) || broadcast.getString("type").equals("national")) {
                return broadcast.getString("name");
            }
        }

        return "";
    }

    public String getCodedGameState() throws JSONException {
        return getStatus().getString("codedGameState");
    }

    public JSONObject getStatus() throws JSONException {
        return game.getJSONObject("status");
    }

    public int getPeriodScore(String team, int period) throws JSONException {
        JSONArray periods = game.getJSONObject("linescore").getJSONArray("periods");
        for (int i = 0; i < periods.length(); i++) {
            JSONObject periodObj = periods.getJSONObject(i);
            if (periodObj.getInt("num") == period) {
                return periodObj.getJSONObject(team).getInt("goals");
            }
        }

        return 0;
    }

    public int getShotsOnGoal(String team) throws JSONException {
        int shots = 0;
        JSONArray periods = game.getJSONObject("linescore").getJSONArray("periods");
        for (int i = 0; i < periods.length(); i++) {
            shots += periods.getJSONObject(i).getJSONObject(team).getInt("shotsOnGoal");
        }

        return shots;
    }

    public int getFirstStarId() throws JSONException {
        return getFirstStar().getInt("id");
    }

    public int getSecondStarId() throws JSONException {
        return getSecondStar().getInt("id");
    }

    public int getThirdStarId() throws JSONException {
        return getThirdStar().getInt("id");
    }

    public JSONObject getFirstStar() throws JSONException {
        return game.getJSONObject("decisions").getJSONObject("firstStar");
    }

    public JSONObject getSecondStar() throws JSONException {
        return game.getJSONObject("decisions").getJSONObject("secondStar");
    }

    public JSONObject getThirdStar() throws JSONException {
        return game.getJSONObject("decisions").getJSONObject("thirdStar");
    }

    public String getPlayerStats(int playerId) throws JSONException {
        int goals = 0;
        int assists = 0;
        List<Play> scoringPlays = getScoringPlays();
        for (Play play : scoringPlays) {
            JSONArray players = play.getPlay().getJSONArray("players");
            for (int j = 0; j < players.length(); j++) {
                JSONObject player = players.getJSONObject(j);
                if (player.getJSONObject("player").getInt("id") == playerId) {
                    String playerType = player.getString("playerType");
                    if (playerType.equals(PLAYER_TYPE_SCORER)) {
                        goals ++;
                    }
                    if (playerType.equals(PLAYER_TYPE_ASSIST)) {
                        assists ++;
                    }
                }
            }
        }

        return goals+"G, "+assists+"A";
    }

    public List<Play> getScoringPlays() throws JSONException {
        if (plays == null) {
            JSONArray jsonPlays = game.getJSONArray("scoringPlays");
            plays = new ArrayList<>();
            for (int i = 0; i < jsonPlays.length(); i++) {
                plays.add(new Play(this, jsonPlays.getJSONObject(i)));
            }
        }

        return plays;
    }

    public Play getScoringPlay(int scoringPlayIndex) throws JSONException {
        return getScoringPlays().get(scoringPlayIndex);
    }

    public Roster getRosterForTeam(String team) throws JSONException {
        JSONObject teamObj = getTeam(team);
        return new Roster(teamObj.getJSONObject("team").getJSONObject("roster").getJSONArray("roster"));
    }

    public Integer getId() throws JSONException {
        return game.getInt("gamePk");
    }

    @Override
    public boolean isPregame() throws JSONException {
        return this.getCodedGameState().equals(CODE_PREGAME);
    }

    @Override
    public boolean isTimeRunning() throws JSONException {
        return false;
    }

    @Override
    public boolean hasShootout() throws JSONException {
        return game.getJSONObject("linescore").getBoolean("hasShootout");
    }

    @Override
    public int getShootoutGoals(String team) throws JSONException {
        return game.getJSONObject("linescore").getJSONObject("shootoutInfo").getJSONObject(team).getInt("scores");
    }

    @Override
    public GameContent getGameContent() {
        if (gameContent == null) {
            try {
                gameContent = new GameContent(game.getJSONObject("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return gameContent;
    }

    @Override
    public String getTeamSideById(int id) throws JSONException {
        if (getTeam("away").getJSONObject("team").getInt("id") == id) {
            return "away";
        }

        return "home";
    }

    @Override
    public boolean hasOt() throws JSONException {
        return game.getJSONObject("linescore").getJSONArray("periods").length() > 3;
    }

    @Override
    public Boxscore getBoxscore(String team) throws JSONException {
        return new Boxscore(game.getJSONObject("boxscore").getJSONObject("teams").getJSONObject(team));
    }

    public String getWinningTeamAbbreviation(JSONObject goals) throws JSONException {
        int home = goals.getInt("home");
        int away = goals.getInt("away");

        if (away == home) {
            return "Tie";
        }

        if (home > away) {
            return getTeam("home").getJSONObject("team").getString("abbreviation");
        }

        return getTeam("away").getJSONObject("team").getString("abbreviation");
    }
}
