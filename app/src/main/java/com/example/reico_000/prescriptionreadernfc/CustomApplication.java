package com.example.reico_000.prescriptionreadernfc;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Yosua Susanto on 22/10/2015.
 */
public class CustomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "AH2TAYdx19co5xauhJokZZ4MCMTs6ci6N8n98s5v", "ejIutRABlAKxTG1x5d8ySB0bwgk92aLi0qWKemgK");
    }
}