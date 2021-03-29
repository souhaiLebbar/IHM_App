package com.polytech.ihm;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class EspaceDemandeur extends AppCompatActivity {
    private MapView map;

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
        //TileSourceFactory.PUBLIC_TRANSPORT
        //http://leaflet-extras.github.io/leaflet-providers/preview/
        map.setTileSource(TileSourceFactory.MAPNIK); //render
        map.setBuiltInZoomControls(true);            //zoomable
        GeoPoint startPoint = new GeoPoint(43.61572296415799, 7.071842570348114);
        IMapController mapController = map.getController();
        mapController.setZoom(18.0);
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> items = new ArrayList<>();
        OverlayItem home = new OverlayItem("Souhail's Office","my office",new GeoPoint(43.62500377041074, 7.0505728838423805));
        Drawable m =home.getMarker(0);
        items.add(home);
        items.add(new OverlayItem("Casino","Casino",new GeoPoint(43.61794442542704, 7.075222153668426)));

        ItemizedOverlayWithFocus<OverlayItem> mOverlay =
                new ItemizedOverlayWithFocus<OverlayItem>(getApplicationContext(), items,
                        new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                            @Override
                            public boolean onItemSingleTapUp(int index, OverlayItem item) {
                                return true;
                            }

                            @Override
                            public boolean onItemLongPress(int index, OverlayItem item) {
                                return false;
                            }
                        });
        mOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlay);
    }


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
}