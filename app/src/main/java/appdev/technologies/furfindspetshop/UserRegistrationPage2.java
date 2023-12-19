package appdev.technologies.furfindspetshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;

public class UserRegistrationPage2 extends Fragment
{
    private EditText inputUsername;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirm;

    private TextView passwordWarnLabel;

    private String msgNotMatched;
    private String msgInvalidPassword;

    public enum PASSWORD_LABEL_WARN_MODES
    {
        NONE,
        NOT_MATCHED,
        CRITERIA
    }

    private OnViewPagerFragmentCreated onFragmentReady;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_fragment_registration_page2, container, false);

        inputUsername = view.findViewById(R.id.input_username);
        inputEmail = view.findViewById(R.id.input_email);
        inputPassword = view.findViewById(R.id.input_password);
        inputConfirm = view.findViewById(R.id.input_retype_password);
        passwordWarnLabel = view.findViewById(R.id.warn_label_password);

        msgNotMatched = getString(R.string.password_match_error);
        msgInvalidPassword = getString(R.string.password_criteria_error);

        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }

    public EditText getInputUsername() {
        return inputUsername;
    }

    public EditText getInputEmail() {
        return inputEmail;
    }

    public EditText getInputPassword() {
        return inputPassword;
    }

    public EditText getInputConfirmPassword() {
        return inputConfirm;
    }

    public TextView getPasswordWarnLabel() {
        return passwordWarnLabel;
    }

    public void showPasswordWarnLabel(boolean show, PASSWORD_LABEL_WARN_MODES warnMode)
    {
        if (warnMode == PASSWORD_LABEL_WARN_MODES.NOT_MATCHED)
            passwordWarnLabel.setText(msgNotMatched);
        else if (warnMode == PASSWORD_LABEL_WARN_MODES.CRITERIA)
            passwordWarnLabel.setText(msgInvalidPassword);

        if (show)
            passwordWarnLabel.setVisibility(View.VISIBLE);
        else
            passwordWarnLabel.setVisibility(View.INVISIBLE);
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }
}
