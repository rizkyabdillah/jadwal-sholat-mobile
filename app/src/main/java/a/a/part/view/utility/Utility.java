package a.a.part.view.utility;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.intentfilter.androidpermissions.PermissionManager;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.nlopez.smartlocation.SmartLocation;

public class Utility {
    
    private static double latitude, longitude;
    private static final Calendar c = Calendar.getInstance();
    private static ProgressDialog dialog;

    public static final String[] PERMISSION = {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public static int getYear() {
        return c.get(Calendar.YEAR);
    }

    public static int getMonth() {
        return c.get(Calendar.MONTH);
    }

    public static int getDay() {
        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getGMT() {
        TimeZone timeZone = TimeZone.getDefault();
        return timeZone.getOffset(new Date().getTime()) / 3600000;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static void setLatitude(double latitude) {
        Utility.latitude = latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    // Check gps provider is enabled
    public static boolean isProviderEnable(Context context) {
        return SmartLocation.with(context).location().state().isAnyProviderAvailable();
    }

    public static void setLongitude(double longitude) {
        Utility.longitude = longitude;
    }

    public static void dialogDissmiss() {
        dialog.dismiss();
    }

    public static void showProgressDialog(Activity activity) {
        dialog = new ProgressDialog(activity);
        dialog.setMessage("Mengambil data!");
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void showDialogSetting(Activity activity, PermissionManager permissionManager) {
        // Showing alert dialog where permission is denied
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Diperlukan akses lokasi!");
        alert.setMessage("Aplikasi ini membutuhkan akses lokasi, Apakah anda setuju?");
        alert.setCancelable(false);
        alert.setPositiveButton("Pengaturan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Showing setting page where setting button is pressed
                openSettingPermission(activity, permissionManager);
                activity.finish();
            }
        });
        alert.setNegativeButton("Kembali", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Closing activity where back button is pressed
                dialog.dismiss();
                activity.finish();
            }
        });
        alert.show();
    }

    public static void showDialogEnableGps(Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Diperlukan akses lokasi!");
        alert.setMessage("GPS anda belum aktif, Aktifkan?");
        alert.setCancelable(false);
        alert.setPositiveButton("Aktifkan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Goto setting page for gps activated
                activity.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        alert.show();
    }

    private static void openSettingPermission(Activity activity, PermissionManager permissionManager) {
        // Open setting application page detail for allow permission
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, permissionManager.getResultCode());
    }
}
