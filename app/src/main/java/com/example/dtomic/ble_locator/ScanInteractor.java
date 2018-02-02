package com.example.dtomic.ble_locator;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static com.example.dtomic.ble_locator.GattProfile.SERVICE_UUID;

/**
 * Created by dtomic on 02/02/2018.
 */

public class ScanInteractor {

    public static final String TAG = "ScanInteractor";

    private boolean mScanning;
    private static final long SCAN_TIMEOUT_MS = 10_000;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int REQUEST_PERMISSION_LOCATION = 1;

    private final BluetoothLeScannerCompat mScanner = BluetoothLeScannerCompat.getScanner();
    private final Handler mStopScanHandler = new Handler();

    private MainActivity activity;

    public ScanInteractor(MainActivity activity) {
        this.activity = activity;
    }

    void prepareForScan() {
        if (isBleSupported()) {
            // Ensures Bluetooth is enabled on the device
            BluetoothManager btManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter btAdapter = btManager.getAdapter();
            if (btAdapter.isEnabled()) {
                // Prompt for runtime permission
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startLeScan();
                } else {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
                }
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(activity, "BLE is not supported", Toast.LENGTH_LONG).show();
            //finish();
        }
    }

    boolean isBleSupported() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private void startLeScan() {
        mScanning = true;

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid(SERVICE_UUID)).build());
        mScanner.startScan(filters, settings, mScanCallback);

        // Stops scanning after a pre-defined scan period.
        mStopScanHandler.postDelayed(mStopScanRunnable, SCAN_TIMEOUT_MS);
    }

    public void stopLeScan() {
        if (mScanning) {
            mScanning = false;

            mScanner.stopScan(mScanCallback);
            mStopScanHandler.removeCallbacks(mStopScanRunnable);
        }
    }

    private final Runnable mStopScanRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(activity, "No devices found", Toast.LENGTH_SHORT).show();
            stopLeScan();
        }
    };

    private final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            // We scan with report delay > 0. This will never be called.
            Log.i(TAG, "onScanResult: " + result.getDevice().getAddress());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.i(TAG, "onBatchScanResults: " + results.toString());

            if (!results.isEmpty()) {
                stopLeScan();
                ScanResult result = results.get(0);
                activity.connect(result.getDevice().getAddress());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "Scan failed: " + errorCode);
            stopLeScan();
        }
    };
}
