package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import appdev.technologies.furfindspetshop.R;

public class ItemCardHorizontal extends RelativeLayout {

    public ItemCardHorizontal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ItemCardHorizontal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        inflate(getContext(), R.layout.view_item_card_horizontal, this);

        ImageView imageView = findViewById(R.id.item_image);
        MarqueeTextView titleText = findViewById(R.id.card_item_title_text);
        MarqueeTextView captionText = findViewById(R.id.card_item_caption_text);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ItemCardHorizontal, 0, 0);

        try {
            // Image Content
            int src = attributes.getResourceId(R.styleable.ItemCardHorizontal_cardImageSrc, 0);

            if (src != 0)
                imageView.setImageResource(src);

            // Image Sizing
            int width = attributes.getDimensionPixelSize(R.styleable.ItemCardHorizontal_cardImageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
            int height = attributes.getDimensionPixelSize(R.styleable.ItemCardHorizontal_cardImageHeight, RelativeLayout.LayoutParams.WRAP_CONTENT);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            imageView.setLayoutParams(layoutParams);

            // Texts
            String title = attributes.getString(R.styleable.ItemCardHorizontal_cardTitleText);
            titleText.setText(title);
            titleText.setSelected(true);

            String caption = attributes.getString(R.styleable.ItemCardHorizontal_cardCaptionText);
            captionText.setText(caption);

        } finally {
            attributes.recycle();
        }

    }
}
