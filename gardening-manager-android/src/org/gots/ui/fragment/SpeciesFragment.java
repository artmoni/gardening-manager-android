package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.gots.R;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.adapters.DocumentAttributeResolver;
import org.nuxeo.android.adapters.DocumentsListAdapter;
import org.nuxeo.android.documentprovider.LazyDocumentsList;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.fragments.BaseDocLayoutFragAct;
import org.nuxeo.android.fragments.BaseDocumentsListFragment;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sfleury on 09/07/15.
 */
public class SpeciesFragment extends BaseDocumentsListFragment {

    private GotsSeedManager seedManager;
    private OnSpeciesSelected mCallBack;

    public interface OnSpeciesSelected {
        public void onSpeciesClicked(BotanicSpecie botanicSpecie);
    }

    @Override
    public void onAttach(Activity activity) {
        if (getActivity() instanceof OnSpeciesSelected)
            mCallBack = (OnSpeciesSelected) getActivity();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_species, null);
        listView = (ListView) v.findViewById(R.id.listViewSpecies);
        seedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
//        listView.setExpanded(true);
        return v;
    }

//    @Override
//    public void update() {
//        runAsyncDataRetrieval();
//    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {

        super.onNuxeoDataRetrievalStarted();
    }

//    @Override
//    protected Object retrieveNuxeoData() throws Exception {
//        return seedManager.getSpecies(false);
//    }

//    @Override
//    protected void onNuxeoDataRetrieved(final Object data) {
//        List<BotanicSpecie> botanicSpecies = (List<BotanicSpecie>) data;
//        final ListSpeciesAdapter listSpeciesAdapter = new ListSpeciesAdapter(getActivity(), botanicSpecies
//        );
//        listView.setAdapter(listSpeciesAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                    mCallback.onSpeciesSelected(listSpeciesAdapter.getItem(position));
////                mSeed.setSpecie(listSpeciesAdapter.getItem(position).getSpecieName());
//                mCallBack.onSpeciesClicked(listSpeciesAdapter.getItem(position));
//                listView.setItemChecked(position, true);
//                listSpeciesAdapter.notifyDataSetChanged();
//                listView.setSelection(position);
//            }
//
//
//        });
//
//        super.onNuxeoDataRetrieved(data);
//    }

    @Override
    protected LazyUpdatableDocumentsList fetchDocumentsList(byte b, String s) throws Exception {
        Session session = getNuxeoContext().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        Documents docSpecies = service.query(
                "SELECT * FROM Species WHERE ecm:currentLifeCycleState != \"deleted\"", null,
                null, "*", 0, 50, b);
        return docSpecies.asUpdatableDocumentsList();
    }

    @Override
    protected void displayDocumentList(final ListView listView, LazyDocumentsList lazyDocumentsList) {
        DocumentsListAdapter adapter = new DocumentsListAdapter(getActivity(),
                documentsList, R.layout.list_species_simple, getMapping());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mCallback.onSpeciesSelected(listSpeciesAdapter.getItem(position));
//                mSeed.setSpecie(listSpeciesAdapter.getItem(position).getSpecieName());
//                mCallBack.onSpeciesClicked(listSpeciesAdapter.getItem(position));
//                listView.setItemChecked(position, true);
//                listSpeciesAdapter.notifyDataSetChanged();
//                listView.setSelection(position);
            }


        });
    }

    private Map<Integer, String> getMapping() {
        Map<Integer, String> mapping = new HashMap<Integer, String>();
        mapping.put(R.id.textViewSpecies, "dc:title");
        mapping.put(R.id.imageViewSpecies, DocumentAttributeResolver.PICTUREURI
                + ":Small");
        return mapping;
    }

    @Override
    protected Document initNewDocument(String s) {
        return null;
    }

    @Override
    protected Class<? extends BaseDocLayoutFragAct> getEditActivityClass() {
        return null;
    }


}
