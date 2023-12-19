package appdev.technologies.furfindspetshop.viewextensions;

import android.content.Context;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Objects;

import appdev.technologies.furfindspetshop.R;

public class PasswordField extends androidx.appcompat.widget.AppCompatEditText {

    public PasswordField(Context context) {
        super(context);
        init();
    }

    public PasswordField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PasswordField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Set the input type to password
        setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        // Add the eye drawable to the end of the EditText and your icon to the start
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.icn_show_password, 0);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (getRight() - getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // toggle password visibility
                        if(getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.icn_show_password, 0);
                        } else {
                            setTransformationMethod(PasswordTransformationMethod.getInstance());
                            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_lock_24, 0, R.drawable.icn_hide_password, 0);
                        }
                        // move cursor to end of text
                        setSelection(getText().length());
                        return true;
                    }
                }
                return false;
            }
        });

    }
}
