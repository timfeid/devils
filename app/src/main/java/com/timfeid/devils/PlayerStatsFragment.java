package com.timfeid.devils;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PlayerStatsFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    protected StatsGetter getter;
    protected View rootView;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter recycleAdapter;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_stats, container, false);

        populateView();

        return rootView;
    }

    void populateView() {
        recyclerView = rootView.findViewById(R.id.media);
        Spinner spinner = rootView.findViewById(R.id.spinner);
        FontSpinnerAdapter spinnerAdapter = new FontSpinnerAdapter(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getter.getSortOptions(getContext())
        );
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recycleAdapter = getAdapter();
        recyclerView.setAdapter(recycleAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return new PlayerStatsAdapter(getActivity(), this.getter);
    }

    public void setGetter(StatsGetter getter) {
        this.getter = getter;
    }

    public void reset(StatsGetter getter) {
        this.getter = getter;
        resetAdapter();
    }

    public void resetAdapter() {
        recycleAdapter = getAdapter();
        recyclerView.setAdapter(getAdapter());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        switch (parent.getItemAtPosition(pos).toString()) {
            case "Points":
                reset(new StatsPlayerGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%d", person.getCurrentStats().points());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.PointComparator();
                    }
                });
                break;

            case "Goals":
                reset(new StatsPlayerGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%d", person.getCurrentStats().goals());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.GoalsAgainstComparator();
                    }
                });
                break;

            case "Assists":
                reset(new StatsPlayerGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%d", person.getCurrentStats().assists());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.AssistsComparator();
                    }
                });
                break;

            case "Time On Ice":
                reset(new StatsPlayerGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%s", person.getCurrentStats().timeOnIce());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.TimeOnIceComparator();
                    }
                });
                break;

            case "Save %":
                reset(new StatsGoalieGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%.2f%%", person.getCurrentStats().goalAgainstAverage());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.GoalsComparator();
                    }
                });
                break;

            case "Games Started":
                reset(new StatsGoalieGetter() {
                    @Override
                    public String getStat(Person person) {
                        return String.format(Locale.getDefault(), "%d", person.getCurrentStats().games());
                    }

                    @Override
                    public Comparator<Person> getComparator() {
                        return new Roster.GoalsComparator();
                    }
                });
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
