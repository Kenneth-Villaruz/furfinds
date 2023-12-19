package appdev.technologies.furfindspetshop.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class AuthSharedPrefsManager
{
    private static final String SHARED_PREF_NAME = "auth_shared_pref";

    private static AuthSharedPrefsManager mInstance;
    private Context mCtx;

    private AuthSharedPrefsManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public static synchronized AuthSharedPrefsManager getInstance(Context mCtx) {
        if (mInstance == null) {
            mInstance = new AuthSharedPrefsManager(mCtx);
        }
        return mInstance;
    }

    public void saveUser(UserAuth user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("id", user.getId());
        editor.putString("username", user.getUsername());
        editor.putString("email", user.getEmail());
        editor.putString("password", user.getPassword());

        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("id", -1) != -1;
    }

    public UserAuth getUser()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new UserAuth(
                sharedPreferences.getInt("id", -1),
                sharedPreferences.getString("username", null),
                sharedPreferences.getString("email", null),
                sharedPreferences.getString("password", null)
        );
    }

    public void clear()
    {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}