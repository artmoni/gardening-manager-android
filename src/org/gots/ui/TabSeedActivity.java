/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.PhotoAction;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.help.HelpUriBuilder;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.view.SeedWidgetLong;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class TabSeedActivity extends SherlockFragmentActivity {
    ViewPager mViewPager;

    GrowingSeedInterface mSeed = null;

    private String urlDescription;

    private File cameraPicture;

    private PhotoAction photoAction;

    private Gallery pictureGallery;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seed_tab);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        // bar.setDisplayShowTitleEnabled(false);

        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // ********************** **********************
        if (getIntent().getExtras() == null) {
            Log.e("SeedActivity", "You must provide a org.gots.seed.id as an Extra Int");
            finish();
            return;
        }
        if (getIntent().getExtras().getInt("org.gots.seed.id") != 0) {
            TabHost mTabHost = (TabHost) findViewById(android.R.id.tabhost);

            int seedId = getIntent().getExtras().getInt("org.gots.seed.id");
            mSeed = GotsGrowingSeedManager.getInstance().initIfNew(this).getGrowingSeedById(seedId);
        } else if (getIntent().getExtras().getInt("org.gots.seed.vendorid") != 0) {
            int seedId = getIntent().getExtras().getInt("org.gots.seed.vendorid");
            GotsSeedProvider helper = new LocalSeedProvider(getApplicationContext());

            mSeed = (GrowingSeedInterface) helper.getSeedById(seedId);
        } else
            mSeed = new GrowingSeed(); // DEFAULT SEED
        //
        // if (getIntent().getExtras().getString("org.gots.seed.actionphoto") != "") {
        // PhotoAction photoAction = new PhotoAction(getApplicationContext());
        // Date now = new Date();
        // cameraPicture = photoAction.getImageFile(now);
        // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPicture));
        // // takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivityForResult(takePictureIntent, 0);
        //
        // // TODO The action must not be executed if activity for result has been canceled because no
        // // image
        // // has been taken but the database get the imagefile
        // // actionItem.execute(seed);

        // }

        pictureGallery = (Gallery) findViewById(R.id.idPictureGallery);

        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... params) {
                NuxeoActionSeedProvider provider = new NuxeoActionSeedProvider(getApplicationContext());
                List<File> imageFile = provider.getPicture(mSeed);

                return imageFile;
            }

            protected void onPostExecute(List<File> result) {
                if (result.size() > 0) {
                    pictureGallery.setSpacing(10);
                    pictureGallery.setAdapter(new GalleryImageAdapter(getApplicationContext(), result));
                } else
                    pictureGallery.setVisibility(View.GONE);
            };
        }.execute();

        bar.setTitle(mSeed.getSpecie());

        SeedWidgetLong seedWidget = (SeedWidgetLong) findViewById(R.id.IdSeedWidgetLong);
        seedWidget.setSeed(mSeed);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        TabsAdapter mTabsAdapter = new TabsAdapter(this, mViewPager);

        // ********************** Tab actions **********************
        if (mSeed.getGrowingSeedId() > 0) {
            mTabsAdapter.addTab(
                    bar.newTab().setTag("event_list").setText(getString(R.string.seed_description_tabmenu_actions)),
                    ListActionActivity.class, null);
        }
        // // ********************** Tab description **********************
        mTabsAdapter.addTab(
                bar.newTab().setTag("event_list").setText(getString(R.string.seed_description_tabmenu_detail)),
                SeedActivity.class, null);

        // ********************** Tab Wikipedia**********************
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            mTabsAdapter.addTab(
                    bar.newTab().setTag("event_list").setText(getString(R.string.seed_description_tabmenu_wikipedia)),
                    WebViewActivity.class, null);

            urlDescription = "http://" + Locale.getDefault().getLanguage() + ".wikipedia.org/wiki/" + mSeed.getSpecie();

        }

        if (!GotsPreferences.getInstance().initIfNew(getApplicationContext()).isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int arg1, Intent arg2) {
        if (requestCode == 0) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    GotsActionSeedManager.getInstance().initIfNew(getApplicationContext()).uploadPicture(mSeed,
                            cameraPicture);
                    // photoAction.execute(mSeed);
                    return null;
                }
            }.execute();

        }
        super.onActivityResult(requestCode, arg1, arg2);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // first saving my state, so the bundle wont be empty.
        // http://code.google.com/p/android/issues/detail?id=19917
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_seeddescription, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.help:
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
            startActivity(browserIntent);

            return true;
        case R.id.photo:
            photoAction = new PhotoAction(getApplicationContext());
            Date now = new Date();
            cameraPicture = photoAction.getImageFile(now);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPicture));
            // takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(takePictureIntent, 0);

            // TODO The action must not be executed if activity for result has been canceled because no
            // image
            // has been taken but the database get the imagefile
            // actionItem.execute(seed);
            return true;
        case R.id.delete:
            final DeleteAction deleteAction = new DeleteAction(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new AsyncTask<SeedActionInterface, Integer, Void>() {
                                @Override
                                protected Void doInBackground(SeedActionInterface... params) {
                                    SeedActionInterface actionItem = params[0];
                                    actionItem.execute(mSeed);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    Toast.makeText(getApplicationContext(), "action done", Toast.LENGTH_SHORT).show();
                                    TabSeedActivity.this.finish();
                                    super.onPostExecute(result);
                                }
                            }.execute(deleteAction);
                            sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    static final class TabInfo {
        private final Class<?> clss;

        private final Bundle args;

        TabInfo(Class<?> _class, Bundle _args) {
            clss = _class;
            args = _args;
        }
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
    public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {
        private final Context mContext;

        private final ActionBar mActionBar;

        private final ViewPager mViewPager;

        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getSupportActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt("org.gots.seed.id", mSeed.getSeedId());
            bundle.putInt("org.gots.growingseed.id", mSeed.getGrowingSeedId());
            bundle.putString("org.gots.seed.url", urlDescription);
            Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            fragment.setArguments(bundle);
            return fragment;
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
