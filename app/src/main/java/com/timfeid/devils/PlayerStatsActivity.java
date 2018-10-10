package com.timfeid.devils;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class PlayerStatsActivity extends HamburgerActivity {
    protected ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);
        getSupportActionBar().setTitle("Player Stats");

        final PlayerStatsPageAdapter adapter = new PlayerStatsPageAdapter(getSupportFragmentManager());
        Helpers.d("HEYO");
        PlayerStatsFragment points = new PlayerStatsFragment();
        points.setComparator(new Roster.PointComparator());

        PlayerStatsFragment goals = new PlayerStatsFragment();
        goals.setComparator(new Roster.GoalsComparator());

        PlayerStatsFragment assists = new PlayerStatsFragment();
        assists.setComparator(new Roster.AssistsComparator());

        adapter.addFragment(points, "Points");
        adapter.addFragment(goals, "Goals");
        adapter.addFragment(assists, "Assists");

        viewPager = findViewById(R.id.page_viewer);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public class PlayerStatsPageAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public PlayerStatsPageAdapter(FragmentManager manager) {
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
