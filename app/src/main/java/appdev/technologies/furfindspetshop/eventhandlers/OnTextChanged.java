package appdev.technologies.furfindspetshop.eventhandlers;

import android.text.Editable;
import android.text.TextWatcher;

public class OnTextChanged implements TextWatcher
{
    private final Runnable action;

    public OnTextChanged(Runnable action)
    {
        this.action = action;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // Do nothing
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (action != null)
            action.run();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        // Do nothing
    }
}
