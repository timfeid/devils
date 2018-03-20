package com.timfeid.devils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * This abstract class implements the Runnable interface and can be used to notify listeners
 * when the runnable thread has completed. To use this class, first extend it and implement
 * the doRun function - the doRun function is where all work should be performed. Add any listener to update upon completion, then
 * create a new thread with this new object and run.
 * @author Greg Cope
 *
 */
public abstract class ObservableThread extends Observable implements Runnable {
    /**
     * An abstract function that children must implement. This function is where
     * all work - typically placed in the run of runnable - should be placed.
     */
    public abstract void doWork();

    /**
     * Implementation of the Runnable interface. This function first calls doRun(), then
     * notifies all listeners of completion.
     */
    public void run(){
        try {
            doWork();
        } finally {
            notifyListeners();
        }
    }
}
