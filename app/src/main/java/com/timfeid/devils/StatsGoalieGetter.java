package com.timfeid.devils;

import android.content.Context;

import java.util.Arrays;
import java.util.List;

abstract public class StatsGoalieGetter implements StatsGetter {
    public List<Person> getPlayers(Roster roster) {
        return roster.getGoalies();
    }

    public List<String> getSortOptions(Context ctx) {
        return Arrays.asList(ctx.getResources().getStringArray(R.array.goalies_sort));
    }
}
