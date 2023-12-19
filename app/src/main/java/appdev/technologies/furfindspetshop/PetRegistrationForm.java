package appdev.technologies.furfindspetshop;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.DatabaseHelper;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetEditFormFail;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetEditFormSuccess;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetRegistrationFormFail;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetRegistrationFormHidden;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetRegistrationFormShown;
import appdev.technologies.furfindspetshop.eventhandlers.OnPetRegistrationFormSuccess;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Timestamps;
import appdev.technologies.furfindspetshop.helpers.UserAuth;
import appdev.technologies.furfindspetshop.helpers.ValidationContainer;
import appdev.technologies.furfindspetshop.popups.Modal;
import appdev.technologies.furfindspetshop.recyclerviews.MyPetsItem;

public class PetRegistrationForm
{
    //
    // Behaviours
    //
    public enum FORM_MODES
    {
        REGISTER,
        EDIT
    }

    private FORM_MODES formMode;

    private Activity activity;
    private FragmentActivity fragmentActivity;
    private Animation pulseAnimation;
    private DatabaseHelper dbHelper;

    private boolean fragment1Ready;
    private boolean fragment2Ready;
    private boolean fragment3Ready;
    //
    // Data & Contents
    //
    private ContentValues formDataset;
    private ContentValues prefillDataset;
    private String validationFailMessage;
    private String currentTimestamp;
    private UserAuth userAuth;
    //
    // Root
    //
    private LinearLayout overlayContainer;
    private Modal alert;
    //
    // Viewpager Fragments
    //
    private ViewPager2 viewPager2;

    private PetRegistrationFragment1 fragment1;
    private PetRegistrationFragment2 fragment2;
    private PetRegistrationFragment3 fragment3;

    //
    // Control Buttons
    //
    private Button nextButton;
    private Button cancelButton;

    //
    // Events
    //
    private OnPetRegistrationFormShown onShow;
    private OnPetRegistrationFormHidden onHide;
    private OnPetRegistrationFormSuccess onRegisterSuccess;
    private OnPetRegistrationFormFail onRegisterFail;

    private OnPetEditFormSuccess onEditSuccess;
    private OnPetEditFormFail onEditFail;

    private View.OnClickListener nextButton_Click;
    private ViewPager2.OnPageChangeCallback viewPagerFrameChange;

    public PetRegistrationForm(Activity activity, FragmentActivity fragmentActivity, UserAuth userAuth, DatabaseHelper dbHelper)
    {
        this.activity = activity;                                                   // Store a reference to the Activity that uses this class
        this.fragmentActivity = fragmentActivity;                                   // The Activity that hosts (contains) the fragment
        this.userAuth = userAuth;                                                   // Get the information of currently logged-on user
        this.dbHelper = dbHelper;                                                   // Get the db helper instance from host activity

        formDataset = new ContentValues();                                          // The form dataset will be used to store the data
                                                                                    // that will be used for INSERT and UPDATE
        currentTimestamp = Timestamps.currentTimestamp();                           // Get the current date and time in 'Y-m-d h:i a' format
    }

    public void initializeViews()
    {
        validationFailMessage = activity.getString(R.string.double_check_entries);  // Message to show when entries are incomplete or empty
        pulseAnimation = AnimationUtils.loadAnimation(activity, R.anim.pulse);      // The animation to play, when a field needs to be emphasized
        overlayContainer = activity.findViewById(R.id.pet_registration_overlay);    // The root layout in which the registration form is contained

        fragment1 = new PetRegistrationFragment1();                                 // Instantiate the fragment objects
        fragment2 = new PetRegistrationFragment2();
        fragment3 = new PetRegistrationFragment3();

        viewPager2 = activity.findViewById(R.id.pet_registration_view_pager);       // The view that handles the scrolling of fragments
        viewPager2.setUserInputEnabled(false);                                      // Prevent the user from swiping the view pager
        viewPager2.setAdapter(new FragmentStateAdapter(this.fragmentActivity)       // Put the fragments into the view pager
        {
            @NonNull
            @Override
            public Fragment createFragment(int position) {                          // Display the Fragment when scrolled, given by its position
                switch (position)                                                   // Fragment frame index is zero-based; 1st frame is index 0
                {
                    case 0:
                        return fragment1;                                           // When position == 0, display the 1st fragment
                    case 1:
                        return fragment2;                                           // When position == 1, display the 2nd fragment
                    case 2:
                        return fragment3;                                           // When position == 2, display the 3rd fragment
                    default:
                        return null;                                                // When Null, no fragment will be shown
                }
            }

            @Override
            public int getItemCount() {                                             // We have a total of 3 fragments
                return 3;
            }
        });

        nextButton = activity.findViewById(R.id.btn_next);                          // Store a reference of the Next button
        cancelButton = activity.findViewById(R.id.btn_cancel);                      // Store a reference of the Cancel button
    }
    //
    //------------------------------------
    // BEGIN: Form Appearance
    //------------------------------------
    //
    public void show()
    {
        overlayContainer.setVisibility(View.VISIBLE);                               // Show this registration form

        if (onShow != null)                                                         // Then fire an event when this form is shown
            onShow.shown();
    }

