package appdev.technologies.furfindspetshop;

import android.content.Intent;
import android.widget.Button;

import appdev.technologies.furfindspetshop.helpers.AuthGuard;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Landing extends BaseActivity
{
    private Button registerButton   = null;
    private Intent registrationPage = null;

    private Button loginButton      = null;
    private Intent loginPage        = null;

    @Override
    public void onAwake()
    {
        // Do not require login here.
        // But check if the user has saved login data.
        // If so, send him to home page
        this.requireAuth(false);
        AuthGuard.shouldSkipAuth(this);
    }
    @Override
    protected void onBackKeyPressed() {}

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_landing);

        registerButton = findViewById(R.id.btn_register);
        loginButton    = findViewById(R.id.btn_login);
    }

    @Override
    public void bindEvents()
    {
        registerButton.setOnClickListener(view ->
        {
            if (registrationPage == null)
                registrationPage = new Intent(getApplicationContext(), UserRegistrationPage.class);

            startActivity(registrationPage);
            finish();
        });

        loginButton.setOnClickListener(view -> goToLoginPage());
    }

    private void goToLoginPage()
    {
        if (loginPage == null)
            loginPage = new Intent(this, Login.class);

        switchActivity(loginPage);
    }
}