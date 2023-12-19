package appdev.technologies.furfindspetshop;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import appdev.technologies.furfindspetshop.eventhandlers.OnTextChanged;
import appdev.technologies.furfindspetshop.popups.Modal;

public class UserVerification extends BaseActivity
{
    private EditText inputCode1 = null;
    private EditText inputCode2 = null;
    private EditText inputCode3 = null;
    private EditText inputCode4 = null;

    private Button btnVerify = null;
    private Button btnCancel = null;

    private Modal  alert = null;
    private Intent landingPage = null;

    @Override
    public void onAwake() {
        alert = new Modal(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_user_verification);

        inputCode1 = findViewById(R.id.input_code1);
        inputCode2 = findViewById(R.id.input_code2);
        inputCode3 = findViewById(R.id.input_code3);
        inputCode4 = findViewById(R.id.input_code4);

        btnVerify  = findViewById(R.id.btn_verify);
        btnCancel  = findViewById(R.id.btn_cancel_registration);
    }

    @Override
    public void bindEvents()
    {
        inputCode1.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode1, inputCode2) ));    // focus on inputCode2
        inputCode2.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode2, inputCode3) ));    // focus on inputCode3
        inputCode3.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode3, inputCode4) ));    // focus on inputCode4
        inputCode4.addTextChangedListener(new OnTextChanged( () -> watchTextChanged(inputCode4, btnVerify)  ));    // unlocks the Verify button

        btnVerify.setOnClickListener(view -> {

        });

        btnCancel.setOnClickListener(view -> handleCancel());
    }

    @Override
    protected void onBackKeyPressed() {
        handleCancel();
    }

    //
    // This method tracks for input fields if there values had changed.
    // If their values are empty, the "Verify" button will be disabled.
    // Only unlock the "Verify" button when all input fields have values
    //
    private void watchTextChanged(EditText from, View next)
    {
        // Unlock the Verify button when all fields have values
        boolean allFieldsFilled = !TextUtils.isEmpty(inputCode1.getText())
                && !TextUtils.isEmpty(inputCode2.getText())
                && !TextUtils.isEmpty(inputCode3.getText())
                && !TextUtils.isEmpty(inputCode4.getText());

        btnVerify.setEnabled(allFieldsFilled);

        // After typing, focus to the next input
        if (!TextUtils.isEmpty(from.getText()))
            next.requestFocus();
    }

    private void handleCancel()
    {
        String message = getString(R.string.user_registration_warning);
        alert.prompt(message, "Heads up!", this::goBackToLanding, null);
    }

    private void goBackToLanding()
    {
        if (landingPage == null)
            landingPage = new Intent(this, Landing.class);

        switchActivity(landingPage);
    }
}