    public void hide()
    {
        overlayContainer.setVisibility(View.INVISIBLE);                             // Hide this registration form

        if (onHide != null)                                                         // Then fire an event when this form is hidden
            onHide.hidden();
    }

    public boolean isVisible() {
        return overlayContainer.getVisibility() == View.VISIBLE;
    }
    public void setOnFormShow(OnPetRegistrationFormShown listener) {               // Allow other classes to access the event listener
        this.onShow = listener;
    }
    public void setOnFormHide(OnPetRegistrationFormHidden listener) {             // Allow other classes to access the event listener
        this.onHide = listener;
    }
    //.....................................
    // END: Form Appearance
    //.....................................

    //
    //------------------------------------
    // BEGIN: Form Behaviours
    //------------------------------------
    //
    public void setUserAuth(UserAuth userAuth) { this.userAuth = userAuth; }
    public void setFormMode(FORM_MODES mode) {                                      // Allow other classes to set the form mode
        formMode = mode;
    }
    public void setModalReference(Modal modal) {
        alert = modal;
    }

    public void setOnRegisterSuccess(OnPetRegistrationFormSuccess listener) {
        onRegisterSuccess = listener;
    }

    public void setOnRegisterFail(OnPetRegistrationFormFail listener) {
        onRegisterFail = listener;
    }

    public void setOnEditSuccess(OnPetEditFormSuccess listener) {
        onEditSuccess = listener;
    }

    public void setOnEditFail(OnPetEditFormFail listener) {
        onEditFail = listener;
    }

    public void bindEventHandlers()
    {
        fragment1.setOnFragmentReady(() ->
        {
            fragment1Ready = true;
            nextButton.setEnabled(true);

            if (formMode == FORM_MODES.EDIT)
                fragment1.prefillEntries(prefillDataset);
        });

        fragment2.setOnFragmentReady(() ->
        {
            fragment2Ready = true;

            if (formMode == FORM_MODES.EDIT)
                fragment2.prefillEntries(prefillDataset);
        });

        fragment3.setOnFragmentReady(() ->
        {
            fragment3Ready = true;

            if (formMode == FORM_MODES.EDIT)
                fragment3.prefillEntries(prefillDataset);
        });

        nextButton_Click = view ->                                                  // This is the click event of the Next button
        {
            int currentFrameIndex = viewPager2.getCurrentItem();                    // Get the current position (index) of the fragment shown

            if (!validateFragments(currentFrameIndex))                              // If at least one of the inputs from each fragment is empty,
                return;                                                             // do not allow the user to scroll to the next fragment

            if (currentFrameIndex < 2)                                              // Scroll the next fragment up to the 3rd (last) fragment
                viewPager2.setCurrentItem(currentFrameIndex + 1);

            else if (currentFrameIndex == 2)                                        // When the last fragment is reached, handle the Submit logic
                handleFormSubmit();
        };
        nextButton.setOnClickListener(nextButton_Click);                            // Handle the click event of the 'Next' button
        cancelButton.setOnClickListener(view -> handleFormCancellation());          // Handle the click events of the 'Cancel' button

        viewPagerFrameChange = new ViewPager2.OnPageChangeCallback()
        {
            @Override
            public void onPageSelected(int position)
            {
                // When the last fragment was reached, change button text
                // according to its specific action
                if (position == 2)
                {
                    String buttonText = activity.getString(R.string.button_text_register);

                    if (formMode == FORM_MODES.EDIT)
                        buttonText =  activity.getString(R.string.button_text_update);

                    nextButton.setText(buttonText);
                }
            }
        };

        viewPager2.registerOnPageChangeCallback(viewPagerFrameChange);
    }

