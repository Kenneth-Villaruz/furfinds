package appdev.technologies.furfindspetshop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import appdev.technologies.furfindspetshop.eventhandlers.OnPermissionRequested;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.PermissionsManager;

public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity
{
    private boolean m_requireAuth = true;

    private static final int EXT_SD_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;

    private boolean requestResult_canUseCam = false;
    private boolean requestResult_canUseStorage = false;

    private OnPermissionRequested onPermissionRequested;

    /**
     * Instantiate objects here before views are created
     */
    @Override
    public abstract void onAwake();

    /**
     * Set the main layout then initialize views here
     */
    @Override
    public abstract void initializeViews();

    /**
     * Handle events here after views are initialized
     */
    @Override
    public abstract void bindEvents();

    /**
     * Called when the back button was pressed
     */
    protected abstract void onBackKeyPressed();

    @Override
    public void onBackPressed()
    {
        onBackKeyPressed();
    }

    @Override
    public void switchActivity(Intent intent)
    {
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Derived class initialization block for class objects
        onAwake();

        // Auth guard
        if (m_requireAuth)
            guardAuth();

        // Derived class initialization block for views
        initializeViews();

        // Derived class event handler binding block
        bindEvents();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        PermissionsManager.checkPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (requestCode == PermissionsManager.MULTIPLE_PERMISSIONS_REQUEST_CODE)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                switch (permissions[i])
                {
                    case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                        requestResult_canUseStorage = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                        break;

                    case Manifest.permission.CAMERA:
                        requestResult_canUseCam = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                        break;
                    // Handle other permissions as needed...
                }
            }

            if (onPermissionRequested != null)
                onPermissionRequested.onRequested();
        }
    }
    //
    // Check if user is logged on to access the activity
    //
    private void guardAuth()
    {
        if (!AuthSharedPrefsManager.getInstance(this).isLoggedIn())
        {
            // Check if current activity is not Login Activity before starting Login Activity
            if (!(this instanceof Login))
            {
                Intent intent = new Intent(this, Login.class);
                // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }
    //
    // Force login
    //
    public void requireAuth(boolean requireAuth)
    {
        this.m_requireAuth = requireAuth;
    }
    //
    // Check if we can access the camera
    //
    public boolean canUseCamera() {
        return requestResult_canUseCam;
    }
    //
    // Check if we can access the storage
    //
    public boolean canUseStorage() {
        return requestResult_canUseStorage;
    }
    //
    // Event handler that triggers after the permission
    // request dialog has been shown
    //
    public void setOnPermissionRequested(OnPermissionRequested listener)
    {
        onPermissionRequested = listener;
    }
}