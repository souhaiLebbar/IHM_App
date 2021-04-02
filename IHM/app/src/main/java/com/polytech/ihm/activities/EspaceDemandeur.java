package com.polytech.ihm.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.polytech.ihm.R;
import com.polytech.ihm.models.MapCustomInfoBubble;
import com.polytech.ihm.models.Util;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

public class EspaceDemandeur extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private MapView map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    //coordonée
    private double myLongitude;
    private double myLattitude;
    private MyLocationNewOverlay mLocationOverlay;
    //trajet
    private ArrayList<GeoPoint> trajet;

    //menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Button menuIcon;

    //fin
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().
                load(
                        getApplicationContext(),
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                );
        setContentView(R.layout.espace_demandeur);
        map = findViewById(R.id.map);
        // Hooks Drawer Menu
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu);

        //TileSourceFactory.PUBLIC_TRANSPORT
        //http://leaflet-extras.github.io/leaflet-providers/preview/
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setBuiltInZoomControls(true);            //zoomable
        GeoPoint startPoint = new GeoPoint(43.61572296415799, 7.071842570348114);
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(startPoint);

        map.getOverlays().add(addMarker(new GeoPoint(43.61572296415799, 7.071842570348114)));
        map.invalidate();
        //navigation Drawer
        navifationDrawer();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private Marker addMarker(GeoPoint gp) {
        Marker m = new Marker((map));
        m.setPosition(gp);
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m.setIcon(getResources().getDrawable(R.drawable.icons8_trolley_100));
        m.setDraggable(true);
        m.setInfoWindow(new MapCustomInfoBubble(map));
        m.setInfoWindowAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_TOP);

        map.getOverlayManager().add(m);
        //OnClick
        m.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                if (m.isInfoWindowShown())
                    m.closeInfoWindow();
                else {

                    m.showInfoWindow();
                }
                return true;
            }
        });
        return m;

    }


    private void navifationDrawer() {
        //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        //by default the nav_profile is selected
        navigationView.setCheckedItem(R.id.demander_1);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.demander_2:
                Intent intent;
                intent = new Intent(this, d_page1.class);
                startActivity(intent);
                break;
        }
        return true;
    }


    //fin menu


    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setLocalisation() {
        //copier_coller
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                myLongitude = location.getLongitude();
                myLattitude = location.getLatitude();
                //textView.append("\n " + longitude + " " + lattitude);
                mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
                mLocationOverlay.enableMyLocation();
                map.setMultiTouchControls(true);
                map.getOverlays().add(mLocationOverlay);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET}, 10);
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 5, locationListener);
        Polyline line = new Polyline();
        line.setTitle("Un trajet");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setWidth(10f);
        line.setId("-1");
        line.setColor(Color.RED);
        trajet = new ArrayList<GeoPoint>();
        OverlayItem point = new OverlayItem("dechet_organique", "moyen", new GeoPoint(45.31765771762817, 5.922782763890293));
        trajet.add(new GeoPoint(point.getPoint().getLatitude(), point.getPoint().getLongitude()));
        line.setPoints(trajet);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, map));
        map.getOverlayManager().add(line);
        map.invalidate();
        //fin
    }

    class MapCustomInfoBubble extends InfoWindow {

        public MapCustomInfoBubble(MapView mapView) {
            super(R.layout.map_infobubble_black, mapView);//my custom layout and my mapView
        }

        @Override
        public void onClose() {
            //by default, do nothing
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onOpen(Object item) {
            Marker marker = (Marker) item; //the marker on which you click to open the bubble
            //marker infos
            TextView title = mView.findViewById(R.id.name);
            ImageView favorite = (ImageView) mView.findViewById(R.id.bubble_favorie);
            ImageView agenda = (ImageView) mView.findViewById(R.id.bubble_agenda);
            ImageView call = (ImageView) mView.findViewById(R.id.bubble_call);

            title.setText("show-shine");
            TextView desc = mView.findViewById(R.id.bubble_desc);
            desc.setText("Description : Un paque \n" +
                    "de carton ondulé peu \n" +
                    "épais, les trois quarts \n" +
                    "sont de couleur blanche, \n" +
                    "le reste est maron\n" +
                    "Weight : 38 kg\n");
            call.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    makeCall();
                }
            });
            agenda.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                   //add number to your contact
                }
            });
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Util.print(getApplicationContext(),"test");
                    boolean isfilled = Util.checkImageResource(v.getContext(),favorite,R.drawable.icons8_heart_24);
                    if(isfilled){
                        favorite.setImageResource(R.drawable.icons8_heart_48);
                    }else{
                        favorite.setImageResource(R.drawable.icons8_heart_24);
                    }

                }
            });
        }

    }


    public void makeCall()
    {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + "0693727277"));
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED){

            startActivity(intent);

        } else {

            requestPermission();
        }
    }
    private void requestPermission()
    {

        if (ActivityCompat.shouldShowRequestPermissionRationale(EspaceDemandeur.this,Manifest.permission.CALL_PHONE))
        {
        }
        else {

            ActivityCompat.requestPermissions(EspaceDemandeur.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                }
                break;
        }
    }

}
