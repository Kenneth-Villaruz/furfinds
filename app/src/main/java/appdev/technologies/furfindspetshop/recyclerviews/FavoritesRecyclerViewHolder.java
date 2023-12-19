package appdev.technologies.furfindspetshop.recyclerviews;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class FavoritesRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public SquareImageView favItemImage;
    public ImageButton favDeleteButton;

    public TextView favItemTitle;
    public TextView favItemCategory;
    public TextView favItemPrice;

    public FavoritesRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        favItemImage = itemView.findViewById(R.id.favorite_image);
        favItemTitle = itemView.findViewById(R.id.favorite_title);
        favItemPrice = itemView.findViewById(R.id.favorite_price);

        favItemCategory = itemView.findViewById(R.id.favorite_category);
        favDeleteButton = itemView.findViewById(R.id.btn_delete_favorite);
    }
}
