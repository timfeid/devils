package com.timfeid.devils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tim on 3/7/2018.
 * Get a person from the NHL api
 */

public class PersonGetter extends Observable implements Runnable, Listener {

    private int personId;
    protected Person person;
    void setPersonId(int personId) {
        this.personId = personId;
    }
    public Person getPerson() {
        return person;
    }

    @Override
    public void run() {
        ApiRequest request = new ApiRequest("people/"+personId);
        request.addParam("hydrate", "stats(splits=[yearByYear,careerRegularSeason,gameLog]),draft(team(abbreviation))");
        request.addListener(this);
        Thread thread = new Thread(request);
        thread.start();
    }

    private void handle(ApiRequest request) {
        try {
            person = new Person(new JSONObject(request.getOutput()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyListeners();
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof ApiRequest) {
            handle((ApiRequest) observable);
        }
    }
}
