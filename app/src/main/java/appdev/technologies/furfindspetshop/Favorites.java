package appdev.technologies.furfindspetshop;

import  androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.FavoritesHelper;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.recyclerviews.FavoritesAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.FavoritesItem;

public class Favorites extends BaseActivity
{
    private UserAuth userAuth;

    private TextView emptyFavoritesLabel;
    private Button btnBack;
    private RecyclerView favoritesRecyclerView;

    private FavoritesHelper favoritesHelper;
    private FavoritesAdapter favoritesAdapter;
    private List<FavoritesItem> favoritesDataset;
    private Modal alert;

    @Override
    public void onAwake()
    {
        // Get the logged in user
        userAuth = AuthSharedPrefsManager.getInstance(this).getUser();
        favoritesHelper = new FavoritesHelper(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_favorites);

        alert = new Modal(this);

        btnBack = findViewById(R.id.favorites_btn_back);
        emptyFavoritesLabel = findViewById(R.id.empty_favorites);

        favoritesRecyclerView = findViewById(R.id.favorites_recycler);
        favoritesDataset = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(favoritesDataset);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        getFavorites();
    }

    @Override
    public void bindEvents() {
        btnBack.setOnClickListener(view -> goBack());
    }

    @Override
    protected void onBackKeyPressed() {
        goBack();
    }

    @SuppressLint("Range")
    private void getFavorites()
    {
        Cursor cursor = favoritesHelper.getFavorites(userAuth.getId());

        int length = cursor.getCount();

        if (length > 0)
        {
            emptyFavoritesLabel.setVisibility(View.INVISIBLE);

            while (cursor.moveToNext())
            {
                String petName      = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_NAME) );
                String petCategory  = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_CATEGORY) );
                String petImage     = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_IMAGE) );
                String petPrice     = cursor.getString( cursor.getColumnIndex(PetsModel.COLUMN_SALE_PRICE));
                String petId        = cursor.getString( cursor.getColumnIndex(DBSchema.COLUMN_ID) );

                FavoritesItem item = new FavoritesItem();
                item.setName(petName);
                item.setCategory(petCategory);
                item.setImage(petImage);
                item.setPrice(petPrice);
                item.setId(Integer.parseInt(petId));

                favoritesDataset.add(item);
            }

            favoritesAdapter.setAdapterItemClick(position ->
            {
                int petId = favoritesDataset.get(position).getId();
                viewPetDetails(petId);
            });

            favoritesAdapter.setAdapterItemDeleteClick(position ->
            {
                FavoritesItem item = favoritesDataset.get(position);
                HashMap<String, Object> data = new HashMap<>();

                data.put("petId",   item.getId());
                data.put("petName", item.getName());

                deleteFromFavorites(data, position);
            });

            favoritesAdapter.notifyItemInserted( favoritesDataset.size() - 1 );
        }
        else
        {
            emptyFavoritesLabel.setVisibility(View.VISIBLE);
        }
    }

    private void viewPetDetails(int petId)
    {
        Log.w("logger", String.valueOf(petId));
        Intent petDetails = new Intent(this, PetDetails.class);
        petDetails.putExtra(DBSchema.COLUMN_ID, petId);
        petDetails.putExtra(INTENT_EXTRAS_CALLER_ACTIVITY, getClass().getName());

        switchActivity(petDetails);
    }

    private void deleteFromFavorites(HashMap<String, Object> data, int position)
    {
        Integer petId = (Integer) data.get("petId");
        String petName = (String) data.get("petName");

        String msg = getString(R.string.prompt_remove_pet_favorites);
        msg = msg.replaceAll("this pet", String.format("\"%s\"", petName));

        alert.prompt(msg, "Remove Favorites", () -> {

            favoritesHelper.removeFromFavorites(userAuth.getId(), petId,
            () ->
            {
                favoritesDataset.remove(position);
                favoritesAdapter.notifyItemRemoved(position);

                if (favoritesDataset.size() > 0)
                    emptyFavoritesLabel.setVisibility(View.INVISIBLE);
                else
                    emptyFavoritesLabel.setVisibility(View.VISIBLE);

                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            },
            () -> alert.show(getString(R.string.err_action_failed), "Failure"));

        }, null);
    }

    private void goBack() {
        Intent profile = new Intent(this, Profile.class);
        switchActivity(profile);
    }
}