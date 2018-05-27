package com.hqs.alx.hqsmapproject;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyMapFragment extends Fragment implements android.location.LocationListener, GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    MyBroadCastReciever myBroadCastReciever;

    MyPlaces myPlacePick, placeRecieved;
    MapFragment mainMapFragment;
    GoogleMap myMap;
    LocationManager locationManager;
    AutoCompleteTextView inputSearch;
    Marker markerName;
    int REQUEST_FINE_LOCATION_GRANTED = 1452;
    AlertDialog.Builder builder;
    PlaceAutocompleteAdapter myPlaceAutocompleteAdapter;
    GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    public MyMapFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_map, container, false);
        myBroadCastReciever = new MyBroadCastReciever();
        getActivity().registerReceiver(myBroadCastReciever,
                new IntentFilter("com.hqs.alx.hqsmapproject.CHOSEN_PLACE"));

      //  createAlertDialogForNewLocation();

        //adding the map fragment
        mainMapFragment = new MapFragment();
        getFragmentManager().beginTransaction().add(R.id.mainMapFragmentLayout, mainMapFragment).commit();

        //on click listner on the GPS icon - to get the device location
        (view.findViewById(R.id.gpsIV)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUserLocation();
            }
        });

        //inputSearch is the AutoCompleteTextView in which the user writes a name of a place to search for
        inputSearch = (AutoCompleteTextView) view.findViewById(R.id.input_searchFragment);
        inputSearch.setOnItemClickListener(myOnItemClickListener);

        locationManager = (LocationManager) view.getContext().getSystemService(LOCATION_SERVICE);

        /*checking if the user provided permission for using his GPS location
        if not, it asks for his permission,
        if does, it will start the initMap() method.
         */
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_GRANTED);
            //asking the users' permission to use his device location by GPS
            /*ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_GRANTED);*/
        } else {
            //permission was already granted so we can start initializing the map
            initMap();
        }

        return view;
    }

    //Alert Dialog with 2 options of saving locations: 1. current location   2. search for location
  /*  private void createAlertDialogForNewLocation() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.addANewPlace)).setMessage(getString(R.string.dialogBuilderMessage));
        builder.setPositiveButton(getString(R.string.currentLocation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton(getString(R.string.searchNewLocation), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNeutralButton(getString(R.string.cancell), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

    }*/

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(myBroadCastReciever, new IntentFilter("com.hqs.alx.hqsmapproject.CHOSEN_PLACE"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(myBroadCastReciever);
    }

    //checking if permmission was granted or not and deciding what would be executed in each case
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_FINE_LOCATION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            initMap();
        }else{
            Toast.makeText(getActivity(), getResources().getString(R.string.locationPermissionIsRequired), Toast.LENGTH_SHORT).show();
            MapFragment newMapFragment = new MapFragment();
            //MapFragment newMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragmentCheck);
            getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.mainMapFragmentLayout,newMapFragment ).commit();
            newMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    myMap = googleMap;
                    adjustSearchOptions();
                }
            });
        }
    }


    //Returning the devices location - First checking if permmission granted, if does, a Location object will be recieved and
    //the map would set to its location
    private void getUserLocation() {
        if(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_GRANTED);

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
        final Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location != null) {
            LatLng userCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            moveMapToLocation(myMap, userCurrentLocation, 8);
            myMap.setMyLocationEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(false);
        }else{
            Toast.makeText(getActivity(), getResources().getString(R.string.couldnt_find_location), Toast.LENGTH_SHORT).show();
        }

    }

    //moving the camera to a location - gets 4 variables
    private void moveMapToLocation (GoogleMap googleMap, LatLng ltln, float zoom, MyPlaces myPlaces){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));
        myMap = googleMap;

        //surrounded with try/catch becausr some information might be missing and give a NullPointerEx eror
        try{
            String adr = myPlaces.getAdress();
            final String snippet = "Address: " + adr + "\n"
                    + "Phone Number: " + myPlaces.getPhoneNumber() + "\n"
                    + "Rating: " + myPlaces.getRating() + "\n"
                    + "WebSite: " + myPlaces.getWebSiteString();
            if(markerName != null)
                markerName.remove();

            LatLng latLng = new LatLng(myPlaces.getLat(), myPlaces.getLon());
            MarkerOptions options = new MarkerOptions().position(latLng)
                    .title(myPlaces.getName()).snippet(snippet);
            myMap.addMarker(options);

           /* markerName = myMap.addMarker(new MarkerOptions().position(latLng)
                    .title(myPlaces.getName()).snippet(snippet));*/
            myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    createAlertDialogToSavePlace(myPlacePick, snippet);
                    builder.show();
                }
            });
        }catch (NullPointerException e){
            Log.d("Place Null info", "" + e.getMessage());
        }
    }

    private void createAlertDialogToSavePlace(final MyPlaces myPlace, String info){

        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialogMarkerSaveMessage)).setMessage(info);
        builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                MyPlaces newPlace = new MyPlaces(myPlace.getName(), myPlace.getAdress(), myPlace.getPhoneNumber(),
                                                 myPlace.getUniquePlaceId(), myPlace.getLat(), myPlace.getLon(), myPlace.getRating());
                newPlace.save();
            }
        });
        builder.setNeutralButton(getString(R.string.cancell), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

    }

    //moving the camera to a location - gets 3 variables
    private void moveMapToLocation (GoogleMap googleMap, LatLng ltln, float zoom){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ltln, zoom));
        myMap = googleMap;
    }

    //Adjusting the search options - connecting to GoogleApiClient and connecting an OnItemClickListener for the results
    private void adjustSearchOptions() {

        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage((FragmentActivity) getActivity(), this)
                    .build();
        }
        //An adpater for the results of the search - using "simple_expandable_list_item_2"
        myPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
        inputSearch.setAdapter(myPlaceAutocompleteAdapter);

        inputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    startSearching();
                    hideKeyboard();
                }
                return false;
            }
        });
    }

    //A method to hide the keyboard
    private void hideKeyboard() {
        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    //An actual search of the input - gets called only when a keyEvent has been clicked
    private void startSearching() {
        hideKeyboard();
        String searchString = inputSearch.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.d("error", "error searching for " + searchString);
        }
        if(list.size() > 0){
            Address address = list.get(0);
            Log.d("geoLocate:", "found a location: " + address.toString());
            LatLng adressLatlang = new LatLng(address.getLatitude(), address.getLongitude());
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom( adressLatlang, 8));

            //checking if there is already a marker - if does, delete it
            if(markerName != null){
                markerName.remove();
            }
            markerName = myMap.addMarker(new MarkerOptions().position(adressLatlang).title(address.getAddressLine(0)));

            /*MarkerOptions options = new MarkerOptions().position(adressLatlang)
                    .title(address.getAddressLine(0));
            myMap.addMarker(options);*/
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //getUserLocation();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //initiallizing the map
    private void initMap(){
        Log.d("initMap:", "initializing map");
        mainMapFragment.getMapAsync(this);
    }

    //what happens when the map has been initialized - checking permmission and returning the device place.
    //also, adjusting the search options (input search funcuality)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_GRANTED);
        }else if(placeRecieved == null) {
            getUserLocation();
        }else{
            LatLng latLng = new LatLng(placeRecieved.getLat(), placeRecieved.getLon());
            moveMapToLocation(myMap, latLng, 8, placeRecieved);
        }


        adjustSearchOptions();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*

    ------------------------------------------ Google Places API -------------------------------

    working with the google place api - getting information and adjusting the on clickListener for each item
     */

    private AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();

            //getting the clicked item from the adapter
            final AutocompletePrediction clickedItem = myPlaceAutocompleteAdapter.getItem(position);
            //getting the ID of the clicked item
            final String clickedItemID = clickedItem.getPlaceId();
            // Submiting a request to google api to search for a place by its' unique ID
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, clickedItemID);
            placeResult.setResultCallback(myDetailsCallback);
        }
    };

    // This is the result call back of the request above. will be called back if the result is successfuly received
    private ResultCallback<PlaceBuffer> myDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d("Result callBack: ", "Eror completing search " + places.getStatus().toString());
                // Google requires to release the PlaceBuffer object to prevent memory leak
                places.release();
            }else{
                Place place = places.get(0);
                //sorunded with try & catch because not for every location the information provided
                try{
                    //String placeWebsiteString = place.getWebsiteUri().toString();
                    double placeLat = place.getLatLng().latitude;
                    double placeLon = place.getLatLng().longitude;

                    myPlacePick = new MyPlaces(place.getName().toString(),
                            place.getAddress().toString(),
                            place.getPhoneNumber().toString(),
                            place.getId(),
                            placeLat,
                            placeLon,
                     //       placeWebsiteString,
                            place.getRating());
                    Log.d("Place Info: ", ""+ myPlacePick.toString());

                }catch (NullPointerException e){
                    Log.d("NullObject: ", "" + e.getMessage());
                }


                LatLng latLng = new LatLng(myPlacePick.getLat(), myPlacePick.getLon());
                moveMapToLocation(myMap, latLng, 8, myPlacePick);
                places.release();
            }
        }
    };

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getActivity(), "blalbalbal", Toast.LENGTH_SHORT).show();

    }

    class MyBroadCastReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            placeRecieved = intent.getParcelableExtra("myPlacesObject");
            LatLng latLng = new LatLng(placeRecieved.getLat(), placeRecieved.getLon());
            moveMapToLocation(myMap, latLng, 8, placeRecieved);
            Toast.makeText(context, ""+ placeRecieved.getName(), Toast.LENGTH_SHORT).show();
        }
    }
}
