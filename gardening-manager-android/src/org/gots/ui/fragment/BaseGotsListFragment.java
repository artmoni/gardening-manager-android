package org.gots.ui.fragment;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.bean.DefaultGarden;
import org.gots.broadcast.BroadCastMessages;
import org.gots.exception.GardenNotFoundException;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.fragments.BaseListFragment;
import org.nuxeo.android.fragments.BaseNuxeoFragment;

import java.lang.reflect.Field;
import java.util.Locale;

public abstract class BaseGotsListFragment extends BaseListFragment {

    protected GotsSeedManager seedProvider;
    protected GotsAllotmentManager allotmentManager;
    protected GotsGardenManager gardenManager;
    protected GotsActionSeedProvider actionseedProvider;
    protected GotsGrowingSeedManager growingSeedManager;
    private GardenInterface currentGarden;

    public BaseGotsListFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        seedProvider = GotsSeedManager.getInstance();
        seedProvider.initIfNew(getActivity());
        allotmentManager = GotsAllotmentManager.getInstance();
        allotmentManager.initIfNew(getActivity());
        gardenManager = GotsGardenManager.getInstance();
        gardenManager.initIfNew(getActivity());

        actionseedProvider = GotsActionSeedManager.getInstance().initIfNew(getActivity());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }

    public abstract void update();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.simple_listview, null);
        listView = (ListView) inflate.findViewById(R.id.listView);
        return inflate;
    }

    /**
     * @return currentGarden or DefaultGarden instance if null
     */
    protected GardenInterface getCurrentGarden() {
        try {
            currentGarden = GotsGardenManager.getInstance().initIfNew(getActivity()).getCurrentGarden();
        } catch (GardenNotFoundException e) {
            currentGarden = new DefaultGarden(getActivity(), new Address(Locale.getDefault()));
        }
        return currentGarden;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // BUGFIX
        // http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


}