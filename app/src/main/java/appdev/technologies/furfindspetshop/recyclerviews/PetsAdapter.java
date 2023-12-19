package appdev.technologies.furfindspetshop.recyclerviews;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.eventhandlers.OnAdapterItemClick;
import appdev.technologies.furfindspetshop.helpers.Extensions;

public class PetsAdapter extends RecyclerView.Adapter<PetsRecyclerViewHolder>
{
    private final List<PetsItem> pets;
    private Context context;

    private OnAdapterItemClick adapterItemClick;

    public PetsAdapter(List<PetsItem> animals)
    {
        this.pets = animals;
    }

    @NonNull
    @Override
    public PetsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pet_item_card, parent, false);
        return new PetsRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PetsRecyclerViewHolder holder, int position)
    {
        PetsItem pet = pets.get(position);
        Uri src = Extensions.getContentUri(this.context, pet.getImage());

        holder.petImage.layout(0,0,0,0);

        Glide.with(holder.petImage.getContext())
                .load(src)
                .placeholder(R.drawable.img_placeholder)
                .override(120,120)
                .centerCrop()
                .into(holder.petImage);

        setDisplay(holder.adoptionBadge, pet.isForAdoption());

        holder.animalName.setText(pet.getName());
        holder.animalPrice.setText(Extensions.toCurrency(pet.getSalePrice()));

        holder.itemView.setOnClickListener(view -> {
            if (adapterItemClick != null)
                adapterItemClick.onClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    public void setAdapterItemClick(OnAdapterItemClick listener) {
        adapterItemClick = listener;
    }

    public void setDisplay(View view, boolean show)
    {
        if (!show)
            view.setVisibility(View.GONE);
        else
        {
            if (view.getVisibility() != View.VISIBLE)
                view.setVisibility(View.VISIBLE);
        }
    }
}
