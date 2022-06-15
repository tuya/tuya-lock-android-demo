package com.tuya.smart.bizubundle.panel.demo.videolock.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.tuya.smart.android.common.utils.L;

import java.util.List;

/**
 * Create by blitzfeng on 5/23/22
 */
public class CheckPermissionUtils {
    private static final String TAG = "CheckPermissionUtils";


    public static boolean checkSinglePermission(String permission, Context context) {
        boolean result = true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return result;
        }
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (targetSdkVersion >= Build.VERSION_CODES.M) {
            // targetSdkVersion >= Android M, we can
            // use Context#checkSelfPermission
            result = ContextCompat.checkSelfPermission(context, permission)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            // targetSdkVersion < Android M, we have to use PermissionChecker
            result = PermissionChecker.checkSelfPermission(context, permission)
                    == PermissionChecker.PERMISSION_GRANTED;
        }

        return result;
    }

    private static boolean selfPermissionGranted(Context context, String permission) {
        // For Android < Android M, self permissions are always granted.
        int targetSdkVersion = 0;
        try {
            final PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        boolean result = true;

        L.d(TAG, "selfPermissionGranted targetSdkVersion " + targetSdkVersion);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = context.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                L.d(TAG, "targetSdkVersion >= Android M, we can Context#checkSelfPermission " + result);
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
                L.d(TAG, "targetSdkVersion < Android M, we have to use PermissionChecker " + result);
            }
        }

        return result;
    }

    public static void requestPermission(Activity context, String permission, int requestCode, String tip) {
        //判断当前Activity是否已经获得了该权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (!selfPermissionGranted(context, permission)) {
            requestPermission(context, requestCode, permission);
        }
    }

    public static void requestPermission(Activity context, String[] permission, int requestCode) {
        //判断当前Activity是否已经获得了该权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        boolean isGranted = true;
        for (String perm : permission)
            if (!selfPermissionGranted(context, perm)) {
                isGranted = false;
                break;
            }

        if (!isGranted) {
            requestPermissions(context, requestCode, permission);
        }
    }

    public static void requestPermission(Activity context, String permission, int requestCode) {
        requestPermission(context, permission, requestCode, "");
    }

    private static void requestPermission(Activity activity, int requestCode, String permission) {
        if (activity == null) return;
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
    }

    private static void requestPermissions(Activity activity, int requestCode, String[] permissions) {
        if (activity == null) return;
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    private static void setResult2Activity(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
