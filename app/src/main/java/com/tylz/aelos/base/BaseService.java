package com.tylz.aelos.base;

import android.app.Service;


public abstract class BaseService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        ((BaseApplication) getApplication()).addService(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ((BaseApplication) getApplication()).removeService(this);
    }
}
