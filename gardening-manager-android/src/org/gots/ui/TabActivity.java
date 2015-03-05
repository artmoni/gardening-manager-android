package org.gots.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;

public abstract class TabActivity extends BaseGotsActivity {

    protected static final class TabInfo {
        private final Fragment fragment;

        private final Bundle args;

        TabInfo(Fragment f, Bundle _args) {
            fragment = f;
            args = _args;
        }
    }

    protected ViewPager mViewPager;

    private TabsAdapter mFragmentAdapter;

    public TabActivity() {
        super();
    }

    protected void addTab(Fragment fragment, String title) {
        mFragmentAdapter.addTab(getSupportActionBar().newTab().setText(title), fragment, getIntent().getExtras());
    }

    protected void removeTab(Fragment fragment) {
        mFragmentAdapter.remoteTab(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        mViewPager = getViewPager();

        mFragmentAdapter = new TabsAdapter(this, mViewPager);
        super.onPostCreate(savedInstanceState);
    }

    protected abstract ViewPager getViewPager();

    protected int getSelectedTab() {
        return mViewPager.getCurrentItem();
    }

    protected Fragment getCurrentFragment() {
        return mFragmentAdapter.getItem(mViewPager.getCurrentItem());
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    protected class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {

        private final ActionBar mActionBar;

        private final ViewPager mViewPager;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        private List<Fragment> fragments = new ArrayList<>();

        public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Fragment fragment, Bundle args) {
            TabInfo info = new TabInfo(fragment, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            fragments.add(fragment);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        private void remoteTab(Fragment fragment) {
            for (int i = 0; i < fragments.size(); i++) {
                Fragment fragment2 = fragments.get(i);
                if (fragment.equals(fragment2)) {
                    mTabs.remove(i);
                    notifyDataSetChanged();
                    break;
                }
            }

        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            // TabInfo info = mTabs.get(position);
            // // Fragment fragment = Fragment.instantiate(mContext, info.fragment.getName(), info.args);
            // fragments.get(position);
            // fragment.setArguments(bundle);
            return fragments.get(position);
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }
    }

}