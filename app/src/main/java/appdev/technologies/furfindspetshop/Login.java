package appdev.technologies.furfindspetshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.UsersHelper;
import appdev.technologies.furfindspetshop.database.UsersModel;
import appdev.technologies.furfindspetshop.helpers.AuthGuard;
import appdev.technologies.furfindspetshop.helpers.AuthSharedPrefsManager;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.popups.Modal;

public class Login extends BaseActivity
{
    private Button btnLogin = null;
    private Button btnSignup = null;

    private EditText inputEmail = null;
    private EditText inputPassword = null;

    private Modal alert = null;

//    public static final String PREFS_LOGIN_SESSION      = "login_session";
//    public static final String PREFS_SESSION_UNAME      = "sess_uname";
//    public static final String PREFS_SESSION_PASSWORD   = "sess_password";
//    public static final String PREFS_SESSION_USERID     = "sess_userid";

    @Override
    protected void onBackKeyPressed() {}

    @Override
    public void onAwake()
    {
        alert = new Modal(this);

        // Check if the user has saved login data.
        // If so, send him to home page
        AuthGuard.shouldSkipAuth(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_login);

        btnLogin      = findViewById(R.id.btn_login);
        btnSignup     = findViewById(R.id.btn_sign_up);
        inputEmail    = findViewById(R.id.input_email_uname);
        inputPassword = findViewById(R.id.input_password);
    }

    @Override
    public void bindEvents()
    {
        btnLogin.setOnClickListener(view -> handleLogin());
        btnSignup.setOnClickListener(view -> goToRegistration());
    }

    @SuppressLint("Range")
    private void handleLogin()
    {
        String email = String.valueOf(this.inputEmail.getText());
        String password = String.valueOf(inputPassword.getText());

        // Check if username field is empty
        if (TextUtils.isEmpty(email))
        {
            String message = getString(R.string.warning_fillout_email_uname);
            alert.show(message, "Hold on!", () ->
            {
                // set focus on the username input
                inputEmail.requestFocus();
            });
            return;
        }

        // Check if password field is empty
        if (TextUtils.isEmpty(password))
        {
            String message = getString(R.string.warning_fillout_password);
            alert.show(message, "Oops!", () ->
            {
                // set focus on the password input
                inputPassword.requestFocus();
            });
            return;
        }

        UsersHelper controller = new UsersHelper(this);

        Cursor auth = controller.authenticate(email, password);

        if (auth.moveToFirst())
        {
            String auth_email = auth.getString(auth.getColumnIndex(UsersModel.COLUMN_EMAIL));
            String auth_username = auth.getString(auth.getColumnIndex(UsersModel.COLUMN_USERNAME));
            String auth_userid = auth.getString(auth.getColumnIndex(DBSchema.COLUMN_ID));
            String auth_password = auth.getString(auth.getColumnIndex(UsersModel.COLUMN_PASSWORD));

            int userId = Integer.parseInt(auth_userid);

            // After authenticating the user, save his login session
            UserAuth user = new UserAuth(userId, auth_username, auth_email, auth_password);
            AuthSharedPrefsManager.getInstance(getApplicationContext()).saveUser(user);

            // We expect that the login is successful
            goToHome();
            auth.close();
        }
        else
        {
            String message = getString(R.string.login_unsuccessful);
            alert.show(message, "Oops!", () ->
            {
                // set focus on the password input
                inputPassword.requestFocus();
            });
        }
    }

    private void goToHome()
    {
        Intent homePage = new Intent(this, Home.class);
        switchActivity(homePage);
    }

    private void goToRegistration()
    {
        Intent register = new Intent(this, UserRegistrationPage.class);
        switchActivity(register);
    }
}