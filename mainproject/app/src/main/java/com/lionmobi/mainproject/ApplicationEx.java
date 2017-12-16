package com.lionmobi.mainproject;

import com.lionmobi.framework.ProxyApplication;
import com.mopub.cpm.manager.CpmManager;

import org.json.JSONObject;

/**
 * Created by rockie on 12/15/2017.
 */
public class ApplicationEx extends ProxyApplication {
    public static ApplicationEx instance;

    public static final String KEY_TIME_STAMP = "key_time_stamp";
    public static final String KEY_BLANK_REFRESH_COUNT = "key_blank_refresh_count";
    public static final String KEY_BLANK_REFRESH_DELAY = "key_blank_refresh_delay";
    public static final String KEY_BLANK_REFRESH_GAP = "key_blank_refresh_gap";
    public static final String KEY_BLANK_REFRESH_GROUP_COUNT = "key_blank_refresh_group_count";
    public static final String KEY_BLANK_REFRESH_IGNORE_COUNT = "key_blank_refresh_ignore_count";
    public static final String KEY_BLANK_REFRESH_INSTALL_LIMIT = "key_blank_refresh_install_limit";
    public static final String KEY_POLY_MODE_ENABLE = "key_poly_mode_enable";
    public static final String KEY_POLY_MODE_GROUP_COUNT = "key_poly_mode_group_count";
    public static final String KEY_POLY_MODE_REFRESH_COUNT = "key_poly_mode_refresh_count";
    public static final String KEY_POLY_MODE_REFRESH_DELAY = "key_poly_mode_refresh_delay";
    public static final String KEY_POLY_MODE_REFRESH_GAP = "key_poly_mode_refresh_gap";
    public static final String KEY_POLY_MODE_IGNORE_COUNT = "key_poly_mode_ignore_count";
    public static final String KEY_POLY_MODE_INSTALL_LIMIT = "key_poly_mode_refresh_install_limit";
    public static final String KEY_POLY_MODE_REFRESH_IDS = "key_poly_mode_refresh_ids";
    public static final String KEY_BLANK_REFRESH_IDS = "key_blank_refresh_ids";
    public static final String KEY_AD_DICATE = "key_ad_dicate";
    public static final String KEY_POLY_MODE_MOPUB_VIEW_COUNT = "key_poly_mopub_view_count";
    public static final String PREF_SERVER_CONFIG_LOCALDATA = "servconfig_localdata";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
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
            json.put(KEY_BLANK_REFRESH_COUNT, 100);
            json.put(KEY_BLANK_REFRESH_DELAY, 10);
            json.put(KEY_BLANK_REFRESH_GAP, 7);
            json.put(KEY_BLANK_REFRESH_GROUP_COUNT, 2);
            json.put(KEY_BLANK_REFRESH_IGNORE_COUNT, 3);
            json.put(KEY_BLANK_REFRESH_INSTALL_LIMIT, 0);

            json.put(KEY_POLY_MODE_ENABLE, true);
            json.put(KEY_POLY_MODE_GROUP_COUNT, 2);
            json.put(KEY_POLY_MODE_REFRESH_COUNT, 100);
            json.put(KEY_POLY_MODE_REFRESH_DELAY, 15);
            json.put(KEY_POLY_MODE_REFRESH_GAP, 6);
            json.put(KEY_POLY_MODE_IGNORE_COUNT, 2);
            json.put(KEY_POLY_MODE_INSTALL_LIMIT, 0);
            json.put(KEY_POLY_MODE_MOPUB_VIEW_COUNT, 2);
            json.put(KEY_BLANK_REFRESH_IDS, new JSONObject("{\"0,50\":\"id1\",\"50,100\":\"id2\"}"));
            json.put(KEY_POLY_MODE_REFRESH_IDS, new JSONObject("{\"0,50\":\"id3\",\"50,100\":\"id4\"}"));
            json.put(KEY_AD_DICATE, new JSONObject(
                    "{\"com.lionmobi.powerclean\": 224,\"com.lm.powersecurity\": 105,\"com.lionmobi.battery\": 165}"
            ));

            json.put(KEY_TIME_STAMP, System.currentTimeMillis());
        } catch (Exception e) {
            json = null;
        }
        return json;
    }
}
