package com.timfeid.devils;

/**
 * Created by Tim on 2/14/2018.
 * Interface allows an observable to be observed
 */

interface Listener {
    void handle(final Observable observable);
}
