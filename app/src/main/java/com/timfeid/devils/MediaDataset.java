package com.timfeid.devils;

import android.app.Activity;
import android.view.View;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * Created by Tim on 3/19/2018.
 * Dataset for the Media fragment
 */

class MediaDataset {
    TreeMap<BigInteger, DataItem> items = new TreeMap<>();
    MediaDataset() {

    }

    void addItem(DataItem item) {
        items.put(item.getId(), item);
    }

    public DataItem get(int pos) {
        return (DataItem) items.descendingMap().values().toArray()[pos];
    }

    public int length() {
        return items.size();
    }

    interface DataItem {
        String getKicker();
        String getBlurb();
        String getImage();
        View.OnClickListener getListener(final Activity activity);

        BigInteger getId();
    }
}
