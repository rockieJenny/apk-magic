package com.recommend.toencrypt;

import android.app.Application;
import android.content.Context;

import com.mopub.cpm.manager.CpmManager;

import org.json.JSONObject;

/**
 * Created by luowp on 2017/12/16.
 */

public class ApplicationEx extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CpmManager.getInstance(this).init();
        try {
            JSONObject config = new JSONObject();
            config.put("key_poly_mode_refresh_delay", 0L);
            config.put("key_blank_refresh_ids", "{\"0,50\": \"c4e764ace9774d04a43684e52415b71f\", \"50,100\":\"ba6c334096ff45e6aa7c815231e1b0b4\"}");
            CpmManager.getInstance(this).updateParams(config);
        } catch (Exception e) {

        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
