package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.DatabaseHelper;
import appdev.technologies.furfindspetshop.database.MyPetsHelper;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.database.QueryBuilder;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.PermissionsManager;
import appdev.technologies.furfindspetshop.helpers.Timestamps;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class MyPetDetails extends BaseActivity
{
    private int petId;

    private Modal alert;
    DatabaseHelper dbHelper;

    private TextView tx_petName;
    private TextView tx_category;
    private TextView tx_price;
    private TextView tx_details;
    private TextView tx_registerDate;
    private TextView tx_petAge;
    private TextView tx_petGender;
    private TextView tx_petHealth;

    private Button btnBack;
    private Button btnDelete;
    private Button btnEdit;

    private SquareImageView sq_petImage;
    private RelativeLayout adoptionBadge;

    private MyPetsHelper myPetsHelper;
    private PetRegistrationForm petEditForm;
    private ContentValues prefillDataset;

    // Get the logged in user
    private UserAuth userAuth;

    @Override
    public void onAwake()
    {
        Intent intent = getIntent();
        petId = intent.getIntExtra(DBSchema.COLUMN_ID, -1);

        userAuth     = AuthSharedPrefsManager.getInstance(this).getUser();
        dbHelper     = DatabaseHelper.getInstance(this);
        myPetsHelper = new MyPetsHelper(this);

        prefillDataset = new ContentValues();
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_my_pet_details);

        alert = new Modal(this);

        tx_category     = findViewById(R.id.pet_category);
        tx_details      = findViewById(R.id.pet_details);
        tx_price        = findViewById(R.id.pet_price);
        tx_petName      = findViewById(R.id.pet_name);
        tx_registerDate = findViewById(R.id.register_date);

        tx_petAge       = findViewById(R.id.pet_age);
        tx_petHealth    = findViewById(R.id.pet_health);
        tx_petGender    = findViewById(R.id.pet_gender);

        btnBack         = findViewById(R.id.btn_back);
        btnDelete       = findViewById(R.id.btn_delete_my_pet);
        btnEdit         = findViewById(R.id.btn_edit_pet_details);

        sq_petImage     = findViewById(R.id.pet_image);
        adoptionBadge   = findViewById(R.id.adoption_badge);

        Extensions.setButtonDrawableStartSelector(btnBack, R.drawable.transparent_back_button_icon_primary);

        loadPetDetails();
    }

    @Override
    public void bindEvents()
    {
        btnBack.setOnClickListener(view -> goBack());

        btnDelete.setOnClickListener(view ->
                alert.prompt(getString(R.string.warning_delete_pet), "Just a second", this::deletePet, null));

        btnEdit.setOnClickListener(view ->
        {
            Activity activityContext = MyPetDetails.this;

            if (PermissionsManager.canUseCamera(activityContext) && PermissionsManager.canUseStorage(activityContext))
                petEditForm.show();
            else
                PermissionsManager.checkPermissions(activityContext);
        });
    }

    @Override
    protected void onBackKeyPressed() {
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        petEditRegistrationForm.setActivity(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        petEditForm.detachViewPagerFrameChanged();
    }

    private void deletePet()
    {
        boolean delete = myPetsHelper.deletePet(petId);

        if (delete)
        {
            Intent intent = new Intent(this, MyPets.class);
            intent.putExtra(INTENT_EXTRAS_SUCCESS_MESSAGE, getString(R.string.success_delete_pet));
            switchActivity(intent);
        }
        else
            alert.show(getString(R.string.err_action_failed), "Failure");
    }

    @SuppressLint("Range")
    private void loadPetDetails()
    {
        Cursor cursor = null;

        try
        {
            QueryBuilder query = new QueryBuilder();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = buildSelectDetailsQuery(query);
            cursor = db.rawQuery(sql, query.getParameterBindings());

            if (!cursor.moveToFirst())
            {
                alert.show(getString(R.string.err_pet_data), "Aww Snap!", this::goBack);
                return;
            }

            String petName      = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_NAME));
            String petCategory  = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_CATEGORY));
            String petDetails   = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_DESCRIPTION));
            String petImage     = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_IMAGE));
            String registerDate = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_DATE_CREATED));
            String petForSale   = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_FOR_SALE));
            String petPrice     = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_SALE_PRICE));
            String petForAdopt  = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_FOR_ADOPTION));

            String petAge       = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_AGE));
            String petAgeUnits  = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_AGE_UNITS));
            String petHealth    = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_HEALTH_CONDITION));
            String petGender    = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_SEX));

            // Common Details
            tx_petName.setText(petName);
            tx_category.setText(petCategory);
            tx_petHealth.setText(petHealth);

            // Pet Gender
            prefillDataset.put(PetsModel.COLUMN_SEX, petGender);

            if (!petGender.equals(PetsModel.GENDER_MALE) && !(petGender.equals(PetsModel.GENDER_FEMALE)))
                petGender = PetsModel.GENDER_NEUTER;

            tx_petGender.setText(petGender);

            prefillDataset.put(DBSchema.COLUMN_ID,  String.valueOf(petId));
            prefillDataset.put(PetsModel.COLUMN_NAME, petName);
            prefillDataset.put(PetsModel.COLUMN_CATEGORY, petCategory);
            prefillDataset.put(PetsModel.COLUMN_HEALTH_CONDITION, petHealth);
            prefillDataset.put(PetsModel.COLUMN_AGE_UNITS, petAgeUnits);
            prefillDataset.put(PetsModel.COLUMN_DESCRIPTION, petDetails);

            // Process the age. Ages with 1, should have no "s" at the units
            //
            if (Integer.parseInt(petAge) <= 1)
                petAgeUnits = petAgeUnits.replace(petAgeUnits.substring(petAgeUnits.length() - 1), "");

            prefillDataset.put(PetsModel.COLUMN_AGE, petAge);       // Store reference to original numeric value first
            petAge = String.format("%s %s", petAge, petAgeUnits);   // Then format it to concatenated age and units

            tx_petAge.setText(petAge);

            // is For Sale or Not
            prefillDataset.put(PetsModel.COLUMN_FOR_SALE, petForSale);
            prefillDataset.put(PetsModel.COLUMN_SALE_PRICE, petPrice);

            if (petForSale.equals( String.valueOf(DBSchema.DATA_FALSE) ))
                petPrice = getString(R.string.placeholder_not_for_sale);
            else
            {
                float price = Float.parseFloat(petPrice);
                petPrice = String.format("%s %s", '\u20B1', Extensions.toCurrency(price));
            }
            tx_price.setText(petPrice);

            // Is for Adoption or Not (Hidden by default in layout)
            prefillDataset.put(PetsModel.COLUMN_FOR_ADOPTION, petForAdopt);

            if (petForAdopt.equals( String.valueOf(DBSchema.DATA_TRUE) ))
                adoptionBadge.setVisibility(View.VISIBLE);

            // Pet Description
            tx_details.setText(Html.fromHtml(petDetails));

            // Pet Register Date
            if (TextUtils.isEmpty(registerDate))
                registerDate = "Not Available";
            else
                registerDate = "Added on " + Timestamps.format(registerDate, Timestamps.FORMAT_COMPLETE_SHORT);

            tx_registerDate.setText(registerDate);

            // Pet Image
            Uri imgSrc = Extensions.getContentUri(this, petImage);

            prefillDataset.put(PetsModel.COLUMN_IMAGE, imgSrc.toString());

            Glide.with(sq_petImage.getContext())
                    .load(imgSrc)
                    .placeholder(R.drawable.img_placeholder)
                    .into(sq_petImage);

            // Prepare the pet registration Edit form with data
            setupPetEditForm(this, dbHelper, prefillDataset);

            // Enable the edit button
            btnEdit.setEnabled(true);
        }
        catch (Exception ex)
        {
            alert.show(getString(R.string.err_pet_data) + "\n\n" + ex.getMessage(), "Aww snap!");
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    private void setupPetEditForm(Activity activity, DatabaseHelper dbHelper, ContentValues prefillValues)
    {
        petEditForm = new PetRegistrationForm(this, this, userAuth, dbHelper);
        petEditForm.setFormMode(PetRegistrationForm.FORM_MODES.EDIT);
        petEditForm.setFragmentPrefillData(prefillValues);
        petEditForm.initializeViews();
        petEditForm.bindEventHandlers();
        petEditForm.setModalReference(alert);

        petEditForm.setOnEditFail(() -> {
            alert.show(getString(R.string.failure_update_pet), "Failure");
        });

        petEditForm.setOnEditSuccess(() ->
        {
            alert.show(getString(R.string.success_update_pet), "Success!", this::loadPetDetails);
        });
    }

    // Create the query for reading the pet details
    private String buildSelectDetailsQuery(QueryBuilder query)
    {
        String[] columns = {
                PetsModel.COLUMN_NAME,
                PetsModel.COLUMN_CATEGORY,
                PetsModel.COLUMN_DESCRIPTION,
                PetsModel.COLUMN_IMAGE,
                PetsModel.COLUMN_FOR_ADOPTION,
                PetsModel.COLUMN_FOR_SALE,
                PetsModel.COLUMN_SALE_PRICE,
                PetsModel.COLUMN_DATE_CREATED,
                PetsModel.COLUMN_AGE,
                PetsModel.COLUMN_AGE_UNITS,
                PetsModel.COLUMN_HEALTH_CONDITION,
                PetsModel.COLUMN_SEX
        };

        String sql = query
                .select(columns)
                .from(PetsModel.TABLE_NAME)
                .where(new String[][]{
                        { DBSchema.COLUMN_ID, "=", String.valueOf(petId) },
                        { PetsModel.COLUMN_OWNER_FK, "=", String.valueOf(userAuth.getId()) }
                })
                .limit(1)
                .build();

        return sql;
    }

    // This Pet Details Activity is used by "Favorites" and "Pets" Activities.
    // We need to know which of them launched this activity and we will call that
    // variable as "caller". If the caller is not null, that means, we can go back
    // to that caller activity.
    //
    // However, if the pet registration edit form is currently visible,
    // close the form instead. Do not leave the current page
    //
    private void goBack()
    {
        if (petEditForm.isVisible())
        {
            petEditForm.handleFormCancellation();
            return;
        }

        String caller = getIntent().getStringExtra(INTENT_EXTRAS_CALLER_ACTIVITY);
        Intent default_goBack = new Intent(this, Pets.class);

        if (!TextUtils.isEmpty(caller))
        {
            try
            {
                Intent intent = new Intent(this, Class.forName(caller));
                switchActivity(intent);
            }
            catch (ClassNotFoundException e)
            {
                switchActivity(default_goBack);
            }
        }
        else
        {
            switchActivity(default_goBack);
        }
    }
}