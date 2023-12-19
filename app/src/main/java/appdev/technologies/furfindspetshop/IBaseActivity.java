package appdev.technologies.furfindspetshop;

import android.content.Intent;

public interface IBaseActivity
{
    String INTENT_EXTRAS_CALLER_ACTIVITY = "callingActivity";
    String INTENT_EXTRAS_SUCCESS_MESSAGE = "successMessage";
    //
    // Method signatures
    //
    void onAwake();
    void initializeViews();
    void bindEvents();
    void switchActivity(Intent intent);
}
