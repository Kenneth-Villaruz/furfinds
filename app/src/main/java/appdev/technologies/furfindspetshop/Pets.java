package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.PetsController;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.recyclerviews.PetsAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.PetsItem;
import appdev.technologies.furfindspetshop.viewextensions.PetItemCard;
import appdev.technologies.furfindspetshop.viewextensions.RecyclerGridColGaps;

public class Pets extends BaseActivityBottomNav
{
    private Spinner         searchFiltersDropdown;
    private TextView        searchTerm;
    private TextView        searchCount;
    private Button          searchWrapperClose;
    private LinearLayout    searchWrapperContainer;
    private TextView        usernameTextView;

    private Button          findButton;
    private EditText        searchBar;

    private TextView        newPets;
    private LinearLayout    itemCardsContainer;
    private RelativeLayout  loadingDialog;
    private Modal           alert;
    private PetsController  petsController;

    private RecyclerView    searchResultsRecyclerView;

    private UserAuth        userAuth;
    private AsyncWorker     asyncWorker;
    private Handler         handler;

    private int cardViewGap16;
    private int cardViewGap8;
    private int cardViewGap4;

    private int cardRadius;
    private int cardElevation;

    private final int RECYCLER_GRID_COLS = 2;
    private int GRID_COL_SPACING;

    @Override
    public void onAwake()
    {
        GRID_COL_SPACING = getResources().getDimensionPixelSize(R.dimen.padding_8);

        cardRadius = Extensions.dpToPx(this, 10);
        cardElevation = Extensions.dpToPx(this, 12);
        cardViewGap16 = Extensions.dpToPx(this, 16);
        cardViewGap8 = cardViewGap16 / 2;
        cardViewGap4 = cardViewGap8 / 2;

        asyncWorker = new AsyncWorker(this);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_pets);

        setupBottomNavMenu(R.id.bottom_nav_menu);
        setActiveNavItem(R.id.nav_item_pets);

        petsController = new PetsController(this);

        alert = new Modal(this);
        usernameTextView = findViewById(R.id.label_username);
        loadingDialog    = findViewById(R.id.loading_spinner_dialog);

        // Show the "Loading" dialog
        loadingDialog.setVisibility(View.VISIBLE);

//        asyncWorker.Run(() ->
//        {
//            // Run these block of code in the background
//
//            // Get the information of the currently logged on user
//            userAuth = AuthSharedPrefsManager.getInstance(this).getUser();
//        },
//        () ->
//        {
//            usernameTextView.setText(userAuth.getUsername());
//        });

        searchResultsRecyclerView = findViewById(R.id.search_results_recycler);

        itemCardsContainer      = findViewById(R.id.item_card_container);
        searchWrapperContainer  = findViewById(R.id.search_wrapper_container);
        searchWrapperClose      = findViewById(R.id.search_results_close);
        searchCount             = findViewById(R.id.search_count);
        searchTerm              = findViewById(R.id.search_term);
        searchFiltersDropdown   = findViewById(R.id.search_filter);

        ArrayAdapter<String> searchFilterItems = Extensions.buildSelectMenu(this, R.array.pet_category_search_filters);

        searchFilterItems.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchFiltersDropdown.setAdapter(searchFilterItems);

        findButton = findViewById(R.id.find_button);
        searchBar = findViewById(R.id.search_bar);

