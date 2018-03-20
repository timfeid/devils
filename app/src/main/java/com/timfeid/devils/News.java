package com.timfeid.devils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tim on 2/6/2018.
 * Get news from the NHL api
 */

public class News extends Observable implements Listener {
    private List<Player> players = new ArrayList<>();
    private boolean complete = false;
    private JSONArray roster;
    private String output;

    News() {
        ApiRequest request = new ApiRequest(""+277567796);
        request.setBaseUrl("http://search-api.svc.nhl.com/svc/search/v2/nhl_global_en/topic/");
        request.addListener(this);

        Thread thread = new Thread(request);
        thread.start();
    }

    public void handle(Runnable runner) {
        ApiRequest request = (ApiRequest) runner;
        output = request.getOutput();
        notifyListeners();
        complete = true;
    }

    public boolean done() {
        return complete;
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof ObservableThread) {
            handle((Runnable) observable);
        }
    }

    public String getOutput() {
        return output;
    }
}
