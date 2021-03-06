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

    List<Person> getGoalies () {
        List<Person> goalies = new ArrayList<>();
        for (Person person : roster) {
            try {
                if (person.isGoalie()) {
                    goalies.add(person);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return goalies;
    }

    public List<Person> getSkaters() {
        List<Person> skaters = new ArrayList<>();
        for (Person person : roster) {
            try {
                if (!person.isGoalie()) {
                    skaters.add(person);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return skaters;
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

    static class GoalsAgainstComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            if (a.getCurrentStats().games() == 0) {
                return 1;
            }
            if (b.getCurrentStats().games() == 0) {
                return -1;
            }
            return Double.compare(a.getCurrentStats().goalAgainstAverage(), b.getCurrentStats().goalAgainstAverage());
        }
    }


    static class GamesStartedComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            return Double.compare(b.getCurrentStats().gamesStarted(), a.getCurrentStats().gamesStarted());
        }
    }


    static class SavePercentageComparator implements Comparator<Person> {

        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            return Double.compare(b.getCurrentStats().savePercentage(), a.getCurrentStats().savePercentage());
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

    public static class TimeOnIceComparator implements Comparator<Person> {
        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            int bInt = b.getCurrentStats().timeOnIcePerGameInSeconds();
            int aInt = a.getCurrentStats().timeOnIcePerGameInSeconds();

            return Integer.compare(bInt, aInt);
        }
    }

    public static class PenaltyMinutesComparator implements Comparator<Person> {
        @Override
        public int compare(Person a, Person b) {
            if (a.getCurrentStats() == null) {
                return 1;
            }
            if (b.getCurrentStats() == null) {
                return -1;
            }
            int bInt = Integer.parseInt(b.getCurrentStats().penaltyMinutes());
            int aInt = Integer.parseInt(a.getCurrentStats().penaltyMinutes());

            return Integer.compare(bInt, aInt);
        }
    }
}


