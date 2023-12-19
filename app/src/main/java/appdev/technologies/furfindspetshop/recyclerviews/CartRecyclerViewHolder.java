package appdev.technologies.furfindspetshop.recyclerviews;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class CartRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public SquareImageView petImage;
    public ImageButton deleteButton;

    public TextView petName;
    public TextView petCategory;
    public TextView petPrice;

    public CartRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        petImage = itemView.findViewById(R.id.favorite_image);
        petName = itemView.findViewById(R.id.favorite_title);
        petPrice = itemView.findViewById(R.id.favorite_price);

        petCategory = itemView.findViewById(R.id.favorite_category);
        deleteButton = itemView.findViewById(R.id.btn_delete_favorite);
    }
}
