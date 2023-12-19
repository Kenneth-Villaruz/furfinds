package appdev.technologies.furfindspetshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTheme(R.style.SplashScreenTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() ->
        {
            Intent intent = new Intent(this, GetStarted.class);
            startActivity(intent);
            finish();

        }, 2000);
    }
}