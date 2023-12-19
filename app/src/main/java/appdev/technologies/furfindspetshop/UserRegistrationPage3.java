package appdev.technologies.furfindspetshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import appdev.technologies.furfindspetshop.eventhandlers.OnTextChanged;
import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;

public class UserRegistrationPage3 extends Fragment
{
    private EditText inputCode1 = null;
    private EditText inputCode2 = null;
    private EditText inputCode3 = null;
    private EditText inputCode4 = null;

    private Button btnResend = null;

    private OnViewPagerFragmentCreated onFragmentReady;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_fragment_registration_page3, container, false);

        inputCode1 = view.findViewById(R.id.input_code1);
        inputCode2 = view.findViewById(R.id.input_code2);
        inputCode3 = view.findViewById(R.id.input_code3);
        inputCode4 = view.findViewById(R.id.input_code4);

        inputCode1.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode1, inputCode2) ));    // focus on inputCode2
        inputCode2.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode2, inputCode3) ));    // focus on inputCode3
        inputCode3.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode3, inputCode4) ));    // focus on inputCode4
        inputCode4.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode4, null) ));    // unlocks the Verify button

        btnResend = view.findViewById(R.id.btn_resend);

        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }

    public Button getBtnResend() {
        return btnResend;
    }

    public String getInputCodesValue()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(inputCode1.getText().toString());
        sb.append(inputCode2.getText().toString());
        sb.append(inputCode3.getText().toString());
        sb.append(inputCode4.getText().toString());

        return sb.toString();
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }

    private void watchTextChanged(EditText from, View next)
    {
        // After typing, focus to the next input
        if (!TextUtils.isEmpty(from.getText()) && next != null)
            next.requestFocus();
    }
}