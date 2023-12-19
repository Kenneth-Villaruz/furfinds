package appdev.technologies.furfindspetshop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import appdev.technologies.furfindspetshop.database.DatabaseHelper;
import appdev.technologies.furfindspetshop.database.QueryBuilder;
import appdev.technologies.furfindspetshop.database.UsersHelper;
import appdev.technologies.furfindspetshop.database.UsersModel;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Notifier;
import appdev.technologies.furfindspetshop.helpers.Timestamps;
import appdev.technologies.furfindspetshop.popups.Modal;

public class UserRegistrationPage extends BaseActivity
{
    private UsersHelper usersHelper;

    private UserRegistrationPage1 userRegistrationPage1;
    private UserRegistrationPage2 userRegistrationPage2;
    private UserRegistrationPage3 userRegistrationPage3;

    private ViewPager2 viewPager2;
    private ViewPager2.OnPageChangeCallback viewPagerFrameChange;

    private Button nextButton;
    private Button cancelButton;
    private Button btnLogin;
    private Button btnResend;

    private List<Fragment> viewFragments;
    private LinkedHashMap<String, EditText> inputsPage1;
    private LinkedHashMap<String, EditText> inputsPage2;

    private EditText inputRetypePassword;
    private Modal alert;
    private Notifier notifier;

    private int viewPagerLastFrame;

    private final String inputkey_fname = "Firstname";
    private final String inputkey_mname = "Middlename";
    private final String inputkey_lname = "Lastname";
    private final String inputkey_phone = "Contact Number";
    private final String inputkey_address = "Address";
    private final String inputkey_uname = "Username";
    private final String inputkey_email = "Email";
    private final String inputkey_passw = "Password";

    private DatabaseHelper dbHelper;

    @Override
    public void onAwake()
    {
        this.requireAuth(false);

        dbHelper = DatabaseHelper.getInstance(this);

        userRegistrationPage1 = new UserRegistrationPage1();
        userRegistrationPage2 = new UserRegistrationPage2();
        userRegistrationPage3 = new UserRegistrationPage3();

        viewFragments = new ArrayList<>();

        viewFragments.add(userRegistrationPage1);
        viewFragments.add(userRegistrationPage2);
        viewFragments.add(userRegistrationPage3);

        viewPagerLastFrame = viewFragments.size() - 1;

        inputsPage1 = new LinkedHashMap<>();
        inputsPage2 = new LinkedHashMap<>();

        usersHelper = new UsersHelper(this);

        notifier = new Notifier(this);
    }

    @Override
    public void initializeViews()
    {
        setContentView(R.layout.activity_user_registration_page);

        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return viewFragments.get(position);
            }

