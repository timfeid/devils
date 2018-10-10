package com.timfeid.devils;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class HamburgerActivity extends AppCompatActivity {

    private static final String TAG = "Hamburger";
    private ActionBarDrawerToggle mToggle;

    @Override
    public void setContentView(@LayoutRes int layoutResId) {
        super.setContentView(layoutResId);
        onCreateDrawer();
    }

    protected void onCreateDrawer() {
        DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView mNavigationView = findViewById(R.id.hamburger_menu);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case (R.id.nav_home):
                        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(home);
                        break;
                    case (R.id.nav_player_stats):
                        Intent playerStats = new Intent(getApplicationContext(), PlayerStatsActivity.class);
                        startActivity(playerStats);
                        break;
                    case (R.id.nav_media):
                        Intent media = new Intent(getApplicationContext(), MediaActivity.class);
                        startActivity(media);
                        break;
                    case (R.id.nav_schedule):
                        Intent schedule = new Intent(getApplicationContext(), ScheduleActivity.class);
                        startActivity(schedule);
                        break;
                    case (R.id.nav_standings):
                        Intent standings = new Intent(getApplicationContext(), StandingsActivity.class);
                        startActivity(standings);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
}
