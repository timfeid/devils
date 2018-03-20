package com.timfeid.devils;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Tim on 2/14/2018.
 * Allows a class to be observed by a listener
 */

public class Observable {
    /**
     * Our list of listeners to be notified upon thread completion.
     */
    private java.util.List<Listener> listeners = Collections.synchronizedList( new ArrayList<Listener>() );

    /**
     * Adds a listener to this object.
     * @param listener Adds a new listener to this object.
     */
    public void addListener( Listener listener ){
        listeners.add(listener);
    }
    /**
     * Removes a particular listener from this object, or does nothing if the listener
     * is not registered.
     * @param listener The listener to remove.
     */
    public void removeListener( Listener listener ){
        listeners.remove(listener);
    }
    /**
     * Notifies all listeners that the thread has completed.
     */
    final void notifyListeners() {
        synchronized ( listeners ){
            for (Listener listener : listeners) {
                listener.handle(this);
            }
        }
    }
}
