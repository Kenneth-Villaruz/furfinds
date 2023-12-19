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
import appdev.technologies.furfindspetshop.helpers.Timestamps;

public class PostsAdapter extends RecyclerView.Adapter<PostsRecyclerViewHolder>
{
    private final List<PostsItem> postItems;
    private Context context;

    private OnAdapterItemClick adapterItemClick;
    private OnAdapterItemDeleteClick adapterItemDeleteClick;

    public PostsAdapter(List<PostsItem> postItems)
    {
        this.postItems = postItems;
    }

    @NonNull
    @Override
    public PostsRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        this.context = parent.getContext();

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_posts, parent, false);
        return new PostsRecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsRecyclerViewHolder holder, int position)
    {
        PostsItem item = postItems.get(position);

        if (!Extensions.StrNullOrEmpty(item.getImageContent()))
        {
            Uri src = Extensions.getContentUri(this.context, item.getImageContent());

            holder.postImage.layout(0,0,0,0);

            Glide.with(holder.postImage.getContext())
                    .load(src)
                    .override(200,200)
                    .centerCrop()
                    .into(holder.postImage);

            holder.postImage.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.postOwnerName.setText(item.getOwnerName());
        holder.postContent.setText(Html.fromHtml(item.getTextContent()));
        holder.postDate.setText(Timestamps.format(item.getDate(), Timestamps.FORMAT_COMPLETE_SHORT));

//        holder.itemView.setOnClickListener(view -> {
//            if (adapterItemClick != null)
//                adapterItemClick.onClick(holder.getAdapterPosition());
//        });
//
//        holder.deleteButton.setOnClickListener(view -> {
//            if (adapterItemDeleteClick != null)
//                adapterItemDeleteClick.onClick(holder.getAdapterPosition());
//        });
    }

    @Override
    public int getItemCount()
    {
        return postItems.size();
    }

//    public void setAdapterItemClick(OnAdapterItemClick listener)
//    {
//        adapterItemClick = listener;
//    }
//
//    public void setAdapterItemDeleteClick(OnAdapterItemDeleteClick listener)
//    {
//        adapterItemDeleteClick = listener;
//    }
}
