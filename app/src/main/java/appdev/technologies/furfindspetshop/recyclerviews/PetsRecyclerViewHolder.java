package appdev.technologies.furfindspetshop.recyclerviews;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class PetsRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public SquareImageView petImage;
    public ImageView adoptionBadge;

    public TextView animalName;
    public TextView animalPrice;

    public PetsRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        petImage = itemView.findViewById(R.id.item_card_image);
        adoptionBadge = itemView.findViewById(R.id.img_adopt_badge);

        animalName = itemView.findViewById(R.id.item_card_title);
        animalPrice = itemView.findViewById(R.id.item_card_price_text);
    }
}
