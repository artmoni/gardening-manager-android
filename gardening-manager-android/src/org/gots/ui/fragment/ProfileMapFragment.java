package org.gots.ui.fragment;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.view.OnProfileEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProfileMapFragment extends BaseGotsFragment implements OnMapLongClickListener, OnMarkerClickListener {
    private static final String TAG = ProfileMapFragment.class.getSimpleName();
    OnProfileEventListener mCallback;
    HashMap<Marker, GardenInterface> markerGarden = new HashMap<>();
    private MapView mapView;
    private GoogleMap mMap;
    private GotsGardenManager gardenManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        View v = inflater.inflate(R.layout.profilemap_fragment, null, false);
        mapView = new MapView(getActivity());
//        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        MapsInitializer.initialize(getActivity());
        setUpMapIfNeeded();
        return mapView;
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnProfileEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProfileEventListener");
        }
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        super.onAttach(activity);
    }

    /**
     * ** Sets up the map if it is possible to do so ****
     */
    public void setUpMapIfNeeded() {

        mMap = mapView.getMap();
        if (mMap != null) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerClickListener(this);
        }
    }

    // /**
    // * This is where we can add markers or lines, add listeners or move the
    // * camera.
    // * <p>
    // * This should only be called once and when we are sure that {@link #mMap} is not null.
    // */
    // private void setUpMap() {
    // // For showing a move to my loction button
    // mMap.setMyLocationEnabled(true);
    // // For dropping a marker at a point on the Map
    // mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Home").snippet(
    // "Home Address"));
    // // For zooming automatically to the Dropped PIN Location
    // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12.0f));
    // }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    private void focusGardenOnMap(GardenInterface garden) {
        LatLng gardenPOI = new LatLng(garden.getGpsLatitude(), garden.getGpsLongitude());
        if (mMap != null && gardenPOI.latitude != 0 && gardenPOI.longitude != 0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gardenPOI, 17));
    }

    protected void displayGardensOnMap(List<GardenInterface> allGardens) {
        // map.setMyLocationEnabled(true);
        if (mMap == null)
            return;
        mMap.clear();
        for (GardenInterface garden : allGardens) {
            displayMarker(garden);
        }
    }

    private void displayMarker(GardenInterface garden) {
        LatLng gardenPOI = new LatLng(garden.getGpsLatitude(), garden.getGpsLongitude());

        MarkerOptions markerOption = new MarkerOptions().title(garden.getName()).snippet(garden.getName()).position(
                gardenPOI);
        if (garden.getId() == getCurrentGarden().getId())
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.bt_dashboard_profile_pressed));
        else if (garden.isIncredibleEdible())
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.bt_dashboard_incredible));
        else
            markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.bt_dashboard_profile));

        Marker marker = mMap.addMarker(markerOption);
        markerGarden.put(marker, garden);
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        setUpMapIfNeeded();
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return gardenManager.getMyGardens(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (isAdded()) {
            List<GardenInterface> myGardens = (List<GardenInterface>) data;
            displayGardensOnMap(myGardens);
            focusGardenOnMap(getCurrentGarden());
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        GardenInterface selectedGarden = markerGarden.get(arg0);
        mCallback.onProfileSelected(selectedGarden);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng arg0) {
        Geocoder geo = new Geocoder(getActivity());
        GardenInterface garden = getCurrentGarden();
        try {
            List<Address> adresses = geo.getFromLocation(arg0.latitude, arg0.longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address address = adresses.get(0);
                garden.setGpsLatitude(address.getLatitude());
                garden.setGpsLongitude(address.getLongitude());
                garden.setLocality(address.getLocality());
                garden.setAdminArea(address.getAdminArea());
                garden.setCountryName(address.getCountryName());
                garden.setCountryCode(address.getCountryCode());
                garden.setLocalityForecast(address.getLocality());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterator it = markerGarden.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            System.out.println(pairs.getKey() + " = " + pairs.getValue());
            if (((GardenInterface) pairs.getValue()).getId() == getCurrentGarden().getId()) {
                Marker currentMarker = (Marker) pairs.getKey();
                currentMarker.remove();
                markerGarden.remove(pairs.getKey());
                displayMarker(getCurrentGarden());
                break;
            }
            // it.remove(); // avoids a ConcurrentModificationException
        }
        mCallback.onProfileEdited(this, getCurrentGarden());
    }

}
