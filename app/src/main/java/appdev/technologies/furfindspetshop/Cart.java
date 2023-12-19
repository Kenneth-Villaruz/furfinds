package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import appdev.technologies.furfindspetshop.database.CartHelper;
import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.helpers.AsyncWorker;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.recyclerviews.CartAdapter;
import appdev.technologies.furfindspetshop.recyclerviews.CartItem;

public class Cart extends BaseActivityBottomNav
{
    private RelativeLayout  loadingDialog;
    private LinearLayout    cartEmptyIndicator;
    private Button          btnContinueShopping;
    private Button          btnCheckout;
    private TextView        totalItemsLabel;

    private RecyclerView    cartRecyclerView;
    private CartAdapter     cartAdapter;
    private List<CartItem>  cartDataset;

    private Modal alert;
    private CartHelper      cartHelper;

    private AsyncWorker asyncWorker;
    private UserAuth userAuth;
    private Handler handler;

    @Override
    public void onAwake()
    {
        userAuth = AuthSharedPrefsManager.getInstance(this).getUser();
        asyncWorker = new AsyncWorker(this);
        handler = new Handler(Looper.getMainLooper());
        cartHelper = new CartHelper(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_cart);

        setupBottomNavMenu(R.id.bottom_nav_menu);
        setActiveNavItem(R.id.nav_item_cart);

        loadingDialog = findViewById(R.id.loading_spinner_dialog);

        // Show the "Loading" dialog
        loadingDialog.setVisibility(View.VISIBLE);

        alert = new Modal(this);

        totalItemsLabel = findViewById(R.id.tx_total_cart_items);
        cartEmptyIndicator  = findViewById(R.id.empty_cart);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
        btnCheckout = findViewById(R.id.btn_checkout);

        cartRecyclerView = findViewById(R.id.cart_recycler);
        cartDataset      = new ArrayList<>();
        cartAdapter      = new CartAdapter(cartDataset);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);

        handler.postDelayed(this::getCartData, 1200);
    }

    @Override
    public void bindEvents()
    {
        btnContinueShopping.setOnClickListener(view -> {
            Intent intent = new Intent(this, Pets.class);
            switchActivity(intent);
        });
    }

    @Override
    protected void onBackKeyPressed()
    {

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        asyncWorker.Kill();
    }

    @SuppressLint("Range")
    private void getCartData()
    {
        final int[] resultCount = {0};
        final Cursor[] cursor = new Cursor[1];

        asyncWorker.Run(() ->
        {
            cursor[0] = cartHelper.getCartItems(userAuth.getId());
            resultCount[0] = cursor[0].getCount();
        },
        () ->
        {
            if (resultCount[0] > 0)
            {
                while (cursor[0].moveToNext())
                {
                    String petName      = cursor[0].getString( cursor[0].getColumnIndex(PetsModel.COLUMN_NAME) );
                    String petCategory  = cursor[0].getString( cursor[0].getColumnIndex(PetsModel.COLUMN_CATEGORY) );
                    String petImage     = cursor[0].getString( cursor[0].getColumnIndex(PetsModel.COLUMN_IMAGE) );
                    String petPrice     = cursor[0].getString( cursor[0].getColumnIndex(PetsModel.COLUMN_SALE_PRICE));
                    String petId        = cursor[0].getString( cursor[0].getColumnIndex(DBSchema.COLUMN_ID) );

                    CartItem item = new CartItem();
                    item.setName(petName);
                    item.setCategory(petCategory);
                    item.setImage(petImage);
                    item.setPrice(petPrice);
                    item.setId(Integer.parseInt(petId));

                    cartDataset.add(item);
                }

//            cartAdapter.setAdapterItemClick(position ->
//            {
//                int petId = cartDataset.get(position).getId();
//                viewPetDetails(petId);
//            });

                cartAdapter.setAdapterItemDeleteClick(position ->
                {
                    CartItem item = cartDataset.get(position);
                    removeFromCart(item.getId(), position, item.getName());
                });

                cartEmptyIndicator.setVisibility(View.INVISIBLE);

                int lastIndex = cartDataset.size() - 1;

                cartAdapter.notifyItemInserted( lastIndex);
                cartRecyclerView.smoothScrollToPosition(lastIndex);

                countCartItems();
            }
            else
                cartEmptyIndicator.setVisibility(View.VISIBLE);

            // Hide the loading dialog
            loadingDialog.setVisibility(View.INVISIBLE);
        });
    }

    // Remove this pet from the cart
    private void removeFromCart(int petId, int position, String petName)
    {
        String msg = getString(R.string.prompt_remove_pet_cart);
        msg = msg.replaceAll("this pet", String.format("\"%s\"", petName));

        alert.prompt(msg, "Remove From Cart", () -> {

            cartHelper.removeFromCart(userAuth.getId(), petId,
            () ->
            {
                Toast.makeText(this, "Removed from cart", Toast.LENGTH_SHORT).show();
                cartDataset.remove(position);
                cartAdapter.notifyItemRemoved(position);
                countCartItems();
            },
            () -> alert.show(getString(R.string.err_action_failed), "Failure"));

        }, null);
    }

    private void countCartItems()
    {
        int count = 0;

        if (cartDataset != null)
            count = cartDataset.size();

        String total = String.format(Locale.ENGLISH,"%d %s", count, "Total Items");
        totalItemsLabel.setText(total);

        btnCheckout.setEnabled(count > 0);
        cartEmptyIndicator.setVisibility( count > 0 ? View.INVISIBLE : View.VISIBLE);
    }
}