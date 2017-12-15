package com.lionmobi.mainproject;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * DEX合并
 * Created by rockie on 12/15/2017.
 */
public class MultiDexMerger {

    /**
     * 多DEX文件合并
     * @param loader
     * @param ctx
     */
    public static void merge(DexClassLoader loader, Context ctx) {
        PathClassLoader pathLoader = (PathClassLoader) ctx.getClassLoader();
        try {
            Object dexElements = combineArray(
                    getDexElements(getPathList(loader)),
                    getDexElements(getPathList(pathLoader)));
            Object pathList = getPathList(pathLoader);
            setField(pathList, pathList.getClass(), "dexElements", dexElements);
        } catch (Exception e) {
            Log.i("multidex", "inject dexclassloader error:" + Log.getStackTraceString(e));
        }
    }

    private static Object getPathList(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
    }

    private static Object getField(Object obj, Class<?> cl, String field)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        return localField.get(obj);
    }

    private static Object getDexElements(Object paramObject)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        return getField(paramObject, paramObject.getClass(), "dexElements");
    }

    private static void setField(Object obj, Class<?> cl, String field,
                                 Object value) throws NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Field localField = cl.getDeclaredField(field);
        localField.setAccessible(true);
        localField.set(obj, value);
    }

    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayRhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }
}
