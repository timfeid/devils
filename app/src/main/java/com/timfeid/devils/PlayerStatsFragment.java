package com.timfeid.devils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

public class PlayerStatsFragment extends Fragment {
    protected Comparator<Person> comparator;
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media, container, false);

        populateView();

        return rootView;
    }

    void populateView() {
        RecyclerView mRecyclerView = rootView.findViewById(R.id.media);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return new PlayerStatsAdapter(getActivity(), comparator);
    }

    public void setComparator(Comparator<Person> comparator) {
        this.comparator = comparator;
    }
}
