package com.example.dtomic.ble_locator;

import android.widget.Toast;

/**
 * Created by dtomic on 02/02/2018.
 */

public class ConnectInteractor {

    private final GattClient mGattClient = new GattClient();
    private MainActivity activity;

    public ConnectInteractor(MainActivity activity) {
        this.activity = activity;
    }

    public void connect(String deviceAddress) {
        mGattClient.onCreate(activity, deviceAddress, new GattClient.OnCounterReadListener() {
            @Override
            public void onCounterRead(final int value) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "onCounterRead " + Integer.toString(value), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onConnected(final boolean success) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "onConnected "+success, Toast.LENGTH_LONG).show();
                        if (!success) {
                            Toast.makeText(activity, "Connection error", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    public GattClient getGattClient() {
        return mGattClient;
    }
}
