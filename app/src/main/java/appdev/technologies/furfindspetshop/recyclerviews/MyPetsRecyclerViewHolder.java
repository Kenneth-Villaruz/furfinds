package appdev.technologies.furfindspetshop.recyclerviews;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class MyPetsRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public SquareImageView myPetImage;
    public Button btnDetails;

    public TextView myPetName;
    public TextView myPetCategory;
    public TextView myPetDescription;

    public MyPetsRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        myPetName = itemView.findViewById(R.id.mypet_title);
        myPetImage = itemView.findViewById(R.id.mypet_image);
        myPetCategory = itemView.findViewById(R.id.mypet_category);
        myPetDescription = itemView.findViewById(R.id.mypet_description);

        //btnDetails = itemView.findViewById(R.id.btn_mypet_details);
    }
}
