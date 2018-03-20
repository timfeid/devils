package com.timfeid.devils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Tim on 3/19/2018.
 * Media fragment
 */

public abstract class MediaFragment extends Fragment {
    protected View rootView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_media, container, false);

        populateView();

        return rootView;
    }

    void populateView() {
        mRecyclerView = rootView.findViewById(R.id.media);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    public abstract RecyclerView.Adapter getAdapter();
}
