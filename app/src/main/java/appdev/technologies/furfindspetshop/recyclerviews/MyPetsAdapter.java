package appdev.technologies.furfindspetshop.recyclerviews;

import android.content.Context;
import android.net.Uri;
import android.text.Html;
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

public class MyPetsAdapter extends RecyclerView.Adapter<MyPetsRecyclerViewHolder>
{
    private final List<MyPetsItem> myPets;
    private Context context;

    private OnAdapterItemClick adapterItemClick;
    private OnAdapterItemDeleteClick adapterItemDeleteClick;

    public MyPetsAdapter(List<MyPetsItem> myPetsItem)
    {
        this.myPets = myPetsItem;
    }

    @NonNull
    @Override
    public MyPetsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_my_pets,parent, false);
        return new MyPetsRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetsRecyclerViewHolder holder, int position)
    {
        MyPetsItem item = myPets.get(position);
        Uri src = Extensions.getContentUri(this.context, item.getImage());

        holder.myPetImage.layout(0,0,0,0);

        Glide.with(holder.myPetImage.getContext())
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .override(64,64)
                .centerCrop()
                .into(holder.myPetImage);

        holder.myPetName.setText(item.getName());
        holder.myPetCategory.setText(item.getCategory());
        holder.myPetDescription.setText(Html.fromHtml(item.getDescription()));

        holder.itemView.setOnClickListener(view -> {
            if (adapterItemClick != null)
                adapterItemClick.onClick(holder.getAdapterPosition());
        });
//
//        holder.favDeleteButton.setOnClickListener(view -> {
//            if (adapterItemDeleteClick != null)
//                adapterItemDeleteClick.onClick(holder.getAdapterPosition());
//        });
    }

    @Override
    public int getItemCount() {
        return myPets.size();
    }

    public void setAdapterItemClick(OnAdapterItemClick listener) {
        adapterItemClick = listener;
    }
}


