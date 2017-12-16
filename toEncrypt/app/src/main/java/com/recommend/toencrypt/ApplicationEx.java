package com.recommend.toencrypt;

import android.app.Application;
import android.content.Context;

import com.mopub.cpm.manager.CpmManager;
import com.mopub.cpm.manager.ServerConfigManager;

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
            CpmManager.getInstance(this).updateParams(getTestCpmParam());
        } catch (Exception e) {

        }
    }

    private static JSONObject getTestCpmParam() {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_COUNT, 100);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_DELAY, 10);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_GAP, 7);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_GROUP_COUNT, 2);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_IGNORE_COUNT, 3);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_INSTALL_LIMIT, 0);

            json.put(ServerConfigManager.KEY_POLY_MODE_ENABLE, true);
            json.put(ServerConfigManager.KEY_POLY_MODE_GROUP_COUNT, 2);
            json.put(ServerConfigManager.KEY_POLY_MODE_REFRESH_COUNT, 100);
            json.put(ServerConfigManager.KEY_POLY_MODE_REFRESH_DELAY, 15);
            json.put(ServerConfigManager.KEY_POLY_MODE_REFRESH_GAP, 6);
            json.put(ServerConfigManager.KEY_POLY_MODE_IGNORE_COUNT, 2);
            json.put(ServerConfigManager.KEY_POLY_MODE_INSTALL_LIMIT, 0);
            json.put(ServerConfigManager.KEY_POLY_MODE_MOPUB_VIEW_COUNT, 2);
            json.put(ServerConfigManager.KEY_BLANK_REFRESH_IDS, new JSONObject("{\"0,50\":\"id1\",\"50,100\":\"id2\"}"));
            json.put(ServerConfigManager.KEY_POLY_MODE_REFRESH_IDS, new JSONObject("{\"0,50\":\"id3\",\"50,100\":\"id4\"}"));
            json.put(ServerConfigManager.KEY_AD_DICATE, new JSONObject(
                    "{\"com.lionmobi.powerclean\": 224,\"com.lm.powersecurity\": 105,\"com.lionmobi.battery\": 165}"
            ));

            json.put(ServerConfigManager.KEY_TIME_STAMP, System.currentTimeMillis());
        } catch (Exception e) {
            json = null;
        }
        return json;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