    public void handleFormSubmit()
    {
        formDataset.put(PetsModel.COLUMN_OWNER_FK,     String.valueOf(userAuth.getId()));
        formDataset.put(PetsModel.COLUMN_STATUS,       PetsModel.STATUS_AVAILABLE);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try
        {
            // Disable the submit button to prevent double click on submit
            nextButton.setEnabled(false);

            // Start the transaction
            db.beginTransaction();

            if (formMode == FORM_MODES.REGISTER)
            {
                // Save the pet details into database and retrieve its ID
                int insertRowId = submitRegister(db, formDataset);

                if (insertRowId > -1 && onRegisterSuccess != null)
                {
                    // Create an object of MyPetsItem that will be inserted into the recycler view
                    MyPetsItem item = new MyPetsItem();

                    item.setName(formDataset.getAsString(PetsModel.COLUMN_NAME));
                    item.setCategory(formDataset.getAsString(PetsModel.COLUMN_CATEGORY));
                    item.setImage(formDataset.getAsString(PetsModel.COLUMN_IMAGE));
                    item.setDescription(formDataset.getAsString(PetsModel.COLUMN_DESCRIPTION));
                    item.setId(insertRowId);

                    onRegisterSuccess.registerSuccess(insertRowId, item);
                }
                else
                {
                    if (onRegisterFail != null)
                        onRegisterFail.registerFailed();
                }
            }
            else if (formMode == FORM_MODES.EDIT)
            {
                submitUpdate(db, formDataset);
            }

            // If both CRUD and FILE IO operations are successful, mark the transaction as successful
            db.setTransactionSuccessful();
        }
        catch (Exception ex)
        {
            Log.w("logger", "Failed to save data -> " + ex.getMessage());
            alert.show(activity.getString(R.string.err_action_failed), "Failure");
        }
        finally
        {
            // End the transaction. If setTransactionSuccessful() was not called, this will rollback.
            db.endTransaction();
            resetForm();
            nextButton.setEnabled(true);
        }
    }

    private int submitRegister(SQLiteDatabase db, ContentValues dataset) throws IOException
    {
        dataset.put(PetsModel.COLUMN_DATE_CREATED, currentTimestamp);
        dataset.put(PetsModel.COLUMN_DATE_UPDATED, currentTimestamp);

        // Save the data into database, then get the id of the newly added record
        long rowId      = db.insert(PetsModel.TABLE_NAME, null, dataset);
        int lastRowId   = (int) rowId;

        // Save the photo to file
        writeBitmapToPhoto(fragment3.getImageUri(), dataset.getAsString(PetsModel.COLUMN_IMAGE));

        return lastRowId;
    }

    private void submitUpdate(SQLiteDatabase db, ContentValues dataset) throws IOException
    {
        dataset.put(PetsModel.COLUMN_DATE_UPDATED, currentTimestamp);

        // Check if the image has been updated by comparing the filenames
        String originalImageFilename = Uri.parse(prefillDataset.getAsString(PetsModel.COLUMN_IMAGE)).getLastPathSegment();
        String newerImageFilename = dataset.getAsString(PetsModel.COLUMN_IMAGE);
        Uri newerImageURI = Uri.parse(dataset.getAsString("imageURI"));
        dataset.remove("imageURI");

        Log.w("logger", "Prefill Image -> " + originalImageFilename);
        Log.w("logger", "Dataset Image -> " + newerImageFilename);
        Log.w("logger", "Are they same -> " + (newerImageFilename.equals(originalImageFilename)));
        Log.w("logger", "image URI -> " + newerImageURI.toString());
        Log.w("logger", "Images Path -> " + Extensions.getDataFilesPath(activity));

        // If the original image is not the same as newer image,
        // update the image
        if (!originalImageFilename.equals(newerImageFilename))
        {
            // Save the new image
            writeBitmapToPhoto(fragment3.getImageUri(), dataset.getAsString(PetsModel.COLUMN_IMAGE));

            // Delete the old image
            Extensions.deleteFile(activity, originalImageFilename, null);
        }

        int affectedRows = db.update(PetsModel.TABLE_NAME, dataset, DBSchema.COLUMN_ID + " = ?",
                new String[] {prefillDataset.getAsString(DBSchema.COLUMN_ID)}
        );

        if (affectedRows > 0 && onEditSuccess != null)
            onEditSuccess.editSuccess();
        else
        {
            if (onEditFail != null)
                onEditFail.editFailed();
        }
    }

    public void handleFormCancellation()                                            // Show an alert dialog to let the user decide
    {                                                                               // whether he wants to cancel the registration
        String message;
        String title;

        switch (formMode)                                                           // Set different alert message depending on current form mode
        {
            case EDIT:                                                              // Alert message when form mode is in EDIT mode
                message = activity.getString(R.string.cancel_pet_edit);
                title = "Pet Details";
                break;

            case REGISTER:                                                          // Alert message when form mode is in REGISTER mode
            default:                                                                // This is also the default alert message
                message = activity.getString(R.string.cancel_pet_registration);
                title = "Pet Registration";
                break;
        }

        alert.prompt(message, title, this::resetForm, null);    // Show the alert dialog
    }

