package in.programmeraki.hbt.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import in.programmeraki.hbt.fragment.CommonAlertsFragment;
import in.programmeraki.hbt.model.TrackerAlert;

public class AlertsViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public AlertsViewPagerAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        CommonAlertsFragment fragment = (CommonAlertsFragment) mFragmentList.get(position);
        if(position == 0){
            fragment.type = TrackerAlert.pulseType;
        } else {
            fragment.type = TrackerAlert.tempType;
        }
        return fragment;
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
