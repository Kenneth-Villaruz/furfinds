package appdev.technologies.furfindspetshop.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class PermissionsManager
{
    public static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 120;

    public static boolean canUseCamera(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canUseStorage(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkPermissions(Activity activity)
    {
        ArrayList<String> permissionsNeeded = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA);
        }

        // Add other permissions as needed...

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(new String[0]),
                    MULTIPLE_PERMISSIONS_REQUEST_CODE);
        }
    }
}
