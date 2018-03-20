package com.timfeid.devils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tim on 2/4/2018.
 * Player object from the NHL api
 */

public class Player {
    private JSONObject person;

    Player(JSONObject player) {
        JSONObject player1 = player;
        try {
            person = player1.getJSONObject("person");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() throws JSONException {
        return person.getString("fullName");
    }
}
