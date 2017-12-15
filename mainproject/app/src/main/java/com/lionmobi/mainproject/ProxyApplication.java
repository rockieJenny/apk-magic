package com.lionmobi.mainproject;

import android.app.Application;
import android.content.Context;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;

/**
 * Created by rockie on 12/15/2017.
 */
public abstract class ProxyApplication extends Application {
    private String apkFileName;
    private String odexPath;
    private String libPath;
    protected DexClassLoader mDexLoader;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public void init(){
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
                //在payload_odex文件夹内，创建payload.apk
                // 读取程序classes.dex文件
                byte[] dexdata = this.readDexFileFromApk();

                // 分离出解壳后的apk文件已用于动态加载
                this.splitPayLoadFromDex(dexdata);
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
            MultiDexMerger.merge(mDexLoader,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**\
     * 释放被加壳的apk文件，so文件
     *
     * @param
     * @throws IOException
     */
    private void splitPayLoadFromDex(byte[] apkdata) throws IOException {
        int ablen = apkdata.length;
        //取被加壳apk的长度   这里的长度取值，对应加壳时长度的赋值都可以做些简化
        byte[] dexlen = new byte[4];
        System.arraycopy(apkdata, ablen - 4, dexlen, 0, 4);
        ByteArrayInputStream bais = new ByteArrayInputStream(dexlen);
        DataInputStream in = new DataInputStream(bais);
        int readInt = in.readInt();
        System.out.println(Integer.toHexString(readInt));
        byte[] newdex = new byte[readInt];
        //把被加壳apk内容拷贝到newdex中
        System.arraycopy(apkdata, ablen - 4 - readInt, newdex, 0, readInt);
        //这里应该加上对于apk的解密操作，若加壳是加密处理的话

        //对源程序Apk进行解密
        newdex = decrypt(newdex);

        //写入apk文件
        File file = new File(apkFileName);
        try {
            FileOutputStream localFileOutputStream = new FileOutputStream(file);
            localFileOutputStream.write(newdex);
            localFileOutputStream.close();
        } catch (IOException localIOException) {
            throw new RuntimeException(localIOException);
        }

        //分析被加壳的apk文件
        ZipInputStream localZipInputStream = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(file)));
        while (true) {
            ZipEntry localZipEntry = localZipInputStream.getNextEntry();//不了解这个是否也遍历子目录，看样子应该是遍历的
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
     * 从apk包里面获取dex文件内容（byte）
     *
     * @return
     * @throws IOException
     */
    private byte[] readDexFileFromApk() throws IOException {
        ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
        ZipInputStream localZipInputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(this.getApplicationInfo().sourceDir)));
        while (true) {
            ZipEntry localZipEntry = localZipInputStream.getNextEntry();
            if (localZipEntry == null) {
                localZipInputStream.close();
                break;
            }
            if (localZipEntry.getName().equals("classes.dex")) {
                byte[] arrayOfByte = new byte[1024];
                while (true) {
                    int i = localZipInputStream.read(arrayOfByte);
                    if (i == -1)
                        break;
                    dexByteArrayOutputStream.write(arrayOfByte, 0, i);
                }
            }
            localZipInputStream.closeEntry();
        }
        localZipInputStream.close();
        return dexByteArrayOutputStream.toByteArray();
    }


    // //直接返回数据，读者可以添加自己解密方法
    private byte[] decrypt(byte[] srcdata) {
        for (int i = 0; i < srcdata.length; i++) {
            srcdata[i] = (byte) (0xFF ^ srcdata[i]);
        }
        return srcdata;
    }

}
