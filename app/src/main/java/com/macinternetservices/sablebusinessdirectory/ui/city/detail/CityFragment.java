package com.macinternetservices.sablebusinessdirectory.ui.city.detail;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.macinternetservices.sablebusinessdirectory.Config;
import com.macinternetservices.sablebusinessdirectory.R;
import com.macinternetservices.sablebusinessdirectory.binding.FragmentDataBindingComponent;
import com.macinternetservices.sablebusinessdirectory.databinding.FragmentCityBinding;
import com.macinternetservices.sablebusinessdirectory.ui.common.PSFragment;
import com.macinternetservices.sablebusinessdirectory.utils.AutoClearedValue;
import com.macinternetservices.sablebusinessdirectory.utils.Utils;
import com.macinternetservices.sablebusinessdirectory.viewmodel.city.CityViewModel;
import com.macinternetservices.sablebusinessdirectory.viewobject.City;
import com.macinternetservices.sablebusinessdirectory.viewobject.holder.CityParameterHolder;


public class CityFragment extends PSFragment implements OnMapReadyCallback {

    //region Variables

    private final androidx.databinding.DataBindingComponent dataBindingComponent = new FragmentDataBindingComponent(this);

    private GoogleMap map;
    private MarkerOptions markerOptions = new MarkerOptions();
    private CityViewModel cityViewModel;
    private Bundle bundle;

    private CityParameterHolder cityParameterHolder = new CityParameterHolder().getRecentCities();

    @VisibleForTesting
    private AutoClearedValue<FragmentCityBinding> binding;
    //endregion

    //region Override Methods
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        FragmentCityBinding dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_city, container, false, dataBindingComponent);
        binding = new AutoClearedValue<>(this, dataBinding);
        setHasOptionsMenu(true);

        bundle = savedInstanceState;
        binding.get().mapView.onCreate(savedInstanceState);
        return binding.get().getRoot();
    }

    private void initializeMap(Bundle savedInstanceState) {
        try {
            if (this.getActivity() != null) {
                MapsInitializer.initialize(this.getActivity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.get().mapView.onCreate(savedInstanceState);
        binding.get().mapView.onResume();

        binding.get().mapView.getMapAsync(googleMap -> {
            map = googleMap;

            map.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.valueOf(cityViewModel.lat), Double.valueOf(cityViewModel.lng)))
                    .title("City Name"));

            //zoom
            if (!cityViewModel.lat.isEmpty() && !cityViewModel.lng.isEmpty()) {
                int zoomlevel = 8;
                // Animating to the touched position
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(cityViewModel.lat), Double.parseDouble(cityViewModel.lng)), zoomlevel));
//                map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(Double.valueOf(cityViewModel.lat), Double.valueOf(cityViewModel.lng))).zoom(zoomlevel).bearing(10).tilt(10).build()));
            }

        });
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        if(binding != null) {
            if (binding.get() != null) {
                if (binding.get().mapView != null) {

                    binding.get().mapView.onDestroy();

                    if (map != null) {
                        map.clear();
                    }

                }
            }
        }
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        binding.get().mapView.onLowMemory();
        super.onLowMemory();

    }

    @Override
    public void onPause() {
        binding.get().mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        binding.get().mapView.onResume();
        super.onResume();
    }

    @Override
    protected void initUIAndActions() {

        if (Config.SHOW_ADMOB && connectivity.isConnected()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            binding.get().adView.loadAd(adRequest);
        } else {
            binding.get().adView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initViewModels() {
        cityViewModel = new ViewModelProvider(this, viewModelFactory).get(CityViewModel.class);
    }

    @Override
    protected void initAdapters() {
    }

    //  private  void replaceAboutUsData()
    @Override
    protected void initData() {

        cityParameterHolder.id = selectedCityId;
        cityViewModel.setCityListObj("10","0",cityParameterHolder);
        cityViewModel.getCityListData().observe(this, resource -> {

            if (resource != null) {

                Utils.psLog("Got Data" + resource.message + resource.toString());

                switch (resource.status) {
                    case LOADING:
                        // Loading State
                        // Data are from Local DB

                        if (resource.data != null) {

                            fadeIn(binding.get().getRoot());

//                            binding.get().setCity(resource.data.get(0));
//                            setAboutUsData(resource.data.get(0));
//                            addLatLong(resource.data.get(0));
                        }
                        break;
                    case SUCCESS:
                        // Success State
                        // Data are from Server

                        if (resource.data != null) {

                            binding.get().setCity(resource.data.get(0));
                            setAboutUsData(resource.data.get(0));
                            addLatLong(resource.data.get(0));
                            bindingMap();
                        }

                        break;
                    case ERROR:
                        // Error State

                        break;
                    default:
                        // Default

                        break;
                }

            } else {

                // Init Object or Empty Data
                Utils.psLog("Empty Data");

            }


            // we don't need any null checks here for the adapter since LiveData guarantees that
            // it won't call us if fragment is stopped or not started.
            if (resource != null) {
                Utils.psLog("Got Data Of About Us.");


            } else {
                //noinspection Constant Conditions
                Utils.psLog("No Data of About Us.");
            }
        });

    }

    private void bindingMap() {
        if(!cityViewModel.lat.isEmpty() && !cityViewModel.lng.isEmpty()) {
            initializeMap(bundle);
        }
    }

    private void addLatLong(City city) {
        cityViewModel.lat = city.lat;
        cityViewModel.lng = city.lng;
    }


    private void setAboutUsData(City city) {

        binding.get().setCity(city);

        cityViewModel.selectedCityId = city.id;

        // For Contact
        // For SourceAddress

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
    //endregion

}