    private void resetForm()                                                        // Clear the inputs for all fragments
    {
        formDataset.clear();

        if (fragment1Ready)
        {
            if (formMode == FORM_MODES.EDIT)
                fragment1.prefillEntries(prefillDataset);

            else if (formMode == FORM_MODES.REGISTER)
                fragment1.clearInputs();
        }

        if (fragment2Ready)
        {
            if (formMode == FORM_MODES.EDIT)
                fragment2.prefillEntries(prefillDataset);

            else if (formMode == FORM_MODES.REGISTER)
                fragment2.clearInputs();
        }

        if (fragment3Ready)
        {
            if (formMode == FORM_MODES.EDIT)
                fragment3.prefillEntries(prefillDataset);

            else if (formMode == FORM_MODES.REGISTER)
                fragment3.clear();
        }

        viewPager2.setCurrentItem(0);                                               // Scroll (go back) to the first frame of fragment

        nextButton.setText(activity.getString(R.string.button_text_next));          // Reset Next button text to default
        hide();                                                                     // Hide this registration form
    }

    private void writeBitmapToPhoto(Uri imageUri, String newFilename) throws IOException
    {
        // Process the temporary bitmap
        Bitmap bitmap       = Extensions.readBitmapFromURI(activity, imageUri);
        Bitmap imageBitmap  = Extensions.cropSquareBitmap(bitmap);

        // Save the image to "Android/data/com.appdev.technologies.furfindspetshop/files/Pictures" folder
        File outputPath         = Extensions.getDataFilesPath(activity);
        File imageFile          = new File(outputPath, newFilename);
        FileOutputStream stream = new FileOutputStream(imageFile);

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

        // Delete the temp files
        Extensions.truncateTempPhotoDirectory(activity, null);

        stream.close();
    }

    public void detachViewPagerFrameChanged() {
        viewPager2.unregisterOnPageChangeCallback(viewPagerFrameChange);
    }

    public void setFragmentPrefillData(ContentValues prefillValues)
    {
        if (formMode != FORM_MODES.EDIT)
            return;

        prefillDataset = prefillValues;
    }
    //.....................................
    // END: Form Behaviours
    //.....................................

    private boolean validateFragments(int fragmentIndex)
    {
        switch (fragmentIndex)
        {
            case 0:
                return validateFragment1();
            case 1:
                return validateFragment2();
            case 2:
                return validateFragment3();
            default:
                return true;
        }
    }

    private boolean validateFragment1()
    {                                                                               // Get the entry values after validation.
        List<ValidationContainer> fields = fragment1.getValidatedFields();          // This also contains the View data

        if (fields.size() == 1 && fields.get(0).getValue() == null)
        {
            showValidationFailedMessage(fields.get(0).getView());
            return false;
        }

        // We assume that the validation was successful, and we can now
        // safely use each input data
        for (ValidationContainer inputs : fields)
        {
            formDataset.put(inputs.getKey(), inputs.getValue());
        }

        return true;
    }

    private boolean validateFragment2()
    {
        List<ValidationContainer> fields = fragment2.getValidatedFields();

        if (fields.size() == 1 && fields.get(0).getValue() == null)
        {
            showValidationFailedMessage(fields.get(0).getView());
            return false;
        }

        // We assume that the validation was successful, and we can now
        // safely use each input data
        for (ValidationContainer inputs : fields)
        {
            formDataset.put(inputs.getKey(), inputs.getValue());
        }

        return true;
    }

    private boolean validateFragment3()
    {
        Uri image = fragment3.getImageUri();

        if (Extensions.isUriEmpty(image) || !Extensions.isValidBitmap(image, activity.getContentResolver()))
        {
            showValidationFailedMessage(fragment3.getTakePhotoButton());
            return false;
        }

        Log.w("logger", "validate img -> " + image);
        formDataset.put(PetsModel.COLUMN_IMAGE, image.getLastPathSegment());

        if (formMode == FORM_MODES.EDIT)
            formDataset.put("imageURI", image.toString());

        return true;
    }

    private void showValidationFailedMessage(View view)
    {
        // Show an alert message that tells the user to fill out all entries.
        // After clicking on "OK", focus on the input field then animate it

        alert.show(validationFailMessage, "Hang On!", () -> {
            view.requestFocus();
            view.startAnimation(pulseAnimation);
        });
    }
}