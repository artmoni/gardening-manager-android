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
import java.io.IOException;
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
import org.gots.analytics.GotsAnalytics;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.utils.FileUtilities;

import android.app.Activity;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class TabSeedActivity extends ActionBarActivity {
    private static final int PICK_IMAGE = 0;

    protected static final String TAG = "TabSeedActivity";

    ViewPager mViewPager;

    GrowingSeedInterface mSeed = null;

    private String urlDescription;

    private File cameraPicture;

    private PhotoAction photoAction;

    private Gallery pictureGallery;

    GotsPreferences gotsPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {

            String cameraFilename = savedInstanceState.getString("CAMERA_FILENAME");
            if (cameraFilename != null)
                cameraPicture = new File(cameraFilename);
        }

        gotsPreferences = GotsPreferences.getInstance().initIfNew(getApplicationContext());
        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

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
            int seedId = getIntent().getExtras().getInt("org.gots.seed.id");
            mSeed = GotsGrowingSeedManager.getInstance().initIfNew(this).getGrowingSeedById(seedId);
        } else if (getIntent().getExtras().getInt("org.gots.seed.vendorid") != 0) {
            int seedId = getIntent().getExtras().getInt("org.gots.seed.vendorid");
            GotsSeedProvider helper = new LocalSeedProvider(getApplicationContext());

            mSeed = (GrowingSeedInterface) helper.getSeedById(seedId);
        } else
            mSeed = new GrowingSeed(); // DEFAULT SEED

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

        pictureGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File f = (File) arg0.getItemAtPosition(position);
                File dest = new File(gotsPreferences.getGARDENING_MANAGER_DIRECTORY(), f.getName());
                try {
                    FileUtilities.copy(f, dest);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(dest), "image/*");
                    startActivity(intent);
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage());
                }

            }
        });

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
            GotsAdvertisement ads = new GotsAdvertisement(getApplicationContext());

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_CANCELED)
            if (requestCode == PICK_IMAGE) {
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (cameraPicture != null)
            outState.putString("CAMERA_FILENAME", cameraPicture.getAbsolutePath());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_seeddescription, menu);
        if (mSeed.getGrowingSeedId() == 0) {
            menu.findItem(R.id.planning).setVisible(false);
            menu.findItem(R.id.photo).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
        }

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
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);
            return true;

        case R.id.planning:
            FragmentManager fm = getSupportFragmentManager();
            DialogFragment editNameDialog = new NewActionActivity();
            Bundle data = new Bundle();
            data.putInt("org.gots.seed.id", mSeed.getGrowingSeedId());
            editNameDialog.setArguments(data);
            editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            editNameDialog.show(fm, "fragment_edit_name");
            return true;
        case R.id.photo:
            photoAction = new PhotoAction(getApplicationContext());
            Date now = new Date();
            cameraPicture = new File(photoAction.getImageFile(now).getAbsolutePath());
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPicture));
            // takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(takePictureIntent, PICK_IMAGE);

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

        public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
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
