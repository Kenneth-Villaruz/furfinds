package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ExpandableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static final int MAX_LINES = 5;

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setMaxLines(Integer.MAX_VALUE);
//            }
//        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMaxLines() == MAX_LINES) {
                    setMaxLines(Integer.MAX_VALUE);
                } else {
                    setMaxLines(MAX_LINES);
                }
            }
        });
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        post(new Runnable() {
            public void run() {
                setMaxLines(MAX_LINES);
            }
        });
    }
}