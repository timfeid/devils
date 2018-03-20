package com.timfeid.devils;

/**
 * Created by Tim on 2/6/2018.
 * Get video details from NHL api
 */

public class Videos extends Observable implements Listener {
    private boolean complete = false;
    private String output;

    Videos() {
        ApiRequest request = new ApiRequest("277437418");
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
