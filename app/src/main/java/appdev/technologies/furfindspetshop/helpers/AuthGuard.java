package appdev.technologies.furfindspetshop.helpers;

import android.content.Context;
import android.content.Intent;

import appdev.technologies.furfindspetshop.Home;

public class AuthGuard
{
    public static void shouldSkipAuth(Context context)
    {
        if (AuthSharedPrefsManager.getInstance(context).isLoggedIn())
        {
            Intent intent = new Intent(context, Home.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
    }
}
