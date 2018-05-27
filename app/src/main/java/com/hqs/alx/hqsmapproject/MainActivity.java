package com.hqs.alx.hqsmapproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.orm.SugarContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaceChanger{

    MapFragment mainMapFragment;
    AlertDialog.Builder builder;
    MyMapFragment myMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SugarContext.init(this);
        createAlertDialogForNewLocation();
      /* MyPlaces newPlace = new MyPlaces("aaaaaaaaa", "asdasd 123", "05555555", "asdasdasdasdasd", 35,32, 2);

        MyPlaces newPlace2 = new MyPlaces("bbbbbbbb", "asdasd 123", "05555555", "asdasdasdasdasd", 35,35, 2);

        MyPlaces newPlace3 =new MyPlaces("cccccccc", "asdasd 123", "05555555", "asdasdasdasdasd", 35,37, 2);

        newPlace.save();
        newPlace2.save();
        newPlace3.save();*/


       myMapFragment = new MyMapFragment();
       getFragmentManager().beginTransaction().add(R.id.mainActivityMainLayout, myMapFragment).commit();

    }

    private void createAlertDialogForNewLocation() {
        builder = new AlertDialog.Builder(this);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.addLocationItem:
                builder.show();
                break;

            case R.id.myPlacesItem:
                ListFragment listFragment = new ListFragment();
                //getFragmentManager().beginTransaction().detach(myMapFragment).commit();
                getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.mainActivityMainLayout,listFragment ).commit();
        }
          return true;
    }

    public void changeFragments(final MyPlaces chosenPlace) {
        //adjustSearchOptions();
        final MyPlaces clickedPlace = chosenPlace;
        final LatLng currentPlaceLatlang = new LatLng(clickedPlace.getLat(), clickedPlace.getLon());
       /* if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {*/

            //MyMapFragment newMapFragment = new MyMapFragment();
            //getFragmentManager().beginTransaction().addToBackStack("replacing").replace(R.id.mainActivityMainLayout,newMapFragment ).commit();

        //getFragmentManager().beginTransaction().attach(myMapFragment).commit();

        myMapFragment = new MyMapFragment();
        getFragmentManager().beginTransaction().add(R.id.mainActivityMainLayout, myMapFragment).commit();

            /*newMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPlaceLatlang, 8));
                    googleMap.addMarker(new MarkerOptions().position(currentPlaceLatlang)
                            .title(clickedPlace.name));
                }
            });*/
       /* }else{
            Toast.makeText(this, "" + chosenPlace.name , Toast.LENGTH_SHORT).show();
            //moveMapToLocation(myMap, currentPlaceLatlang, 8);
        }*/
    }



}
