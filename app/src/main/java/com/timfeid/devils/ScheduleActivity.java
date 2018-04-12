package com.timfeid.devils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;

public class ScheduleActivity extends HamburgerActivity implements GameFragment.OnFragmentInteractionListener {

    private static final String TAG = "ScheduleActivity";
    protected ViewPager viewPager;
    protected Schedule schedule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_schedule);

        getSupportActionBar().setTitle("Schedule");

        final SchedulePageAdapter adapter = new SchedulePageAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.game_view_pager);
        viewPager.setAdapter(adapter);
        setCurrentTab();
    }

    protected void setCurrentTab() {
        if (viewPager == null || schedule == null) {
            return;
        }

        for (int i = 0; i < schedule.getGames().size(); i++) {
            try {
                if (!schedule.getGame(i).getCodedGameState().equals("7")) {
                    viewPager.setCurrentItem(i);
                    final int index = i;
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNotificationChannel() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel("devils_goals", "New Jersey Devils", importance);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class SchedulePageAdapter extends FragmentStatePagerAdapter implements Listener {

        private int tabTotal = 1;

        public SchedulePageAdapter(FragmentManager fm) {
            super(fm);
            Team team = Team.getInstance();
            team.withSchedule(this);
        }

        @Override
        public Fragment getItem(int position) {
            GameFragment gameFragment = GameFragment.newInstance(position);

            return gameFragment;
        }

        @Override
        public int getCount() {
            return tabTotal;
        }

        public void handle(Schedule mySchedule) {
            schedule = mySchedule;
            tabTotal = schedule.getGames().size();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                    setCurrentTab();
                }
            });
        }

        @Override
        public void handle(Observable observable) {
            if (observable instanceof Schedule) {
                handle((Schedule) observable);
            }
        }
    }
}

