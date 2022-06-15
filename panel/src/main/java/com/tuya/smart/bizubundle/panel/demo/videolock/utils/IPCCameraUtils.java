package com.tuya.smart.bizubundle.panel.demo.videolock.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.tuya.smart.camera.utils.AppUtils;
import com.tuya.smart.camera.utils.L;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Create by blitzfeng on 5/24/22
 */
public class IPCCameraUtils {
    private static final String TAG = "IPCCameraUtils";
    private static final String ROOT_PATH;
    private static final String INTERNAL_FILE_DIR;
    private static final String INTERNAL_CACHE_DIR;
    public static final String SNAPSHOT_PATH;
    public static final String DCIM_CAMERA_PATH;
    public static final String COVER_SNAPSHOT_PATH;
    public static final String RECORD_PATH;
    public static final String RECORD_PATH_Q;
    public static final String DOWNLOAD_PATH;
    public static final String LOCAL_DEVICE_THUMBNAIL_PATH;
    public static final String LOCAL_DEVICE_ORIGIN_PATH;
    public static final String LOCAL_DEVICE_COVER_PATH;
    @RequiresApi(
            api = 29
    )
    public static String RELATIVE_PHOTO_ROOT_PATH = null;
    @RequiresApi(
            api = 29
    )
    public static String RELATIVE_VIDEO_ROOT_PATH = null;
    public static final String CACHE_PATH;
    public static final String SEPARATE_LINE = "/";
    private static final int CONVERSION_RATIO = 1024;

    private IPCCameraUtils() {
    }

    public static String getDCIMCameraPath(String devId) {
        return recordSnapshotPath(DCIM_CAMERA_PATH, devId);
    }

    public static String recordSnapshotPath(String devId) {
        return recordSnapshotPath(SNAPSHOT_PATH, devId);
    }

    public static String silenceSnapshotPath(String devId) {
        return recordSnapshotPath(COVER_SNAPSHOT_PATH, devId);
    }

    private static String recordSnapshotPath(String dicPath, String devId) {
        String videoPath = dicPath + devId + "/";
        File file = new File(videoPath);
        if (!file.exists() && !file.mkdirs()) {
            L.e("IPCCameraUtils", "recordSnapshotPath create the directory fail, videoPath is " + videoPath);
        }

        return videoPath;
    }

    public static String recordPath(String devId) {
        String videoPath = RECORD_PATH + devId + "/";
        File file = new File(videoPath);
        if (!file.exists() && !file.mkdirs()) {
            L.e("IPCCameraUtils", "recordPath create the directory fail, videoPath is " + videoPath);
        }

        return videoPath;
    }

    public static String recordPathSupportQ(String devId) {
        if (Build.VERSION.SDK_INT >= 30) {
            String videoPath = RECORD_PATH_Q + devId + "/";
            File file = new File(videoPath);
            if (!file.exists() && !file.mkdirs()) {
                L.e("IPCCameraUtils", "recordPathQ create the directory fail, videoPath is " + videoPath);
            }

            return videoPath;
        } else {
            return recordPath(devId);
        }
    }

    public static String translateSpaceGB(String space) {
        float spaceWithGB = (float)Long.parseLong(space) * 1.0F / 1.07374182E9F;
        float spaceShow = (float)Math.round(spaceWithGB * 100.0F) * 1.0F / 100.0F;
        String tempStr = String.format(Locale.US, "%.2f", spaceShow);
        return tempStr + "GB";
    }

    /** @deprecated */
    @Deprecated
    public static Map<String, Object> toMap(String jsonString) throws JSONException {
        JSONObject object = new JSONObject(jsonString);
        Map<String, Object> retMap = new HashMap();
        if (object != JSONObject.NULL) {
            retMap = toMap(object);
        }

        return (Map)retMap;
    }

    /** @deprecated */
    @Deprecated
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap();

