package in.programmeraki.hbt.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;

import in.programmeraki.hbt.Common;
import in.programmeraki.hbt.HistoryFragment;
import in.programmeraki.hbt.R;
import in.programmeraki.hbt.adapter.HistoryViewPagerAdapter;

public class HistoryActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

        Common.instance.setUpAppDatabase(getApplicationContext());

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tablayout_textview,null);
            tabLayout.getTabAt(i).setCustomView(tv);
        }

        findViewById(R.id.back_iv).setOnClickListener(view -> {
            finish();
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        HistoryViewPagerAdapter adapter = new HistoryViewPagerAdapter(getSupportFragmentManager());

        HistoryFragment todayFragment = new HistoryFragment();
        HistoryFragment lastWeekFragment = new HistoryFragment();
        lastWeekFragment.isLastWeekType = true;

        adapter.addFragment(todayFragment, "Today");
        adapter.addFragment(lastWeekFragment, "Last Week");
        viewPager.setAdapter(adapter);
    }
}
