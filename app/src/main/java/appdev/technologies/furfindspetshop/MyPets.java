package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.DatabaseHelper;
import appdev.technologies.furfindspetshop.database.MyPetsHelper;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.PermissionsManager;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.recyclerviews.MyPetsAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.MyPetsItem;

public class MyPets extends BaseActivity
{
    private TextView                emptyPetsLabel;
    private TextView                totalPetsLabel;
    private Button                  btnBack;
    private FloatingActionButton    fab_addPets;

    private RecyclerView            petsRecyclerView;
    private List<MyPetsItem>        petsRecyclerDataset;
    private MyPetsAdapter           myPetsAdapter;

    private MyPetsHelper            myPetsHelper;
    private Modal                   alert;

    private UserAuth                userAuth;
    private DatabaseHelper          dbHelper;
    private PetRegistrationForm     registrationForm;

    private RelativeLayout          loadingDialog;

    private AsyncWorker asyncWorker;

    @Override
    public void onAwake()
    {
        // Get the logged in user
        asyncWorker = new AsyncWorker(this);
        myPetsHelper     = new MyPetsHelper(this);
        dbHelper         = DatabaseHelper.getInstance(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_my_pets);

        String successMessage = getIntent().getStringExtra(INTENT_EXTRAS_SUCCESS_MESSAGE);

        if (successMessage != null)
            Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show();

        alert = new Modal(this);

        btnBack =  findViewById(R.id.my_pets_btn_back);
        Extensions.setButtonDrawableStartSelector(btnBack, R.drawable.transparent_back_button_icon_primary);

        emptyPetsLabel = findViewById(R.id.empty_pets);
        totalPetsLabel = findViewById(R.id.tx_total_pets);
        loadingDialog  = findViewById(R.id.loading_spinner_dialog);

        // Setup the Floating Action Button's appearance (FAB)
        //
        fab_addPets = findViewById(R.id.fab_add_pets);

        int fabColor = getResources().getColor(R.color.primary_dark);
        ColorStateList fabColorStateList = ColorStateList.valueOf(fabColor);

        fab_addPets.setBackgroundTintList(fabColorStateList);

        petsRecyclerView = findViewById(R.id.pets_recycler);
        petsRecyclerDataset = new ArrayList<>();
        myPetsAdapter = new MyPetsAdapter(petsRecyclerDataset);
        petsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        petsRecyclerView.setAdapter(myPetsAdapter);

        registrationForm = new PetRegistrationForm(MyPets.this, MyPets.this, userAuth, dbHelper);
        registrationForm.initializeViews();
        registrationForm.setModalReference(alert);
        registrationForm.setFormMode(PetRegistrationForm.FORM_MODES.REGISTER);

        asyncWorker.Run(() ->
        {
            // Run these block of code in the background

            // Show a "Loading" dialog
            runOnUiThread(() -> loadingDialog.setVisibility(View.VISIBLE));

            // Get the information of the currently logged on user
            userAuth = AuthSharedPrefsManager.getInstance(this).getUser();

            // Clean up the temporary pictures directory, which is used for
            // temporarily storing captured photos
            Extensions.truncateTempPhotoDirectory(this, null);
        },
        () ->
        {
            // Run these block of code on UI thread

            registrationForm.setUserAuth(userAuth);
            fab_addPets.setVisibility(View.VISIBLE);

            getPets();
        });
    }

    @Override
    public void bindEvents()
    {
        btnBack.setOnClickListener(view -> goBack());

        registrationForm.bindEventHandlers();
        registrationForm.setOnFormShow(() -> fab_addPets.setVisibility(View.INVISIBLE));
        registrationForm.setOnFormHide(() -> fab_addPets.setVisibility(View.VISIBLE));

        registrationForm.setOnRegisterFail(() -> {
            alert.show(getString(R.string.err_action_failed), "Failure");
        });

        registrationForm.setOnRegisterSuccess( (rowId, item) -> {

            petsRecyclerDataset.add(item);

            // Refresh the list Then scroll to the last added item, when OK is clicked
            alert.show(getString(R.string.success_register_pet), getString(R.string.placeholder_success), () ->
            {
                runOnUiThread(() ->
                {
                    emptyPetsLabel.setVisibility(View.INVISIBLE);
                    myPetsAdapter.setAdapterItemClick(position ->
                    {
                        int petId = petsRecyclerDataset.get(position).getId();
                        viewPetDetails(petId);
                    });

                    int lastIndex = petsRecyclerDataset.size() - 1;

                    myPetsAdapter.notifyItemInserted( lastIndex);
                    petsRecyclerView.smoothScrollToPosition(lastIndex);

                    countPets();
                });
            });
        });

        fab_addPets.setOnClickListener(view ->
        {
            Activity activityContext = MyPets.this;

            if (PermissionsManager.canUseCamera(activityContext) && PermissionsManager.canUseStorage(activityContext))
                registrationForm.show();

            else
                PermissionsManager.checkPermissions(activityContext);
        });

        setOnPermissionRequested(() ->
        {
            if (!canUseCamera() || !canUseStorage())
            {
                alert.show(
                        getString(R.string.consent_camera_storage),
                        getString(R.string.alert_title_consent),
                        () -> PermissionsManager.checkPermissions(MyPets.this)
                );
            }
        });
    }

