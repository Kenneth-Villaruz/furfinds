package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.util.AttributeSet;

/**
 * This extends the default textview and adds a scrolling effect in it
 */
public class MarqueeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public MarqueeTextView(Context context) {
        super(context);
        setSelected(true);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSelected(true);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSelected(true);
    }
}