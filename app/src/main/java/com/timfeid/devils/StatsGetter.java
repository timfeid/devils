package com.timfeid.devils;

import android.content.Context;

import java.util.Comparator;
import java.util.List;

interface StatsGetter {
    public List<Person> getPlayers(Roster roster);
    public String getStat(Person person);
    public Comparator<Person> getComparator();
    public List<String> getSortOptions(Context ctx);
}
