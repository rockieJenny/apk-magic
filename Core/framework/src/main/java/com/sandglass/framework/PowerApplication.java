package com.sandglass.framework;

import android.app.Application;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dalvik.system.DexClassLoader;

/**
 * Created by rockie on 12/15/2017.
 */
public abstract class PowerApplication extends Application {
    private String apkFileName;
    private String odexPath;
    private String libPath;
    protected DexClassLoader mDexLoader;
    public static String ENCRYPTION_KEY = "lionmobikey$)!1";
    public static String ENCRYPTION_IV = "4e5Wa71fYoT7MFEX";


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        init();
    }

    public void init() {
        try {
            //创建两个文件夹payload_odex，payload_lib 私有的，可写的文件目录
            File odex = this.getDir("payload_odex", MODE_PRIVATE);
            File libs = this.getDir("payload_lib", MODE_PRIVATE);
            odexPath = odex.getAbsolutePath();
            libPath = libs.getAbsolutePath();
            apkFileName = odex.getAbsolutePath() + "/payload.apk";
            File dexFile = new File(apkFileName);
            Log.i("demo", "apk size:" + dexFile.length());
            if (!dexFile.exists()) {
                dexFile.createNewFile();
                // 分离出解壳后的apk文件已用于动态加载
                this.splitPayLoadFromDex();
            }
            // 配置动态加载环境
            Object currentActivityThread = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{});//获取主线程对象
            //当前apk的包名
            String packageName = this.getPackageName();
            ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldOjbect("android.app.ActivityThread", currentActivityThread, "mPackages");
            WeakReference wr = (WeakReference) mPackages.get(packageName);
            //创建被加壳apk的DexClassLoader对象  加载apk内的类和本地代码（c/c++代码）
            ClassLoader mClassLoader = (ClassLoader) RefInvoke.getFieldOjbect("android.app.LoadedApk", wr.get(), "mClassLoader");
            mDexLoader = new DexClassLoader(apkFileName, odexPath, libPath, mClassLoader);
            MultiDexMerger.merge(mDexLoader, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * \
     * 释放被加壳的apk文件，so文件
     *
     * @param
     * @throws IOException
     */
    private void splitPayLoadFromDex() throws IOException {
        //写入apk文件
        byte[] data = readDataFromAssets();
        data = decrypt(new String(data)).getBytes();
        File file = new File(apkFileName);
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(file);
            localFileOutputStream.write(data);
            localFileOutputStream.close();
        } catch (IOException localIOException) {
            throw new RuntimeException(localIOException);
        }

        //分析被加壳的apk文件
        ZipInputStream localZipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        while (true) {
            ZipEntry localZipEntry = localZipInputStream.getNextEntry();
            if (localZipEntry == null) {
                localZipInputStream.close();
                break;
            }
            //取出被加壳apk用到的so文件，放到 libPath中（data/data/包名/payload_lib)
            String name = localZipEntry.getName();
            if (name.startsWith("lib/") && name.endsWith(".so")) {
                File storeFile = new File(libPath + "/" + name.substring(name.lastIndexOf('/')));
                storeFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(storeFile);
                byte[] arrayOfByte = new byte[1024];
                while (true) {
                    int i = localZipInputStream.read(arrayOfByte);
                    if (i == -1)
                        break;
                    fos.write(arrayOfByte, 0, i);
                }
                fos.flush();
                fos.close();
            }
            localZipInputStream.closeEntry();
        }
        localZipInputStream.close();
    }

    /**
     * @return
     * @throws IOException
     */
    private byte[] readDataFromAssets() throws IOException {
        ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            is = getResources().getAssets().open("sandglass.dat");
            int ch = -1;
            while ((ch = is.read()) != -1) {
                dexByteArrayOutputStream.write(ch);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return dexByteArrayOutputStream.toByteArray();
    }

    public static String decrypt(String src) {
        try {
            byte[] decryptedStr = Base64.decode(src);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
            byte[] originalByte = cipher.doFinal(decryptedStr);
            String originalStr = new String(originalByte);
            return originalStr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Key makeKey() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] key = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));
            return new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static AlgorithmParameterSpec makeIv() {
        try {
            return new IvParameterSpec(ENCRYPTION_IV.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
