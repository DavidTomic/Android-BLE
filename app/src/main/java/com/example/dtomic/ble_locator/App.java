package com.example.dtomic.ble_locator;

import android.app.Application;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

/**
 * Created by dtomic on 02/02/2018.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidGraphicFactory.createInstance(this);
    }
}
