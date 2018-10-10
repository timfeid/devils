package com.timfeid.devils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Tim on 2/10/2018.
 * Roster object from NHL api
 */
class Roster {
    private List<Person> roster = new ArrayList<>();
    Roster(JSONArray roster) {
        for (int i = 0; i < roster.length(); i++) {
            try {
                this.roster.add(new Person(roster.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(this.roster, new PointComparator());
    }

    List<Person> getPlayers() {
        return roster;
    }

    public Roster sortByGoals() {
        try {
            Roster roster = (Roster) this.clone();
            Collections.sort(roster.getPlayers(), new GoalsComparator());

            return roster;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return this;
        }
    }

    Person getPerson(int index) {
        return roster.get(index);
    }

    Person getPersonById(int id) throws JSONException {
        for (Person person : roster) {
            if (person.getId() == id) {
                return person;
            }
        }

        return null;
    }

    public Person get(int position) {
        return roster.get(position);
    }

    static class PointComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }

            return Integer.compare(b.getCurrentStats().points(), a.getCurrentStats().points());
        }
    }

    static class GoalsComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            return Integer.compare(b.getCurrentStats().goals(), a.getCurrentStats().goals());
        }
    }

    static class AssistsComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            return Integer.compare(b.getCurrentStats().assists(), a.getCurrentStats().assists());
        }
    }
}


