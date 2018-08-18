package in.programmeraki.hbt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.TextView;

import in.programmeraki.hbt.adapter.AlertsViewPagerAdapter;
import in.programmeraki.hbt.fragment.CommonAlertsFragment;

public class AlertsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);

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
        AlertsViewPagerAdapter adapter = new AlertsViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CommonAlertsFragment(), "Pulse");
        adapter.addFragment(new CommonAlertsFragment(), "Temp");
        viewPager.setAdapter(adapter);
    }

}
