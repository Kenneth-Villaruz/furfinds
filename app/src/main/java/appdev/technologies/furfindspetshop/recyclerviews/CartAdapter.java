package appdev.technologies.furfindspetshop.recyclerviews;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.eventhandlers.OnAdapterItemClick;
import appdev.technologies.furfindspetshop.eventhandlers.OnAdapterItemDeleteClick;
import appdev.technologies.furfindspetshop.helpers.Extensions;

public class CartAdapter extends RecyclerView.Adapter<CartRecyclerViewHolder>
{
    private final List<CartItem> cartItems;
    private Context context;

    private OnAdapterItemClick adapterItemClick;
    private OnAdapterItemDeleteClick adapterItemDeleteClick;

    public CartAdapter(List<CartItem> cartItems)
    {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_favorites, parent, false);
        return new CartRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRecyclerViewHolder holder, int position)
    {
        CartItem item = cartItems.get(position);
        Uri src = Extensions.getContentUri(this.context, item.getImage());

        Glide.with(holder.petImage.getContext())
                .load(src)
                .into(holder.petImage);

        holder.petName.setText(item.getName());
        holder.petCategory.setText(item.getCategory());

        if (!TextUtils.isEmpty(item.getPrice()))
        {
            float price_raw = Float.parseFloat(item.getPrice());
            String price = String.format("%s %s", '\u20B1', Extensions.toCurrency(price_raw));
            holder.petPrice.setText(price);
        }

        holder.itemView.setOnClickListener(view -> {
            if (adapterItemClick != null)
                adapterItemClick.onClick(holder.getAdapterPosition());
        });

        holder.deleteButton.setOnClickListener(view -> {
            if (adapterItemDeleteClick != null)
                adapterItemDeleteClick.onClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount()
    {
        return cartItems.size();
    }

    public void setAdapterItemClick(OnAdapterItemClick listener)
    {
        adapterItemClick = listener;
    }

    public void setAdapterItemDeleteClick(OnAdapterItemDeleteClick listener)
    {
        adapterItemDeleteClick = listener;
    }
}
