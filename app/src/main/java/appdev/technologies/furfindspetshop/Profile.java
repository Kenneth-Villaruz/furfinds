package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Handler;

import appdev.technologies.furfindspetshop.database.FavoritesHelper;
import appdev.technologies.furfindspetshop.database.MyPetsHelper;
import appdev.technologies.furfindspetshop.database.MyProfileHelper;
import appdev.technologies.furfindspetshop.database.UsersModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;

public class Profile extends BaseActivityBottomNav
{
    private Button btnMyFavorites;
    private Button btnMyFollowers;
    private Button btnMyPets;

    private TextView tx_TotalPets;
    private TextView tx_TotalFavs;
    private TextView label_Username;

    private TextView tx_Name;
    private TextView tx_Contact;
    private TextView tx_Address;
    private TextView tx_Username;
    private TextView tx_Email;

    private MyProfileHelper         myProfileHelper;
    private MyPetsHelper            myPetsHelper;
    private FavoritesHelper         favoritesHelper;
    private FloatingActionButton    fab_logout;
    private UserAuth userAuth;

    private RelativeLayout loadingDialog;

    private AsyncWorker asyncWorker;
    private Modal alert;

    private Handler handler;

    @Override
    public void onAwake()
    {
        handler = new Handler(Looper.getMainLooper());

        asyncWorker = new AsyncWorker(this);

        favoritesHelper = new FavoritesHelper(this);
        myPetsHelper    = new MyPetsHelper(this);
        myProfileHelper = new MyProfileHelper(this);
    }

    @SuppressLint("Range")
    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_profile);

        setupBottomNavMenu(R.id.bottom_nav_menu);
        setActiveNavItem(R.id.nav_item_profile);

        alert = new Modal(this);

        // Setup the Floating Action Button's appearance (FAB)
        //
        fab_logout = findViewById(R.id.fab_logout);

        int fabColor = getResources().getColor(R.color.color_danger);
        ColorStateList fabColorStateList = ColorStateList.valueOf(fabColor);

        fab_logout.setBackgroundTintList(fabColorStateList);

        loadingDialog   = findViewById(R.id.loading_spinner_dialog);

        // Show the "Loading" dialog
        loadingDialog.setVisibility(View.VISIBLE);

        btnMyFavorites  = findViewById(R.id.btn_favorites);
        btnMyPets       = findViewById(R.id.btn_my_pets);

        tx_TotalPets = findViewById(R.id.total_pets);
        tx_TotalFavs = findViewById(R.id.total_favorites);
        label_Username = findViewById(R.id.label_username);

        tx_Name     = findViewById(R.id.section_name_value);
        tx_Contact  = findViewById(R.id.section_contact_value);
        tx_Address  = findViewById(R.id.section_address_value);

        tx_Username = findViewById(R.id.section_username_value);
        tx_Email    = findViewById(R.id.section_email_value);

        // Variables to get the profile details
        final Cursor[] cursor = new Cursor[1];
        final Activity activityReference = this;

        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                asyncWorker.Run(() -> {
                    // Get the logged in user
                    userAuth = AuthSharedPrefsManager.getInstance(activityReference).getUser();

                    // Get the details of this user's profile
                    cursor[0] = myProfileHelper.getBasicDetails(userAuth.getId());
                },
                () -> {

                    String totalFavorites = String.valueOf(favoritesHelper.countFavorites(userAuth.getId()));
                    tx_TotalFavs.setText(totalFavorites);

                    String totalPets = String.valueOf(myPetsHelper.countPets(userAuth.getId()));
                    tx_TotalPets.setText(totalPets);
                    label_Username.setText( userAuth.getUsername() );

                    // Display the profile details such as name, contact, etc
                    if (cursor[0].moveToFirst())
                    {
                        String name = String.join(" ", new String[] {
                                cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_FIRSTNAME)),
                                cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_MIDDLENAME)),
                                cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_LASTNAME)),
                        });

                        tx_Name.setText(name);

                        String contact = cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_CONTACT));
                        tx_Contact.setText(contact);

                        String address = cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_ADDRESS));
                        tx_Address.setText(address);

                        String email = cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_EMAIL));
                        tx_Email.setText(email);

                        String username = cursor[0].getString(cursor[0].getColumnIndex(UsersModel.COLUMN_USERNAME));
                        tx_Username.setText(username);
                    }

                    loadingDialog.setVisibility(View.INVISIBLE);
                });
            }
        }, 2000);
    }

    @Override
    public void bindEvents()
    {
        btnMyFavorites.setOnClickListener(view -> showFavorites());
        btnMyPets.setOnClickListener(view -> showMyPets());

        fab_logout.setOnClickListener(view -> {
            alert.prompt(getString(R.string.prompt_logout) ,"Log out", () -> {
                // In your logout method
                AuthSharedPrefsManager.getInstance(this).clear();
                Intent login = new Intent(this, Login.class);
                switchActivity(login);
            }, null);
        });
    }

    @Override
    protected void onBackKeyPressed() {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asyncWorker.Kill();
    }

    private void showFavorites()
    {
        Intent favorites = new Intent(this, Favorites.class);
        switchActivity(favorites);
    }

    private void showMyPets()
    {
        Intent favorites = new Intent(this, MyPets.class);
        switchActivity(favorites);
    }
}