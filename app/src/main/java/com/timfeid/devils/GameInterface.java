package com.timfeid.devils;

import android.text.SpannableStringBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by Tim on 2/10/2018.
 * Game interface for different types of game data from NHL api
 */

interface GameInterface {
    JSONObject getTeam(String team) throws JSONException;

    String getAwayTeamName() throws JSONException;

    Integer getAwayId() throws JSONException;

    String getAwayTeamCity() throws JSONException;

    SpannableStringBuilder getFormattedRecord(String team) throws JSONException;

    String getHomeTeamName() throws JSONException;

    Integer getHomeId() throws JSONException;

    String getHomeTeamCity() throws JSONException;

    int getPeriodScore(String team, int i) throws JSONException;

    int getShotsOnGoal(String team) throws JSONException;

    int getFirstStarId() throws JSONException;

    int getSecondStarId() throws JSONException;

    int getThirdStarId() throws JSONException;

    JSONObject getFirstStar() throws JSONException;

    JSONObject getSecondStar() throws JSONException;

    JSONObject getThirdStar() throws JSONException;

    String getPlayerStats(int firstStarId) throws JSONException;

    List<Play> getScoringPlays() throws JSONException;

    String getCodedGameState() throws JSONException;

    Roster getRosterForTeam(String team) throws JSONException;

    String getAppTeamSide() throws JSONException;

    String getBroadcastInfo(String appTeamSide) throws JSONException;

    Date getDate() throws JSONException;

    String getGoalsFor(String team) throws JSONException;

    boolean periodHasBeenPlayed(int period) throws JSONException;

    JSONArray getPlayersOnIce(String team) throws JSONException;

    boolean isFinal() throws JSONException;

    boolean isPregame() throws JSONException;

    boolean isTimeRunning() throws JSONException;

    boolean hasShootout() throws JSONException;

    int getShootoutGoals(String team) throws JSONException;

    GameContent getGameContent();

    String getTeamSideById(int id) throws JSONException;

    boolean hasOt() throws JSONException;

    Boxscore getBoxscore(String team) throws JSONException;

    class Boxscore {
        private JSONObject boxscore;
        Boxscore(JSONObject boxscore) {
            this.boxscore = boxscore;
        }
        int getShots() throws JSONException {
            return getTeamSkaterStats().getInt("shots");
        }

        private JSONObject getTeamSkaterStats() throws JSONException {
            return this.boxscore.getJSONObject("teamStats").getJSONObject("teamSkaterStats");
        }

        int getPim() throws JSONException {
            return getTeamSkaterStats().getInt("pim");
        }
        int getPowerPlayGoals() throws JSONException {
            return getTeamSkaterStats().getInt("powerPlayGoals");
        }
        int getPowerPlayOpportunities() throws JSONException {
            return getTeamSkaterStats().getInt("powerPlayOpportunities");
        }
        int getFaceOffWinPercentage() throws JSONException {
            return getTeamSkaterStats().getInt("faceOffWinPercentage");
        }
        int getBlocked() throws JSONException {
            return getTeamSkaterStats().getInt("blocked");
        }
        int getTakeaways() throws JSONException {
            return getTeamSkaterStats().getInt("takeaways");
        }
        int getGiveaways() throws JSONException {
            return getTeamSkaterStats().getInt("giveaways");
        }
        int getHits() throws JSONException {
            return getTeamSkaterStats().getInt("hits");
        }
    }
}