    @Override
    protected void onBackKeyPressed() {
        goBack();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        registrationForm.detachViewPagerFrameChanged();
    }

    private void goBack()
    {
        if (registrationForm.isVisible())
        {
            registrationForm.handleFormCancellation();
            return;
        }

        Intent profile = new Intent(this, Profile.class);
        switchActivity(profile);
    }

    @SuppressLint("Range")
    private void getPets()
    {
        // In Java, when you use a variable inside a lambda expression, it must be final or effectively final.
        // An effectively final variable is one that is not modified after it is initialized. This requirement
        // exists because lambda expressions in Java capture values, not variables.
        //
        // To fix this, we could use an array or a list to hold the resultCount, as arrays and lists are objects and
        // the "final" requirement applies to the reference itself and not its contents.
        //
        // Note: In Java, when you mark a variable as final, it means that the reference cannot be changed to point to a
        // different object or value. However, it does not mean that the state of the object itself cannot be changed.
        //
        // In other words, we can only assign the reference once but still change its state or value
        //
        final int[] resultCount = {0};

        asyncWorker.Run(() ->   // Do the heavy processing / long running task in the background
        {
            Cursor cursor = myPetsHelper.getPets(userAuth.getId());
            resultCount[0] = cursor.getCount();

            while (cursor.moveToNext())
            {
                String petName      = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_NAME) );
                String petCategory  = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_CATEGORY) );
                String petImage     = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_IMAGE) );
                String petDetail    = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_DESCRIPTION));
                String petId        = cursor.getString( cursor.getColumnIndex(DBSchema.COLUMN_ID) );

                MyPetsItem item = new MyPetsItem();
                item.setName(petName);
                item.setCategory(petCategory);
                item.setImage(petImage);
                item.setDescription(petDetail);
                item.setId(Integer.parseInt(petId));

                petsRecyclerDataset.add(item);
            }
        },
        // Invoke on UI thread (Run on UI) after the background task is done
        () ->
        {
            if (resultCount[0] > 0)
            {
                // Hide the "Empty Pets" text
                emptyPetsLabel.setVisibility(View.INVISIBLE);

                // Make the newly added pet item clickable
                myPetsAdapter.setAdapterItemClick(position ->
                {
                    int petId = petsRecyclerDataset.get(position).getId();
                    viewPetDetails(petId);
                });

                // Update the recycler view to show the newly added item
                myPetsAdapter.notifyItemInserted(petsRecyclerDataset.size() - 1);
            }
            else
                // Show the "Empty Pets" text when there are not pets
                emptyPetsLabel.setVisibility(View.VISIBLE);

            // Show how many pets are there
            countPets();

            // Finally, hide the "Loading" dialog after completing the long-running tasks
            loadingDialog.setVisibility(View.INVISIBLE);
        });
    }

    private void viewPetDetails(int petId)
    {
        Intent petDetails = new Intent(this, MyPetDetails.class);

        petDetails.putExtra(INTENT_EXTRAS_CALLER_ACTIVITY, getClass().getName());
        petDetails.putExtra(DBSchema.COLUMN_ID, petId);

        switchActivity(petDetails);
    }

    private void countPets()
    {
        int count = 0;

        if (petsRecyclerDataset != null)
            count = petsRecyclerDataset.size();

        String total = String.format(Locale.ENGLISH,"%d %s", count, "Total Pets");

        totalPetsLabel.setText(total);
    }
}