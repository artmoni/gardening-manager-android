/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.seed.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.exception.GotsException;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.LikeStatus;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.PlanningHarvestAdapter;
import org.gots.seed.adapter.PlanningSowAdapter;
import org.gots.ui.fragment.LoginDialogFragment;

import java.util.Locale;

public class SeedWidgetLong extends RelativeLayout {
    private static final String TAG = SeedWidgetLong.class.getSimpleName();
    Context mContext;
    private BaseSeed mSeed;
    private TextView likeCount;
    private FloatingActionButton floatingActionLike;
    //    private FloatingActionButton floatingActionActions;
    private OnSeedWidgetLongClickListener mCallback;

    public SeedWidgetLong(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public SeedWidgetLong(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seed_widget_long, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    public interface OnSeedWidgetLongClickListener {
        public void onInformationClick(String url);

//        public void onLogClick();
    }

    public void setOnSeedWidgetLongClickListener(OnSeedWidgetLongClickListener seedWidgetLongClickListener) {
        mCallback = seedWidgetLongClickListener;
    }

    @SuppressWarnings("deprecation")
    private void setupView() {

        if (mSeed == null)
            return;

        View familyBackground = (View) findViewById(R.id.idSeedFamilyBackground);
        int familyImageRessource = 0;
        if (mSeed.getFamily() != null)
            familyImageRessource = getResources().getIdentifier(
                    "org.gots:drawable/family_" + mSeed.getFamily().toLowerCase(), null, null);

        if (familyImageRessource != 0)
            familyBackground.setBackgroundResource(familyImageRessource);
        else {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                familyBackground.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.family_unknown));
            } else {
                familyBackground.setBackground(mContext.getResources().getDrawable(R.drawable.family_unknown));
            }
        }
        familyBackground.setAlpha(0.8f);

        ImageView seedImage = (ImageView) findViewById(R.id.idSeedWidget2);
        Bitmap image = SeedUtil.getSeedBitmap(getGotsContext().getServerConfig().getFilesDir(), mSeed);

        if (image != null) {
            seedImage.setImageBitmap(image);
            if (image.getHeight() > image.getWidth())
                seedImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            seedImage.setImageResource(SeedUtil.getSeedDrawable(getContext(), mSeed));
        }

        TextView seedSpecie = (TextView) findViewById(R.id.IdSeedSpecie);
        seedSpecie.setText(SeedUtil.translateSpecie(mContext, mSeed));
        if (GotsPreferences.DEBUG)
            seedSpecie.setText("(" + mSeed.getSeedId() + ")" + SeedUtil.translateSpecie(mContext, mSeed));

        TextView seedVariety = (TextView) findViewById(R.id.IdSeedVariety);
        seedVariety.setText(mSeed.getVariety());

        PlanningWidget planningSow = (PlanningWidget) findViewById(R.id.IdSeedSowingPlanning);
        planningSow.setAdapter(new PlanningSowAdapter(mSeed));
        //
        PlanningWidget planningHarvest = (PlanningWidget) findViewById(R.id.IdSeedHarvestPlanning);
        planningHarvest.setAdapter(new PlanningHarvestAdapter(mSeed));

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.buttonInformation);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    String urlDescription;
                    if (mSeed.getUrlDescription() != null && !"null".equals(mSeed.getUrlDescription())) {
                        urlDescription = mSeed.getUrlDescription();
                    } else {
                        urlDescription = "http://" + Locale.getDefault().getLanguage() + ".wikipedia.org/wiki/" + mSeed.getSpecie();
                    }
                    mCallback.onInformationClick(urlDescription);
                }
            }
        });
        ImageView state = (ImageView) findViewById(R.id.imageStateValidation);
        if ("approved".equals(mSeed.getState()))
            state.setVisibility(View.VISIBLE);
        else
            state.setVisibility(View.GONE);

        TextView stock = (TextView) findViewById(R.id.idSeedStock);
        stock.setText(String.valueOf(mSeed.getNbSachet()));

        if (mSeed.getLanguage() != null && !"".equals(mSeed.getLanguage())) {
            ImageView flag = (ImageView) findViewById(R.id.IdSeedLanguage);
            int flagRessource = getResources().getIdentifier("org.gots:drawable/" + mSeed.getLanguage().toLowerCase(),
                    null, null);
            flag.setImageResource(flagRessource);
        }

//        floatingActionActions = (FloatingActionButton) findViewById(R.id.buttonActions);
//        floatingActionActions.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mCallback != null)
//                    mCallback.onLogClick();
//            }
//        });
//        if (mSeed instanceof GrowingSeed)
//            floatingActionActions.setVisibility(View.VISIBLE);
//        else floatingActionActions.setVisibility(View.GONE);

        likeCount = (TextView) findViewById(R.id.textSeedLike);
        floatingActionLike = (FloatingActionButton) findViewById(R.id.buttonLike);

        displayLikeStatus(mSeed.getLikeStatus());
        if (mSeed.getUUID() == null)
            this.floatingActionLike.setVisibility(View.GONE);

        floatingActionLike.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, LikeStatus>() {
                    GotsException exception = null;

                    @Override
                    protected LikeStatus doInBackground(Void... params) {
                        GotsSeedManager manager = GotsSeedManager.getInstance().initIfNew(mContext);
                        try {
                            return manager.like(mSeed, mSeed.getLikeStatus().getUserLikeStatus() == 1);
                        } catch (GotsException e) {
                            exception = e;
                            return null;
                        } catch (Exception e) {
                            Log.e(getClass().getName(), "" + e.getMessage(), e);
                            return null;
                        }
                    }

                    protected void onPostExecute(LikeStatus result) {
                        if (result == null && exception != null) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

                            // set title
                            alertDialogBuilder.setTitle(exception.getMessage());
                            alertDialogBuilder.setMessage(exception.getMessageDescription()).setCancelable(false).setPositiveButton(
                                    mContext.getResources().getString(R.string.login_connect),
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // Intent loginIntent = new Intent(mContext, LoginDialogFragment.class);
                                            // mContext.startActivity(loginIntent);
                                            LoginDialogFragment dialogFragment = new LoginDialogFragment();
                                            dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                                            dialogFragment.show(
                                                    ((FragmentActivity) mContext).getSupportFragmentManager(), "");
                                        }
                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();

                            alertDialog.show();

                            return;
                        }
                        mSeed.setLikeStatus(result);
                        displayLikeStatus(result);

                    }

                    ;
                }.execute();

            }
        });
    }

    protected void displayLikeStatus(LikeStatus likeStatus) {
        if (likeStatus == null) {
            Log.w(TAG, "likestatus is null");
            return;
        }
        likeCount.setTextColor(getResources().getColor(R.color.text_color_dark));
        likeCount.setText(String.valueOf(likeStatus.getLikesCount()));


        if (likeStatus.getUserLikeStatus() > 0) {
            floatingActionLike.setIconDrawable(getResources().getDrawable(R.drawable.ic_like));

        } else {
            floatingActionLike.setIconDrawable(getResources().getDrawable(R.drawable.ic_like_unknown));
        }
    }


    public void setSeed(BaseSeed seed) {
        this.mSeed = seed;
        setupView();
    }

}
