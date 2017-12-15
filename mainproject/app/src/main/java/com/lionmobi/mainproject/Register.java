package com.lionmobi.mainproject;

/**
 * Created by rockie on 12/15/2017.
 */
public class Register {

    public static  <T> T getService(Class<T> clazz){
        try {
            return (T)clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
