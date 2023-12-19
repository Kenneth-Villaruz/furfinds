package appdev.technologies.furfindspetshop.recyclerviews;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.viewextensions.SquareImageView;

public class PostsRecyclerViewHolder extends RecyclerView.ViewHolder
{
    public SquareImageView postImage;

    public TextView postContent;
    public TextView postOwnerName;
    public TextView postDate;

    public PostsRecyclerViewHolder(@NonNull View itemView)
    {
        super(itemView);

        postImage = itemView.findViewById(R.id.post_image);
        postContent = itemView.findViewById(R.id.post_content);
        postDate = itemView.findViewById(R.id.post_date);
        postOwnerName = itemView.findViewById(R.id.post_owner_name);
    }
}
