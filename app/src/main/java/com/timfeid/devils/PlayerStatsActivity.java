package com.timfeid.devils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import java.util.List;
import java.util.Locale;

public class PlayerStatsActivity extends HamburgerActivity {
    private RecyclerView topPointGetters;
    private RecyclerView.Adapter topPointGettersAdapter;
    private RecyclerView topGoalGetters;
    private RecyclerView.Adapter topGoalGettersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        getSupportActionBar().setTitle("Player Stats");

        topPointGetters = findViewById(R.id.top_point_getters);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        topPointGetters.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager pointsLayoutManager = new LinearLayoutManager(this);
        topPointGettersAdapter = new PointGettersAdapter();

        topPointGetters.setAdapter(topPointGettersAdapter);
        topPointGetters.setLayoutManager(pointsLayoutManager);

        RecyclerView.LayoutManager goalsLayoutManager = new LinearLayoutManager(this);
        topGoalGetters = findViewById(R.id.top_goal_getters);
        topGoalGetters.setLayoutManager(goalsLayoutManager);
        topGoalGetters.setAdapter(new GoalGettersAdapter());

        RecyclerView.LayoutManager assistsLayoutMananger = new LinearLayoutManager(this);
        topGoalGetters = findViewById(R.id.top_assist_getters);
        topGoalGetters.setLayoutManager(assistsLayoutMananger);
        topGoalGetters.setAdapter(new AssistGettersAdapter());

    }

    public class PointGettersAdapter extends RecyclerView.Adapter<PointGettersAdapter.ViewHolder> implements Listener {
        protected List<Person> roster;
        private Transformation circle = new CircleTransform();

        public class ViewHolder extends RecyclerView.ViewHolder {
            public LinearLayout mView;
            public TextView player;
            public TextView number;
            public TextView numberAndPos;
            public TextView total;
            public ImageView photo;

            public ViewHolder(LinearLayout v) {
                super(v);
                mView = v;
                player = mView.findViewById(R.id.player);
                number = mView.findViewById(R.id.number);
                numberAndPos = mView.findViewById(R.id.num_pos);
                total = mView.findViewById(R.id.total);
                photo = mView.findViewById(R.id.player_photo);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public PointGettersAdapter() {
            Team.getInstance().withPlayerStats(this);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public PointGettersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
            LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_stats_row, parent, false);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int itemPosition = topPointGetters.getChildLayoutPosition(v);
                    Person p = roster.get(itemPosition);
                    try {
                        Helpers.d("Clicked on "+p.getFullName());
                        Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                        intent.putExtra("personId", p.getId());
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                holder.mView.setDividerPadding(5);
                holder.mView.setPadding(20,20,20,20);
                Person person = roster.get(position);
                holder.number.setText(String.format(Locale.US, "%d", position+1));
                holder.player.setText(person.getFullName());
                holder.numberAndPos.setText(String.format(Locale.US, "#%s | %s", person.getNumber(), person.getPositionAbbreviation()));
                holder.total.setText(String.format(Locale.US, "%d", person.getCurrentStats().points()));
                Picasso.get().load(person.getImageUrl()).transform(circle).into(holder.photo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return 10;
        }

        public void handle(final PlayerStats playerStats) {
            roster = playerStats.getRoster().getPlayers();
            Collections.sort(roster, new Roster.PointComparator());
        }

        @Override
        public void handle(Observable observable) {
            if (observable instanceof PlayerStats) {
                handle((PlayerStats) observable);
            }
        }
    }

    class GoalGettersAdapter extends PointGettersAdapter {
        @Override
        public void handle(final PlayerStats playerStats) {
            roster = playerStats.getRoster().getPlayers();
            Collections.sort(roster, new Roster.GoalsComparator());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            Person person = roster.get(position);
            holder.total.setText(String.format(Locale.US, "%d", person.getCurrentStats().goals()));
        }
    }

    class AssistGettersAdapter extends PointGettersAdapter {
        @Override
        public void handle(final PlayerStats playerStats) {
            roster = playerStats.getRoster().getPlayers();
            Collections.sort(roster, new Roster.AssistsComparator());
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            Person person = roster.get(position);
            holder.total.setText(String.format(Locale.US, "%d", person.getCurrentStats().assists()));
        }
    }
}
