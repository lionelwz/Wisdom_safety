package me.nereo.multi_image_selector.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by Administrator on 2017/11/6.
 */

public class FileManager {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String VIDEO_SUFFIX = ".mp4";
    public static final String VIDEO_PREFIX = "blog_video_";
    public static final String UETEC_DIR = "uetec";
    public static final String VIDEO_DIR = "video";
    private static String mDstDir;
    private static String mVideoDir;
    private static Context sContext;

    public FileManager(Context context) {

    }


    public static void initDir(Context context) {
        sContext = context;
        File dir = null;
        if(TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStorageDirectory();
        }else{
            dir = getCacheDirectory(context, true);
        }
        String sdcardPath = dir.getAbsolutePath();
        mDstDir = sdcardPath + File.separator + UETEC_DIR;
        File dstDirFile = new File(mDstDir);
        if(!dstDirFile.exists()) {
            dstDirFile.mkdir();
        }
        File videoFile = new File(mDstDir, VIDEO_DIR);
        if(!videoFile.exists()) {
            videoFile.mkdir();
        }
        mVideoDir = videoFile.getAbsolutePath();
    }

    public static String getDstDir() {
        return mDstDir;
    }

    public static String getVideoDir() {
        if(null == mVideoDir) {
            File videoFile = new File(mDstDir, VIDEO_DIR);
            if(!videoFile.exists()) {
                videoFile.mkdir();
            }
            mVideoDir = videoFile.getAbsolutePath();
        }
        return mVideoDir;
    }

    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
