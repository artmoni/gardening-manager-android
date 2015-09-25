package org.gots.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.ui.fragment.ActionsDoneListFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by sfleury on 25/09/15.
 */
public class GrowingPlantDescriptionActivity extends PlantDescriptionActivity {

    public static final String GOTS_GROWINGSEED_ID = "org.gots.seed.id";
    private GrowingSeed growingSeed;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        FloatingActionButton bottomRightLeft = new FloatingActionButton(getApplicationContext());
        bottomRightLeft.setSize(FloatingActionButton.SIZE_NORMAL);
        bottomRightLeft.setColorNormalResId(R.color.text_color_light);
        bottomRightLeft.setColorPressedResId(R.color.text_color_light_transparent);
        bottomRightLeft.setIcon(R.drawable.ic_menu_todo);

        bottomRightLeft.setStrokeVisible(false);
        bottomRightLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContentLayout(new ActionsDoneListFragment(), getIntent().getExtras());
            }
        });

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomRightLeft.setLayoutParams(params);
        ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        ((ViewGroup) root.getChildAt(0)).addView(bottomRightLeft);
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        int seedId = getIntent().getExtras().getInt(GOTS_GROWINGSEED_ID);
        growingSeed = GotsGrowingSeedManager.getInstance().initIfNew(this).getGrowingSeedById(seedId);
        return super.retrieveNuxeoData();
    }

    @Override
    protected BaseSeed getSeed() {
        return growingSeed.getPlant();
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        List<BaseAction> actionInterfaces = (List<BaseAction>) GotsActionManager.getInstance().initIfNew(
                getApplicationContext()).getActions(false);

        for (final BaseAction baseActionInterface : actionInterfaces) {
            if (!(baseActionInterface instanceof ActionOnSeed))
                continue;
            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(baseActionInterface.getName());
            int actionImageRessource = getResources().getIdentifier(
                    "org.gots:drawable/action_" + baseActionInterface.getName(), null, null);
            floatingItem.setRessourceId(actionImageRessource);

            floatingItem.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    new AsyncTask<Void, Void, ActionOnSeed>() {
                        @Override
                        protected ActionOnSeed doInBackground(Void... params) {
                            baseActionInterface.setDuration(7);
                            return actionseedProvider.insertAction(growingSeed, (ActionOnSeed) baseActionInterface);
                        }

                        protected void onPostExecute(ActionOnSeed actionOnSeed) {
                            showNotification(actionOnSeed.getName() + " planned on " + new SimpleDateFormat(" dd/MM/yyyy", Locale.FRANCE).format(actionOnSeed.getDateActionTodo()), false);
                        }
                    }.execute();
                    return true;
                }
            });
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, ActionOnSeed>() {
                        @Override
                        protected ActionOnSeed doInBackground(Void... params) {

                            return actionseedProvider.doAction((ActionOnSeed) baseActionInterface, growingSeed);
                        }

                        protected void onPostExecute(ActionOnSeed actionOnSeed) {
                            showNotification(actionOnSeed.getName() + " done on " + growingSeed.getPlant().getName(), false);
                        }
                    }.execute();
                }
            });
            floatingItems.add(floatingItem);
        }
        return floatingItems;
    }
}
