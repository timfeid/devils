package com.timfeid.devils;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StatsActivity extends HamburgerActivity {
    protected ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);
        getSupportActionBar().setTitle("Stats");

        final StatsPageAdapter adapter = new StatsPageAdapter(getSupportFragmentManager());
        PlayerStatsFragment skaters = new PlayerStatsFragment();
        skaters.setGetter(new StatsPlayerGetter() {
            @Override
            public String getStat(Person person) {
                return String.format(Locale.getDefault(), "%d PTS", person.getCurrentStats().points());
            }

            @Override
            public Comparator<Person> getComparator() {
                return new Roster.PointComparator();
            }
        });
        PlayerStatsFragment goalies = new PlayerStatsFragment();
        goalies.setGetter(new StatsGoalieGetter() {
            @Override
            public String getStat(Person person) {
                return String.format(Locale.getDefault(), "%d G", person.getCurrentStats().goals());
            }


            @Override
            public Comparator<Person> getComparator() {
                return new Roster.GoalsAgainstComparator();
            }
        });

        adapter.addFragment(skaters, "Skaters");
        adapter.addFragment(goalies, "Goalies");

        viewPager = findViewById(R.id.page_viewer);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class StatsPageAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public StatsPageAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
