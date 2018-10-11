package com.timfeid.devils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

class PlayerStatsAdapter extends RecyclerView.Adapter<PlayerStatsAdapter.ViewHolder> implements Listener {
    protected Activity activity;
    protected List<Person> roster;

    private StatsGetter getter;
    public Transformation circle;

    PlayerStatsAdapter(Activity activity, StatsGetter getter) {
        this.activity = activity;
        this.getter = getter;
        Team.getInstance().withPlayerStats(this);
        circle = new CircleTransform();
    }

    @NonNull
    @Override
    public PlayerStatsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_stats_row, parent, false);

        return new PlayerStatsAdapter.ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull PlayerStatsAdapter.ViewHolder holder, int position) {
        Person person = roster.get(position);
        try {
            holder.playerName.setText(person.getFullName());
            holder.playerStat.setText(getter.getStat(person));
            holder.playerNumber.setText(String.format(Locale.US, "#%s | %s", person.getNumber(), person.getPositionAbbreviation()));
            holder.card.setOnClickListener(new OnCardClicked(position));
            Picasso.get().load(person.getImageUrl()).transform(circle).into(holder.image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (roster == null) {
            return 0;
        }

        return roster.size();
    }

    public void handle(final PlayerStats playerStats) {
        roster = this.getter.getPlayers(playerStats.getRoster());
        Collections.sort(roster, this.getter.getComparator());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void handle(Observable observable) {
        if (observable instanceof PlayerStats) {
            handle((PlayerStats) observable);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playerName;
        public TextView playerNumber;
        public TextView playerStat;
        public CardView card;
        public ImageView image;
        public ViewHolder(LinearLayout v) {
            super(v);
            playerName = v.findViewById(R.id.player_name);
            image = v.findViewById(R.id.image);
            playerNumber = v.findViewById(R.id.player_number);
            playerStat = v.findViewById(R.id.stat);
            card = v.findViewById(R.id.card_view);
        }
    }

    public class OnCardClicked implements View.OnClickListener {
        Integer position;
        OnCardClicked(Integer position) {
            this.position = position;
        }

        public void onClick(View v) {
            Person person = roster.get(position);
            try {
                Helpers.d("Clicked on "+person.getFullName());
                Intent intent = new Intent(activity.getApplicationContext(), PlayerActivity.class);
                intent.putExtra("personId", person.getId());
                activity.startActivity(intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
