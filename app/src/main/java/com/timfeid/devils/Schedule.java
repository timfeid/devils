package com.timfeid.devils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tim on 2/2/2018.
 */

public class Schedule extends Observable implements Listener {
    private final static int DAYS_FROM_TODAY = 15;
    public final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private ArrayList<Game> games = new ArrayList<>();
    private boolean done = false;

    public Schedule() {
        build();
    }

    public void build() {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, DAYS_FROM_TODAY);
        startDate.add(Calendar.DATE, DAYS_FROM_TODAY * -1);

        ApiRequest request = new ApiRequest("schedule");
        request.addParam("startDate", DATE_FORMAT.format(startDate.getTime()));
        request.addParam("endDate", DATE_FORMAT.format(endDate.getTime()));
        request.addParam("hydrate", "team(leaders,roster(season=20172018,person(name,stats(splits=[statsSingleSeason])))),linescore,broadcasts(all),tickets,game(content(media(epg),highlights(scoreboard)),seriesSummary),radioBroadcasts,metadata,decisions,scoringplays,seriesSummary(series)");
        request.addParam("teamId", "1");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }

    private void parseObject(String output) {
        try {
            JSONArray dates = new JSONObject(output).getJSONArray("dates");
            for (int j = 0; j < dates.length(); j++) {
                JSONArray games = dates.getJSONObject(j).getJSONArray("games");
                for (int i = 0; i < games.length(); i++) {
                    JSONObject game = games.getJSONObject(i);
                    this.games.add(new Game(game));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Game getGame(int gameNumber) {
        return this.games.get(gameNumber);
    }

    public Game getNextGame() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyy", Locale.US);
        String currentDate = dateFormat.format(new Date());
        for (int i = 0; i < games.size(); i++) {
            try {
                Game game = games.get(i);
                // Today's date (later today or already played) or game is live
                if (dateFormat.format(game.getDate()).equals(currentDate)
                        || !game.getCodedGameState().equals(Game.CODE_FINAL)) {
                    return game;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public Game findGameById(int id) {
        for (int i = 0; i < games.size(); i++) {
            try {
                Game game = games.get(i);
                if (game.getId().equals(id)) {
                    return game;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public boolean done() {
        return this.done;
    }

    public ArrayList<Game> getGames() {
        return games;
    }

    public void handle(Runnable runner) {
        ApiRequest myRequest = (ApiRequest) runner;
        parseObject(myRequest.getOutput());
        done = true;
        notifyListeners();
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof ObservableThread) {
            handle((Runnable) observable);
        }
    }
}
