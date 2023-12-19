package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import appdev.technologies.furfindspetshop.R;

public class ImageLabel extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public ImageLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(getContext(), R.layout.view_image_label, this);

        imageView = findViewById(R.id.image_label_imageview);
        textView  = findViewById(R.id.image_label_textview);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageLabel, 0, 0);

        try
        {
            // Image Content
            int src = attributes.getResourceId(R.styleable.ImageLabel_imageSrc, 0);

            if (src != 0)
                imageView.setImageResource(src);

            // Image Sizing
            int width = attributes.getDimensionPixelSize(R.styleable.ImageLabel_imageWidth, LayoutParams.WRAP_CONTENT);
            int height = attributes.getDimensionPixelSize(R.styleable.ImageLabel_imageHeight, LayoutParams.WRAP_CONTENT);

            LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams(); //new LayoutParams(width, height);
            layoutParams.width = width;
            layoutParams.height = height;
            imageView.setLayoutParams(layoutParams);

            // Text
            String label = attributes.getString(R.styleable.ImageLabel_labelText);
            textView.setText(label);
        }
        finally
        {
            attributes.recycle();
        }

//        setOnClickListener(new OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                // Handle click event
//            }
//        });
    }
}