        String key;
        Object value;
        for(Iterator keysItr = object.keys(); keysItr.hasNext(); map.put(key, value)) {
            key = (String)keysItr.next();
            value = object.get(key);
            if (value instanceof JSONArray) {
                value = toList((JSONArray)value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject)value);
            }
        }

        return map;
    }

    /** @deprecated */
    @Deprecated
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList();

        for(int i = 0; i < array.length(); ++i) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray)value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject)value);
            }

            list.add(value);
        }

        return list;
    }

    public static String drivingRecorderOriginPath(String devId) {
        String videoPath = LOCAL_DEVICE_ORIGIN_PATH + devId + "/";
        File file = new File(videoPath);
        if (!file.exists() && !file.mkdirs()) {
            L.e("IPCCameraUtils", "recordPath create the directory fail, videoPath is " + videoPath);
        }

        return videoPath;
    }

    public static String drivingRecorderThumbnailPath(String devId) {
        String videoPath = LOCAL_DEVICE_THUMBNAIL_PATH + devId + "/";
        File file = new File(videoPath);
        if (!file.exists() && !file.mkdirs()) {
            L.e("IPCCameraUtils", "recordPath create the directory fail, videoPath is " + videoPath);
        }

        return videoPath;
    }

    public static String drivingRecorderVideoCoverPath(String devId) {
        String videoPath = LOCAL_DEVICE_COVER_PATH + devId + "/";
        File file = new File(videoPath);
        if (!file.exists() && !file.mkdirs()) {
            L.e("IPCCameraUtils", "recordPath create the directory fail, videoPath is " + videoPath);
        }

        return videoPath;
    }

    public static String getCachePath(@NonNull Context context, String devId) {
        File file = new File(getCacheRootPath(context) + devId);
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            L.d("IPCCameraUtils", mkdirs + " getCacheRootPath create dic:" + file.getAbsolutePath());
        }

        return file.getAbsolutePath();
    }

    public static String getCacheRootPath(@NonNull Context context) {
        File cacheDir = context.getExternalCacheDir();
        return cacheDir != null ? cacheDir.getPath() + "/Camera/" : "";
    }

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RELATIVE_PHOTO_ROOT_PATH = Environment.DIRECTORY_DCIM + "/Camera/";
            RELATIVE_VIDEO_ROOT_PATH = Environment.DIRECTORY_DCIM + "/Camera/Thumbnail/";
        }
        
        if (Build.VERSION.SDK_INT >= 30) {
            ROOT_PATH = AppUtils.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        } else {
            ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        INTERNAL_FILE_DIR = AppUtils.getContext().getFilesDir().getAbsolutePath();
        INTERNAL_CACHE_DIR = AppUtils.getContext().getCacheDir().getAbsolutePath();
        SNAPSHOT_PATH = ROOT_PATH + "/Camera/";
        DCIM_CAMERA_PATH = ROOT_PATH + "/" + Environment.DIRECTORY_DCIM + "/Camera/";
        COVER_SNAPSHOT_PATH = INTERNAL_CACHE_DIR + "/Camera/Cover/";
        RECORD_PATH = ROOT_PATH + "/Camera/Thumbnail/";
        RECORD_PATH_Q = ROOT_PATH + "/Camera/" + Environment.DIRECTORY_DCIM + "/Thumbnail/";
        DOWNLOAD_PATH = ROOT_PATH + "/Camera/Download/";
        LOCAL_DEVICE_THUMBNAIL_PATH = ROOT_PATH + "/Camera/Cache/DriverRecorder/thumbnail/";
        LOCAL_DEVICE_ORIGIN_PATH = ROOT_PATH + "/Camera/Cache/DriverRecorder/origin/";
        LOCAL_DEVICE_COVER_PATH = ROOT_PATH + "/Camera/Cache/DriverRecorder/cover/";
        CACHE_PATH = ROOT_PATH + "/Camera/Cache/";
    }
}
