package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import appdev.technologies.furfindspetshop.R;
import appdev.technologies.furfindspetshop.helpers.Extensions;

public class PetItemCard extends CardView
{
    private ImageView itemImageView;
    private ImageView badgeForAdopt;
    private TextView itemNameText;
    private TextView itemPriceText;

    private Context context;

    public PetItemCard(@NonNull Context context) {
        super(context);
        init(context);
    }

    public PetItemCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PetItemCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context)
    {
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_pet_item_card, this);

        itemImageView = view.findViewById(R.id.item_card_image);
        itemNameText = view.findViewById(R.id.item_card_title);
        itemPriceText = view.findViewById(R.id.item_card_price_text);

        badgeForAdopt = view.findViewById(R.id.img_adopt_badge);
    }

    public void setItemNameText(String name) {
        itemNameText.setText(name);
    }

    public void setItemPriceText(String price) {
        itemPriceText.setText(price);
    }

    public void setItemImage(String src)
    {
        Uri imagePath = Extensions.getContentUri(this.context, src);

        itemImageView.layout(0,0,0,0);

        Glide.with(itemImageView.getContext())
                .load(imagePath)
                .placeholder(R.drawable.img_placeholder)
                .override(150,150)
                .centerCrop()
                .into(itemImageView);
    }

    public void setForAdopt(boolean forAdopt)
    {
        if (!forAdopt)
            badgeForAdopt.setVisibility(View.INVISIBLE);
        else
            badgeForAdopt.setVisibility(View.VISIBLE);
    }
}