            @Override
            public int getItemCount() {
                return viewFragments.size();
            }
        });

        nextButton = findViewById(R.id.btn_next);
        cancelButton = findViewById(R.id.btn_cancel);
        btnLogin = findViewById(R.id.btn_login);

        alert = new Modal(this);
    }

    @Override
    public void bindEvents()
    {
        userRegistrationPage1.setOnFragmentReady(() ->
        {
            inputsPage1.put(inputkey_fname, userRegistrationPage1.getInputFname());
            inputsPage1.put(inputkey_mname, userRegistrationPage1.getInputMname());
            inputsPage1.put(inputkey_lname, userRegistrationPage1.getInputLname());
            inputsPage1.put(inputkey_phone, userRegistrationPage1.getInputContact());
            inputsPage1.put(inputkey_address, userRegistrationPage1.getInputAddress());
        });

        userRegistrationPage2.setOnFragmentReady(() ->
        {
            inputsPage2.put(inputkey_uname, userRegistrationPage2.getInputUsername());
            inputsPage2.put(inputkey_email, userRegistrationPage2.getInputEmail());
            inputsPage2.put(inputkey_passw, userRegistrationPage2.getInputPassword());

            inputRetypePassword = userRegistrationPage2.getInputConfirmPassword();
        });

        userRegistrationPage3.setOnFragmentReady(() ->
        {
            btnResend = userRegistrationPage3.getBtnResend();
            btnResend.setOnClickListener(view ->
            {
                String username_input = inputsPage2.get(inputkey_uname).getText().toString();
                resendVerification(username_input);
            });
        });

        nextButton.setOnClickListener(view ->
        {
            int currentItem = viewPager2.getCurrentItem();

            if (!validatePages(currentItem))
                return;

            if (currentItem < viewFragments.size() - 1)
            {
                viewPager2.setCurrentItem(currentItem + 1);
            }
            //else
            //{
            //    viewPager2.setCurrentItem(0); -> go back first page
            //}
        });

        cancelButton.setOnClickListener(view -> handleCancel());

        // Track the viewpager if it's page frames were scrolled (changed)
        //
        viewPagerFrameChange = new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                if (position == viewPagerLastFrame)
                {
                    nextButton.setText(getString(R.string.button_text_verify));
                    nextButton.setOnClickListener(view ->
                    {
                        String username_input = inputsPage2.get(inputkey_uname).getText().toString();
                        String verify_code_input = userRegistrationPage3.getInputCodesValue();

                        handleVerification(username_input, verify_code_input);
                    });
                }
            }
        };

        viewPager2.registerOnPageChangeCallback(viewPagerFrameChange);

        btnLogin.setOnClickListener(view ->
        {
            String message = getString(R.string.user_registration_switch_login);
            alert.prompt(message, "Heads up!", this::goToLogin, null);
        });

        notifier.setOnNotifierSent(() -> {
            if (btnResend != null)
                btnResend.setEnabled(true);
        });
    }

    @Override
    protected void onBackKeyPressed() {
        handleCancel();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        viewPager2.unregisterOnPageChangeCallback(viewPagerFrameChange);
    }

    private boolean validatePages(int page)
    {
        switch (page)
        {
            case 0:
                return validatePage1();
            case 1:
                return validatePage2();
            default:
                return true;
        }
    }

    private boolean validatePage1()
    {
        for (Map.Entry<String, EditText> inputs : inputsPage1.entrySet())
        {
            EditText input = inputs.getValue();

            if (TextUtils.isEmpty(input.getText().toString()))
            {
                input.requestFocus();
                alert.show("Please fill out the '" + inputs.getKey() + "' field.", "Hang on!");
                return false;
            }
        }

        return true;
    }

    private boolean validatePage2()
    {
        for (Map.Entry<String, EditText> inputs : inputsPage2.entrySet())
        {
            EditText input = inputs.getValue();

            if (TextUtils.isEmpty(input.getText().toString()))
            {
                input.requestFocus();
                alert.show("Please fill out the '" + inputs.getKey() + "' field.", "Hang on!");
                return false;
            }

            // Validate username to make sure it is not yet taken by someone else
            if (inputs.getKey().equals(inputkey_uname))
            {
                if (usersHelper.identityExists(input.getText().toString(), UsersModel.COLUMN_USERNAME))
                {
                    input.requestFocus();
                    alert.show("Username is taken. Please try another.", "Hang on!");
                    return false;
                }
            }
            // Validate email to make sure it is not yet taken by someone else
            else if (inputs.getKey().equals(inputkey_email))
            {
                if (usersHelper.identityExists(input.getText().toString(), UsersModel.COLUMN_EMAIL))
                {
                    input.requestFocus();
                    alert.show("Email is already in use. Please try another.", "Hang on!");
                    return false;
                }
            }
        }

        String password = Objects.requireNonNull(inputsPage2.get(inputkey_passw)).getText().toString();

        // Password confirmation
        if (!(inputRetypePassword.getText().toString().equals( password )))
        {
            String msg = getString(R.string.password_didnt_matched);
            alert.show(msg, "Hang on!");

            userRegistrationPage2.showPasswordWarnLabel(true, UserRegistrationPage2.PASSWORD_LABEL_WARN_MODES.NOT_MATCHED);
            return false;
        }

        // Password criteria
        else if (!Extensions.isValidPassword(password))
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Your password must meet the following requirements:\n\n");
            sb.append("\u2022 no extra spaces\n");
            sb.append("\u2022 at least 8 characters long\n");
            sb.append("\u2022 contains numbers and letters\n");
            sb.append("\u2022 contains at least one special character from @#$%^&+=*");

            alert.show(sb.toString(), "Invalid password", Extensions.TEXT_ALIGN_LEFT);

            userRegistrationPage2.showPasswordWarnLabel(true, UserRegistrationPage2.PASSWORD_LABEL_WARN_MODES.CRITERIA);
            return false;
        }

        // Password all good
        else
        {
            userRegistrationPage2.showPasswordWarnLabel(false, UserRegistrationPage2.PASSWORD_LABEL_WARN_MODES.NONE);
        }

        // Pre-register the user so that he can receive a verification code
        boolean register = preRegisterUser();

        if (!register) {
            String message = getString(R.string.user_registration_failed);
            alert.show(message, "Registration Failed");
            return false;
        }

        return true;
    }

    private void handleCancel()
    {
        String message = getString(R.string.user_registration_warning);
        alert.prompt(message, "Heads up!", this::goBackToLanding, null);
    }

    private void goBackToLanding()
    {
        Intent landingPage = new Intent(this, Landing.class);
        switchActivity(landingPage);
    }

    private void goToLogin()
    {
        Intent login = new Intent(this, Login.class);
        switchActivity(login);
    }

    private boolean preRegisterUser()
    {
        String username_input = inputsPage2.get(inputkey_uname).getText().toString();

        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put(UsersModel.COLUMN_FIRSTNAME, inputsPage1.get(inputkey_fname).getText().toString());
        data.put(UsersModel.COLUMN_MIDDLENAME, inputsPage1.get(inputkey_mname).getText().toString());
        data.put(UsersModel.COLUMN_LASTNAME, inputsPage1.get(inputkey_lname).getText().toString());
        data.put(UsersModel.COLUMN_CONTACT, inputsPage1.get(inputkey_phone).getText().toString());
        data.put(UsersModel.COLUMN_ADDRESS, inputsPage1.get(inputkey_address).getText().toString());

        data.put(UsersModel.COLUMN_USERNAME, username_input);
        data.put(UsersModel.COLUMN_EMAIL, inputsPage2.get(inputkey_email).getText().toString());
        data.put(UsersModel.COLUMN_PASSWORD, inputsPage2.get(inputkey_passw).getText().toString());

        String code = Extensions.randomNumber(4);
        data.put(UsersModel.COLUMN_VERIFY_CODE, code);

        boolean registerSuccess = usersHelper.register(data);

        if (registerSuccess)
        {
            persistVerificationCode(username_input, code);
        }

        return registerSuccess;
    }

    private void handleVerification(String username_input, String code_input)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Extensions.PREFS_FILE, Context.MODE_PRIVATE);

        String pref_uname = sharedPreferences.getString(UsersHelper.PREF_KEY_USERNAME, "");
        String verifyCode = username_input.equals(pref_uname) ?
                            sharedPreferences.getString(UsersHelper.PREF_KEY_VERIFY_CODE, "") :
                            usersHelper.readVerificationCode(username_input);

        if (!code_input.equals(verifyCode)) {
            alert.show(getString(R.string.warning_verification_not_match), "Verification Failed!");
            return;
        }

        if (username_input.equals(pref_uname))
        {
            // Set the user as fully verified
            String timestamp = Timestamps.currentTimestamp();
            QueryBuilder queryBuilder = new QueryBuilder();
            String sql = queryBuilder
                    .update(UsersModel.TABLE_NAME)
                    .set(new String[][]{{ UsersModel.COLUMN_VERIFIED_AT, "=", timestamp}, {UsersModel.COLUMN_DATE_UPDATED, "=", timestamp}})
                    .where(new String[][]{{ UsersModel.COLUMN_USERNAME, "=", username_input }})
                    .build();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sql, queryBuilder.getParameterBindings());

            // We must clear the old verification code data of the verified user
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(UsersHelper.PREF_KEY_USERNAME);
            editor.remove(UsersHelper.PREF_KEY_VERIFY_CODE);
            editor.apply();
        }

        // Exit the verification page and redirect the user to the login page
        alert.show(getString(R.string.verification_success), "Verification Complete", () -> {
            Intent login = new Intent(this, Login.class);
            switchActivity(login);
        });
    }

    private void resendVerification(String username_input)
    {
        if (btnResend != null)
            btnResend.setEnabled(false);

        String code = Extensions.randomNumber(4);

        QueryBuilder query = new QueryBuilder();
        String sql = query.update(UsersModel.TABLE_NAME)
                          .set(new String[][]{{ UsersModel.COLUMN_VERIFY_CODE, "=", code}})
                          .build();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sql, query.getParameterBindings());

        persistVerificationCode(username_input, code);
        // String sql = query.select(UsersModel.COLUMN_VERIFY_CODE);
    }

    private void persistVerificationCode(String username_input, String code)
    {
        // Save the verification codes and username for user.
        String verfcode_key = UsersHelper.PREF_KEY_VERIFY_CODE;
        String username_key = UsersHelper.PREF_KEY_USERNAME;

        // Get the shared preferences
        SharedPreferences settings = getSharedPreferences(Extensions.PREFS_FILE, Context.MODE_PRIVATE);

        // Edit the shared preferences
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(verfcode_key, code);
        editor.putString(username_key, username_input);

        // Commit the edits
        editor.commit();

        // Notify the user about his verification code
        String message = "Your verification code is: " + code;
        notifier.notify(message, notifier.NOTIF_ID_SPECIAL);
    }
}