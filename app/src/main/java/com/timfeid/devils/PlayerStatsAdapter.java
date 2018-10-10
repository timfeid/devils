package com.timfeid.devils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

class PlayerStatsAdapter extends RecyclerView.Adapter<PlayerStatsAdapter.ViewHolder> implements Listener {
    protected Activity activity;
    protected List<Person> roster;
    private Comparator<Person> comparator;

    PlayerStatsAdapter(Activity activity, Comparator<Person> comparator) {
        this.activity = activity;
        this.comparator = comparator;
        Team.getInstance().withPlayerStats(this);
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
            Integer points = person.getCurrentStats() != null ? person.getCurrentStats().points() : 0;
            holder.playerName.setText(person.getFullName());
            holder.playerStat.setText(String.format(Locale.US, "%d", points));
            holder.playerNumber.setText(String.format(Locale.US, "#%s | %s", person.getNumber(), person.getPositionAbbreviation()));
            holder.card.setOnClickListener(new OnCardClicked(position));
            Picasso.get().load(person.getActionPhotoUrl()).into(holder.image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (position == 0) {
            Resources r = activity.getApplicationContext().getResources();
            int px = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16,
                    r.getDisplayMetrics()
            );
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.card.getLayoutParams();
            params.setMargins(px, px, px, px / 2);
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
        roster = playerStats.getRoster().getPlayers();
        Collections.sort(roster, this.comparator);
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
