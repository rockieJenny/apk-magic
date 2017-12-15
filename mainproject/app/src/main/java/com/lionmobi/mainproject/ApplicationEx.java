package com.lionmobi.mainproject;

/**
 * Created by rockie on 12/15/2017.
 */
public class ApplicationEx extends ProxyApplication {
    public static ApplicationEx instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public <T> T getService(Class<T> clazz) {
        try {
            Class<?> aClass = Class.forName("com.lionmobi.service.AdService");
            return (T) aClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
