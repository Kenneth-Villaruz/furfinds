package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.util.AttributeSet;

/**
 * This will force the image view to match its width and height to produce a perfect squared image
 */
public class SquareImageView extends androidx.appcompat.widget.AppCompatImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // This is the key line. We force the height to be the same as width
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
