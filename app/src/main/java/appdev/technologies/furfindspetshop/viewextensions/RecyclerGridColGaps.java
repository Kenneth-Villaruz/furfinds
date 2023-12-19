package appdev.technologies.furfindspetshop.viewextensions;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerGridColGaps extends RecyclerView.ItemDecoration
{
    public enum SpacingModes
    {
        GAP_HORIZONTAL,
        GAP_VERTICAL,
        GAP_ALL
    }

    private int spacing_top = 0;
    private int spacing_end = 0;
    private int spacing_bottom = 0;
    private int spacing_start = 0;

    public RecyclerGridColGaps(int space, SpacingModes spacingModes)
    {
        switch (spacingModes)
        {
            case GAP_HORIZONTAL:
                spacing_start = space;
                spacing_end = space;
                break;

            case GAP_VERTICAL:
                spacing_top = space;
                spacing_bottom = space;
                break;

            case GAP_ALL:
            default:
                spacing_start = space;
                spacing_end = space;
                spacing_top = space;
                spacing_bottom = space;
                break;
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state)
    {
        outRect.left = spacing_start / 2;
        outRect.right = spacing_end / 2;
        outRect.bottom = spacing_bottom;

        // Add top margin only for the first item to avoid double space between items
        // if (parent.getChildLayoutPosition(view) == 0) {

        if (parent.getChildLayoutPosition(view) < 2) {
            outRect.top = spacing_top;
        } else {
            outRect.top = 0;
        }
    }
}