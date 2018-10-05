package com.timfeid.devils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Tim on 2/10/2018.
 */

abstract class GameLayout {
    protected ConstraintLayout layout;

    private Transformation circle = new CircleTransform();
    private TextView awayTeamCity;
    private TextView awayTeamRecord;
    private TextView homeTeamCity;
    private TextView homeTeamRecord;
    private RelativeLayout loader;
    protected TextView gameDate;
    protected TextView gameTime;
    protected GameInterface game;
    protected View rootView;
    protected Activity activity;

    public GameLayout(GameInterface game, View rootView, Activity activity) {
        this.game = game;
        this.rootView = rootView;
        this.activity = activity;
    }

    public final void build() {
        try {
            add();
            initMainView();
            initView();
            populateAwayTeam();
            populateHomeTeam();
            fill();
            doneLoading();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void add() throws JSONException {
        layout = (ConstraintLayout) getLayoutInflater().inflate(this.getLayoutId(), null);
        ViewGroup vg = (ViewGroup) rootView;
        vg.addView(layout);
    }

    private void populateAwayTeam() throws JSONException {
        setTextViewTextByTag("AWAY_TEAM_NAME", game.getAwayTeamName());
        setImageViewByTag("AWAY_TEAM_LOGO", getLogoResourceFor(game.getAwayId()));
        awayTeamCity.setText(game.getAwayTeamCity());
        awayTeamRecord.setText(game.getFormattedRecord("away"));
    }

    private void populateHomeTeam() throws JSONException {
        setTextViewTextByTag("HOME_TEAM_NAME", game.getHomeTeamName());
        setImageViewByTag("HOME_TEAM_LOGO", getLogoResourceFor(game.getHomeId()));
        homeTeamCity.setText(game.getHomeTeamCity());
        homeTeamRecord.setText(game.getFormattedRecord("home"));


        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.US);

        gameDate.setText(dateFormat.format(game.getDate()));
        gameTime.setText(timeFormat.format(game.getDate()));
    }

    private void doneLoading() {
        if (loader != null) {
            loader.setVisibility(View.GONE);
        }
    }

    private void initMainView() {
        awayTeamCity = (TextView) findViewById(R.id.awayTeamCity);
        awayTeamRecord = (TextView) findViewById(R.id.awayTeamRecord);

        homeTeamCity = (TextView) findViewById(R.id.homeTeamCity);
        homeTeamRecord = (TextView) findViewById(R.id.homeTeamRecord);

        gameDate = (TextView) findViewById(R.id.gameDate);
        gameTime = (TextView) findViewById(R.id.gameTime);

        loader = rootView.findViewById(R.id.loading);
    }

    public View findViewById(int id) {
        View layout = activity instanceof FragmentActivity ? rootView : this.layout;
        return layout.findViewById(id);
    }

    public View findViewById(String ident) {
        View layout = activity instanceof FragmentActivity ? rootView : this.layout;
        int id = findId(ident);

        return layout.findViewById(id);
    }

    public int findId(String ident) {
        return getResources().getIdentifier(ident,
                "id", getActivity().getPackageName());
    }



    abstract int getLayoutId();
    abstract void initView();
    abstract void fill() throws JSONException;
    protected void setTextViewTextByTag(String tag, String text) {
        for (View view : getViewsByTag((ViewGroup) rootView, tag)) {
            TextView v = (TextView) view;
            v.setText(text);
        }
    }

    private void setImageViewByTag(String tag, int resourceId) {
        for (View view : getViewsByTag((ViewGroup) rootView, tag)) {
            ImageView v = (ImageView) view;
            v.setImageResource(resourceId);
        }
    }

    private ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(activity);
    }

    public Activity getActivity() {
        return activity;
    }

    public Resources getResources() {
        return activity.getResources();
    }

    private int getLogoResourceFor(Integer id) {
        return getResources().getIdentifier("team_" + id.toString() +"_20172018_dark",
                "drawable", activity.getPackageName());
    }

    void imageCircleUrl(ImageView imageView, String url) {
        Picasso.get().load(url).into(imageView);
    }

    String getImageFor(int playerId) {
        return "https://nhl.bamcontent.com/images/headshots/current/60x60/"+playerId+".png";
    }
}