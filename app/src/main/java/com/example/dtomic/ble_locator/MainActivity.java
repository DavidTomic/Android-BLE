package com.example.dtomic.ble_locator;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.mapView) MapView mapView;
    private MapWorker mapWorker;
    private ScanInteractor scanInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        scanInteractor = new ScanInteractor(this);
        mapWorker = new MapWorker(mapView, this);
        mapWorker.addMarker(45.8, 16.0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        scanInteractor.prepareForScan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanInteractor.stopLeScan();
    }

    @Override
    protected void onDestroy() {
        this.mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ScanInteractor.REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "You must turn Bluetooth on, to use this app", Toast.LENGTH_LONG).show();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == ScanInteractor.REQUEST_PERMISSION_LOCATION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission accepted");
        } else {
            Toast.makeText(this, "You must grant the location permission.", Toast.LENGTH_SHORT).show();
        }
    }

}
