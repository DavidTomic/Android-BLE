package com.example.dtomic.ble_locator;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

/**
 * Created by dtomic on 02/02/2018.
 */

public class MapWorker {

    private static final String MAP_FILE = "croatia.map";

    private MapView mapView;
    private Activity activity;

    public MapWorker(MapView mapView, Activity activity) {
        this.mapView = mapView;
        this.activity = activity;
        initMap();
    }

    private void initMap() {
        this.mapView.setClickable(true);
        this.mapView.getMapScaleBar().setVisible(true);
        this.mapView.setBuiltInZoomControls(true);
        this.mapView.setZoomLevelMin((byte) 10);
        this.mapView.setZoomLevelMax((byte) 20);


        // create a tile cache of suitable size
        TileCache tileCache = AndroidUtil.createTileCache(activity, "mapcache",
                mapView.getModel().displayModel.getTileSize(), 1f,
                this.mapView.getModel().frameBufferModel.getOverdrawFactor());


        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard + "/Download",MAP_FILE);
        Log.i("Files", "file: " + file.exists());


        // tile renderer layer using internal render theme
        MapDataStore mapDataStore = new MapFile(file);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore,
                this.mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        // only once a layer is associated with a mapView the rendering starts
        this.mapView.getLayerManager().getLayers().add(tileRendererLayer);

        this.mapView.setCenter(new LatLong(45.8, 16.0));
        this.mapView.setZoomLevel((byte) 12);
    }

    public void addMarker(Double lat, Double lon) {
        LatLong ll = new LatLong(lat, lon);
        Bitmap icon = AndroidGraphicFactory.convertToBitmap(activity.getResources().getDrawable(R.mipmap.ic_launcher));
        Marker marker = new Marker(ll, icon, 0, 0);
        this.mapView.getLayerManager().getLayers().add(marker);
    }
}
