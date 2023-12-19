package appdev.technologies.furfindspetshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;

import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;

public abstract class BaseActivityBottomNav extends AppCompatActivity implements IBaseActivity
{
    protected BottomNavigationView bottomNavigationView;

    private HashMap<Integer, Class<?>> activityMap;
    public boolean fitSystemWindows = true;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Auth guard
        requireAuth();

        // Derived class initialization block for class objects
        onAwake();

        // Derived class initialization block for views
        initializeViews();

        // Derived class event handler binding block
        bindEvents();

        // Transparent status bar, thanks to Philipp Lackner, #YoutubeShorts
        // WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        if (fitSystemWindows)
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    /**
     * Initializes the bottom nav menu and applies custom styling
     * @param bottomNavMenuId The view id
     */
    protected void setupBottomNavMenu(int bottomNavMenuId)
    {
        this.bottomNavigationView = findViewById(bottomNavMenuId);
        this.bottomNavigationView.setItemIconTintList(null);

        // In this code, Class<?> is a type-safe way to declare a Class object that can be of any type
        this.activityMap = new HashMap<>();
        activityMap.put(R.id.nav_item_home, Home.class);
        activityMap.put(R.id.nav_item_pets, Pets.class);
        activityMap.put(R.id.nav_item_cart, Cart.class);
        activityMap.put(R.id.nav_item_profile, Profile.class);
    }

    /**
     * Set a nav menu item as "Selected"
     * @param menuId The menu item id
     */
    protected void setActiveNavItem(int menuId)
    {
        if (this.bottomNavigationView == null)
            return;

        this.bottomNavigationView.setOnItemSelectedListener(null);
        this.bottomNavigationView.setSelectedItemId(menuId);
        this.bottomNavigationView.setOnItemSelectedListener(bottomNavMenuClickListener);
    }

    /**
     * Starts a new activity then closes the current
     */
    public void switchActivity(Intent intent)
    {
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
        onBackKeyPressed();
    }

    private void requireAuth()
    {
        if (!AuthSharedPrefsManager.getInstance(this).isLoggedIn())
        {
            Intent intent = new Intent(this, Login.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private final NavigationBarView.OnItemSelectedListener bottomNavMenuClickListener = item ->
    {
//        if (item.getItemId() == R.id.nav_item_home)
//        {
//            Intent intent = new Intent(this, Home.class);
//            switchActivity(intent);
//            return true;
//        }
//        else if (item.getItemId() == R.id.nav_item_pets)
//        {
//            Intent intent = new Intent(this, Pets.class);
//            switchActivity(intent);
//            return true;
//        }
//        else if (item.getItemId() == R.id.nav_item_profile)
//        {
//            Intent intent = new Intent(this, Profile.class);
//            switchActivity(intent);
//            return true;
//        }

        if (item.getItemId() != bottomNavigationView.getSelectedItemId())
        {
            Class<?> activityClass = activityMap.get(item.getItemId());
            if (activityClass != null) {
                Intent intent = new Intent(getApplicationContext(), activityClass);
                startActivity(intent);
                return true;
            }
        }
        return false;
    };
}
