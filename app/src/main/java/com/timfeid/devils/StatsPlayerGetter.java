package com.timfeid.devils;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

abstract public class StatsPlayerGetter implements StatsGetter {
    public List<String> getSortOptions(Context ctx) {
        return Arrays.asList(ctx.getResources().getStringArray(R.array.skaters_sort));
    }

    public List<Person> getPlayers(Roster roster) {
        return roster.getSkaters();
    }
}