        asyncWorker.Run(() ->
        {
            // Run these block of code in the background

            // Get the information of the currently logged on user
            userAuth = AuthSharedPrefsManager.getInstance(this).getUser();
        },
        () ->
        {
            usernameTextView.setText(userAuth.getUsername());

            handler.postDelayed(this::showPetsForYou, 1500);
        });
    }

    @Override
    public void bindEvents()
    {
        findButton.setOnClickListener(view -> {
            String searchTerm = searchBar.getText().toString();

            if (TextUtils.isEmpty(searchTerm))
            {
                alert.show(getString(R.string.warn_empty_search), "Oops!");
                searchBar.requestFocus();
            }
            else
                showSearchResults(true, searchTerm);
        });

        searchWrapperClose.setOnClickListener(view -> showSearchResults(false, null));

        searchFiltersDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    @Override
    public void onBackKeyPressed() {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asyncWorker.Kill();
    }

    @SuppressLint("Range")
    private void showPetsForYou()
    {
        Activity activityReference = this;
        final int[] resultCount = {0};
        final Cursor[] cursor = new Cursor[1];

        asyncWorker.Run(() ->
        {
            cursor[0] = petsController.getPetsForYou();
            resultCount[0] = cursor[0].getCount();
        },
        () ->
        {
            // Add the view holder into the scrollview container
            if (resultCount[0] > 0)
            {
                while (cursor[0].moveToNext())
                {
                    // Store the cardviews here. They will be displayed horizontally,
                    // two columns per row
                    LinearLayout cardViewHolder = createCardViewHolder();

                    // Create two cardviews then move the cursor to the next record
                    for (int i = 0; i < 2; i++)
                    {
                        if (cursor[0].isAfterLast())
                            break;

                        HashMap<String, String> itemData = new HashMap<>();

                        itemData.put("petId", cursor[0].getString(cursor[0].getColumnIndex(DBSchema.COLUMN_ID)));
                        itemData.put("petName", cursor[0].getString(cursor[0].getColumnIndex(PetsModel.COLUMN_NAME)));
                        itemData.put("petImageSrc", cursor[0].getString(cursor[0].getColumnIndex(PetsModel.COLUMN_IMAGE)));
                        itemData.put("petPrice", cursor[0].getString(cursor[0].getColumnIndex(PetsModel.COLUMN_SALE_PRICE)));
                        itemData.put("petOwner", cursor[0].getString(cursor[0].getColumnIndex(PetsModel.COLUMN_OWNER_FK)));
                        itemData.put("forAdopt", cursor[0].getString(cursor[0].getColumnIndex(PetsModel.COLUMN_FOR_ADOPTION)));

                        PetItemCard card = createItemCard(activityReference, itemData, i);
                        cardViewHolder.addView(card);

                        // Move to the next item in the cursor only if it's not the last iteration of the loop
                        if (i < 1)
                            cursor[0].moveToNext();
                    }

                    itemCardsContainer.addView(cardViewHolder);
                }
            }

            if (cursor[0] != null)
                cursor[0].close();

            // Finally, hide the "Loading" dialog after completing the long-running tasks
            loadingDialog.setVisibility(View.INVISIBLE);
        });
    }

    private PetItemCard createItemCard(Context context, HashMap<String, String> itemData, int cardPosition)
    {
        PetItemCard card = new PetItemCard(context);

        card.setItemNameText(itemData.get("petName"));
        card.setItemPriceText(itemData.get("petPrice"));
        card.setItemImage(itemData.get("petImageSrc"));
        //Log.w("logger", itemData.get("petName") + " is for adopt -> " + itemData.get("forAdopt"));
        String isForAdopt = itemData.get("forAdopt");
        Log.w("logger", itemData.get("petName") + " is for adopt -> " + isForAdopt);
        boolean forAdopt = !Extensions.StrNullOrEmpty(isForAdopt) && Integer.parseInt(isForAdopt) == DBSchema.DATA_TRUE;
        Log.w("logger", "which means: " + forAdopt);
        card.setForAdopt( forAdopt );

        card.setOnClickListener(view -> {
            int petId = Integer.parseInt(Objects.requireNonNull( itemData.get("petId") ));
            int owner = Integer.parseInt(Objects.requireNonNull( itemData.get("petOwner") ));

            viewPetDetails(petId, owner);
        });

        // Set layout parameters for the CardView
        LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f);

        // Set margins for ...
        if (cardPosition == 0)
            // 1st card view
            cardViewParams.setMargins(cardViewGap4, 0, cardViewGap8, cardViewGap16);
        else
            // 2nd card view
            cardViewParams.setMargins(cardViewGap8, 0, cardViewGap4, cardViewGap16);

        card.setCardElevation(cardElevation);
        card.setRadius(cardRadius);
        card.setLayoutParams(cardViewParams);

        return card;
    }

    private LinearLayout createCardViewHolder()
    {
        LinearLayout cardViewHolder = new LinearLayout(this);
        cardViewHolder.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        cardViewHolder.setOrientation(LinearLayout.HORIZONTAL);

        return cardViewHolder;
    }

    private void showSearchResults(boolean show, String term)
    {
        if (!show)
        {
            searchTerm.setText("");
            searchCount.setText("");
            searchBar.setText("");
            searchBar.clearFocus();

            searchResultsRecyclerView.setAdapter(null);
            searchWrapperContainer.setVisibility(View.INVISIBLE);
        }
        else
        {
            searchTerm.setText(String.format("\"%s\"", term));
            applySearch(term);
            searchWrapperContainer.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("Range")
    private void applySearch(String searchTerm)
    {
        Cursor cursor = petsController.findPets(searchTerm, null);

        int length = cursor.getCount();
        searchCount.setText(String.format("%d found", length));

        if (length > 0)
        {
            searchResultsRecyclerView.setLayoutManager(new GridLayoutManager(this, RECYCLER_GRID_COLS));
            searchResultsRecyclerView.addItemDecoration(new RecyclerGridColGaps(GRID_COL_SPACING, RecyclerGridColGaps.SpacingModes.GAP_ALL));

            List<PetsItem> pets = new ArrayList<>();

            while (cursor.moveToNext())
            {
                String petName = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_NAME) );
                String petPrice = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_SALE_PRICE) );
                String petImage = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_IMAGE) );
                String petId = cursor.getString( cursor.getColumnIndex(DBSchema.COLUMN_ID) );
                String petOwner = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_OWNER_FK) );

                float price = 0.0f;

                if (!TextUtils.isEmpty(petPrice))
                    price = Float.parseFloat(petPrice);

                PetsItem item = new PetsItem();
                item.setName(petName);
                item.setSalePrice(price);
                item.setImage(petImage);
                item.setId(Integer.parseInt(petId));
                item.setOwnerFK(Integer.parseInt(petOwner));
                pets.add(item);
            }

            PetsAdapter adapter = new PetsAdapter(pets);
            adapter.setAdapterItemClick(position ->
            {
                PetsItem item = pets.get(position);
                int petId = item.getId();
                viewPetDetails(petId, item.getOwnerFK());
            });
            searchResultsRecyclerView.setAdapter(adapter);
        }
    }

    private void viewPetDetails(int petId, int ownerFk)
    {
        Intent petDetails;

        // If the pet is owned by current user, direct them to "my pet details" page
        if (ownerFk == userAuth.getId())
            petDetails = new Intent(this, MyPetDetails.class);
        else
            petDetails = new Intent(this, PetDetails.class);

        petDetails.putExtra(INTENT_EXTRAS_CALLER_ACTIVITY, getClass().getName());
        petDetails.putExtra(DBSchema.COLUMN_ID, petId);

        switchActivity(petDetails);
    }
    /*
   if (!animals.isEmpty())
        {
            String totalAnimals = String.format("%s (%d)", getString(R.string.pets_for_you), animals.size());
            newPets.setText(totalAnimals);
            animalsRecyclerView.setLayoutManager(new GridLayoutManager(this, RECYCLER_GRID_COLS));
            animalsRecyclerView.addItemDecoration(new RecyclerGridColGaps(GRID_COL_SPACING, RecyclerGridColGaps.SpacingModes.GAP_ALL));
            animalsRecyclerView.setAdapter(new AnimalsRecyclerAdapter(animals));
        }
    */
}