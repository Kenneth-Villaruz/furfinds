package appdev.technologies.furfindspetshop;

import android.content.Intent;
import android.widget.Button;

import appdev.technologies.furfindspetshop.helpers.AuthGuard;

public class GetStarted extends BaseActivity
{
    private Button btnGetStarted = null;
    private Intent landingPage = null;

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
        setContentView(R.layout.activity_get_started);
        btnGetStarted = findViewById(R.id.btn_get_started);
    }

    @Override
    public void bindEvents()
    {
        btnGetStarted.setOnClickListener(view -> {
            if (landingPage == null)
                landingPage = new Intent(this, Landing.class);

            switchActivity(landingPage);
        });
    }
}