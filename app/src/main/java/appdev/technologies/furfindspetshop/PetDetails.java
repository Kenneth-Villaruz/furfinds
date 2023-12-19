package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import appdev.technologies.furfindspetshop.database.CartHelper;
import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.DatabaseHelper;
import appdev.technologies.furfindspetshop.database.FavoritesHelper;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.database.QueryBuilder;
import appdev.technologies.furfindspetshop.database.UsersModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Timestamps;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class PetDetails extends BaseActivity
{
    private int petId;

    private Modal alert;
    DatabaseHelper dbHelper;

    private TextView tx_petName;
    private TextView tx_category;
    private TextView tx_location;
    private TextView tx_price;
    private TextView tx_details;
    private TextView tx_petOwner;
    private TextView tx_registerDate;
    private TextView tx_petAge;
    private TextView tx_petGender;
    private TextView tx_petHealth;

    private Button btnAdopt;
    private Button btnAddToCart;
    private Button btnDropCart;

    private ImageButton btnBack;
    private ImageButton btnFav;
    private SquareImageView sq_petImage;
    private RelativeLayout adoptionBadge;
    private RelativeLayout loadingDialog;

    private FavoritesHelper favoritesHelper;
    private CartHelper cartHelper;

    private boolean isAlreadyFavorite;
    private boolean isAlreadyInCart;

    // Get the logged in user
    private UserAuth userAuth;

    private AsyncWorker asyncWorker;
    private Activity activityReference;
    private Handler handler;

    @Override
    public void onAwake()
    {
        Intent intent = getIntent();
        petId = intent.getIntExtra(DBSchema.COLUMN_ID, -1);
        dbHelper = DatabaseHelper.getInstance(this);

        favoritesHelper = new FavoritesHelper(this);
        cartHelper = new CartHelper(this);

        asyncWorker = new AsyncWorker(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_pet_details);

        activityReference = this;

        alert = new Modal(this);
        loadingDialog   = findViewById(R.id.loading_spinner_dialog);

        asyncWorker.Run(() ->
        {
            userAuth  = AuthSharedPrefsManager.getInstance(activityReference).getUser();
            isAlreadyFavorite = favoritesHelper.existsInFavorites(userAuth.getId(), petId);
            isAlreadyInCart = cartHelper.existsInCart(userAuth.getId(), petId);

        }, () ->
        {
            if (isAlreadyInCart)
            {
                showDropButton(true);
                btnAddToCart.setVisibility(View.GONE);
            }
            else
            {
                showDropButton(false);
                btnAddToCart.setVisibility(View.VISIBLE);
            }
        });
//        // Show the "Loading" dialog
//        loadingDialog.setVisibility(View.VISIBLE);

        tx_category     = findViewById(R.id.pet_category);
        tx_details      = findViewById(R.id.pet_details);
        tx_location     = findViewById(R.id.pet_location);
        tx_price        = findViewById(R.id.pet_price);
        tx_petName      = findViewById(R.id.pet_name);
        tx_petOwner     = findViewById(R.id.pet_owner_name);
        tx_registerDate = findViewById(R.id.register_date);

        tx_petAge       = findViewById(R.id.pet_age);
        tx_petHealth    = findViewById(R.id.pet_health);
        tx_petGender    = findViewById(R.id.pet_gender);

        btnAdopt        = findViewById(R.id.btn_adopt);
        btnFav          = findViewById(R.id.btn_favorite);
        btnAddToCart    = findViewById(R.id.btn_add_to_cart);
        btnBack         = findViewById(R.id.btn_back);
        btnDropCart     = findViewById(R.id.btn_remove_in_cart);

        sq_petImage     = findViewById(R.id.pet_image);
        adoptionBadge   = findViewById(R.id.adoption_badge);

        loadPetDetails();
        updateFavButtonIcon(isAlreadyFavorite);
    }

    @Override
    public void bindEvents()
    {
        btnBack.setOnClickListener(view -> goBack());
        btnFav.setOnClickListener(view -> addToFavorites());
        btnAddToCart.setOnClickListener(view -> addToCart());
        btnDropCart.setOnClickListener(view -> removeFromCart());
    }

    @Override
    protected void onBackKeyPressed() {
        goBack();
    }

    @SuppressLint("Range")
    private void loadPetDetails()
    {
        Cursor cursor = null;

        try
        {
            QueryBuilder query = new QueryBuilder();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(buildSelectDetailsQuery(query), query.getParameterBindings());

            if (!cursor.moveToFirst())
            {
                alert.show(getString(R.string.err_pet_data), "Aww Snap!", this::goBack);
                return;
            }

            String petName      = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_NAME));
            String petCategory  = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_CATEGORY));
            String petDetails   = cursor.getString(cursor.getColumnIndex(PetsModel.COLUMN_DESCRIPTION));
            String petLocation  = cursor.getString(cursor.getColumnIndex(UsersModel.COLUMN_ADDRESS));
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
            tx_location.setText(petLocation);
            tx_petHealth.setText(petHealth);
            tx_petGender.setText(petGender);

            // Process the age. Ages with 1, should have no "s" at the units
            if (Integer.parseInt(petAge) <= 1)
                petAgeUnits = petAgeUnits.replace(petAgeUnits.substring(petAgeUnits.length() - 1), "");

            petAge = String.format("%s %s", petAge, petAgeUnits);

            tx_petAge.setText(petAge);

            // is For Sale or Not
            if (petForSale.equals( String.valueOf(DBSchema.DATA_FALSE) ))
                petPrice = getString(R.string.placeholder_not_for_sale);
            else
            {
                float price = Float.parseFloat(petPrice);
                petPrice = String.format("%s %s", '\u20B1', Extensions.toCurrency(price));

                btnAddToCart.setEnabled(true);
            }
            tx_price.setText(petPrice);

            // Is for Adoption or Not (Hidden by default in layout)
            if (petForAdopt.equals( String.valueOf(DBSchema.DATA_TRUE) ))
            {
                adoptionBadge.setVisibility(View.VISIBLE);
                btnAdopt.setEnabled(true);
            }

            // Pet Description
            if (TextUtils.isEmpty(petDetails))
                petDetails = getString(R.string.placeholder_no_details);

            tx_details.setText(Html.fromHtml(petDetails));

            // Pet Register Date
            if (TextUtils.isEmpty(registerDate))
                registerDate = "Not Available";
            else
                registerDate = "Added on " + Timestamps.format(registerDate, Timestamps.FORMAT_COMPLETE_SHORT);

            tx_registerDate.setText(registerDate);

            // Pet Image
            Uri imgSrc = Extensions.getContentUri(this, petImage);
            Glide.with(sq_petImage.getContext())
                    .load(imgSrc)
                    .placeholder(R.drawable.img_placeholder)
                    .into(sq_petImage);

            // Owner Details
            String ownerName = String.join(" ", new String[] {
                    cursor.getString(cursor.getColumnIndex(UsersModel.COLUMN_FIRSTNAME)),
                    cursor.getString(cursor.getColumnIndex(UsersModel.COLUMN_MIDDLENAME)),
                    cursor.getString(cursor.getColumnIndex(UsersModel.COLUMN_LASTNAME))
            });

            tx_petOwner.setText(ownerName);
        }
        catch (Exception ex)
        {
            Log.w("logger", ex.toString());
            alert.show(getString(R.string.err_pet_data) + "\n\n" + ex.getMessage(), "Aww snap!");
        }
        finally {
            if (cursor != null)
                cursor.close();
        }
    }

    // Create the query for reading the pet details
    private String buildSelectDetailsQuery(QueryBuilder query)
    {
        String[] petFields = Extensions.prefixItems(new String[] {
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
        }, "p.");

        String[] userFields = Extensions.prefixItems(new String[] {
                UsersModel.COLUMN_FIRSTNAME,
                UsersModel.COLUMN_MIDDLENAME,
                UsersModel.COLUMN_LASTNAME,
                UsersModel.COLUMN_ADDRESS
        }, "u.");

        String[] selectFields = Extensions.mergeStringArrays(petFields, userFields);

        String pets_table   = PetsModel.TABLE_NAME + " AS p";
        String users_table  = UsersModel.TABLE_NAME + " AS u";
        String users_join   = "u." + DBSchema.COLUMN_ID;
        String pets_join    = "p." + PetsModel.COLUMN_OWNER_FK;
        String petIdCol     = "p." + DBSchema.COLUMN_ID;

        String sql = query
                .select(selectFields)
                .from(pets_table)
                .leftJoin(users_table, users_join, pets_join)
                .where(new String[][]{{ petIdCol, "=", String.valueOf(petId) }})
                .limit(1)
                .build();

        return sql;
    }

    // Add this pet to the current user's "favorites" list
    private void addToFavorites()
    {
        isAlreadyFavorite = favoritesHelper.existsInFavorites(userAuth.getId(), petId);

        if (isAlreadyFavorite)
        {
            alert.prompt(getString(R.string.prompt_remove_pet_favorites), "Remove Favorites", () -> {

                favoritesHelper.removeFromFavorites(userAuth.getId(), petId,
                        () ->
                        {
                            updateFavButtonIcon(false);
                            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        },
                        () -> alert.show(getString(R.string.err_action_failed), "Failure"));

            }, null);
        }
        else
        {
            favoritesHelper.addToFavorites(userAuth.getId(), petId, () ->
            {
                Toast.makeText(this, getString(R.string.msg_success_favorite), Toast.LENGTH_SHORT).show();
                updateFavButtonIcon(true);
            }, null);
        }
    }

    // Add this per to the current user's "cart"
    private void addToCart()
    {
        // Show the "Loading" dialog
        loadingDialog.setVisibility(View.VISIBLE);

        handler.postDelayed(() ->
        {
            asyncWorker.Run(() ->
            {
                cartHelper.addToCart(userAuth.getId(), petId, () ->
                {
                    runOnUiThread(() ->
                    {
                        Toast.makeText(this, getString(R.string.msg_success_cart), Toast.LENGTH_SHORT).show();
                        showDropButton(true);
                        btnAddToCart.setVisibility(View.GONE);
                    });
                    // updateFavButtonIcon(true);
                }, null);
            },
            () -> {
                // Show the "Loading" dialog
                loadingDialog.setVisibility(View.INVISIBLE);
            });
        }, 1200);
    }

    // Remove this pet from the cart
    private void removeFromCart()
    {
        alert.prompt(getString(R.string.prompt_remove_pet_cart), "Remove From Cart", () -> {

            cartHelper.removeFromCart(userAuth.getId(), petId,
            () ->
            {
                // updateFavButtonIcon(false);
                Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
                showDropButton(false);
                btnAddToCart.setVisibility(View.VISIBLE);
            },
            () -> alert.show(getString(R.string.err_action_failed), "Failure"));

        }, null);
    }

    // Show or Hide the "Drop" (Remove From Cart) button
    private void showDropButton(boolean show)
    {
        int visibility = show ? View.VISIBLE : View.GONE;
        btnDropCart.setVisibility(visibility);
    }

    // Show or Hide the "Add to cart" button
    private void showAddCartButton(boolean show)
    {
        int visibility = show ? View.VISIBLE : View.GONE;
        btnAddToCart.setVisibility(visibility);
    }

    // This Pet Details Activity is used by "Favorites" and "Pets" Activities.
    // We need to know which of them launched this activity and we will call that
    // variable as "caller". If the caller is not null, that means, we can go back
    // to that caller activity.
    private void goBack()
    {
        String caller = getIntent().getStringExtra(INTENT_EXTRAS_CALLER_ACTIVITY);
        Log.w("logger", "caller -> " + caller);
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
                Log.w("logger", "stack trace -> " + e.getMessage());
                // e.printStackTrace();
                switchActivity(default_goBack);
            }
        }
        else
        {
            switchActivity(default_goBack);
        }
    }

    private void updateFavButtonIcon(boolean isFavorite)
    {
        if (isFavorite)
            btnFav.setImageResource(R.drawable.ic_baseline_favorite_24);
        else
            btnFav.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asyncWorker.Kill();
    }
}