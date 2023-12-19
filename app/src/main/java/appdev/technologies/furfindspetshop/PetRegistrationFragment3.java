package appdev.technologies.furfindspetshop;

import static android.app.Activity.RESULT_OK;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.Timestamps;

public class PetRegistrationFragment3 extends Fragment
{
    private Button btnTakePhoto;
    private ImageView previewHolder;

    private final int REQUEST_IMAGE_CAPTURE = 200;

    // Declare a global variable to store the image URI
    private Uri imageUri;

    private Context context;
    private OnViewPagerFragmentCreated onFragmentReady;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putString("imageUri", imageUri.toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pet_registration_fragment3, container, false);

        btnTakePhoto  = view.findViewById(R.id.btn_take_photo);
        previewHolder = view.findViewById(R.id.preview_holder);
        btnTakePhoto.setOnClickListener(v -> takePicture());

        // Event that can be used by activities, when fragment is ready
        //
        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey("imageUri"))
            imageUri = Uri.parse(savedInstanceState.getString("imageUri"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code and the result code are correct
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            // Set the image URI to the image view
            loadImageFromURI(imageUri);

            // Change button text to hint user to take another
            btnTakePhoto.setText(context.getString(R.string.button_text_retry));
        }
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }

    // Create a method to launch the camera intent with a FileProvider URI
    private void takePicture()
    {
        // Create an intent to capture an image
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if the device has a camera app
        if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            // Create a file to store the image
            File imageFile = createImageFile();

            // Check if the file was created successfully
            if (imageFile != null)
            {
                // Get the URI of the file from the FileProvider
                imageUri = FileProvider.getUriForFile(context,
                        Extensions.getAuthority(context),
                        imageFile);

                // Add the URI as an extra to the intent
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                // Grant read and write permissions to the camera app
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                // Start the camera activity
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // Create a method to create a file with a unique name
    private File createImageFile()
    {
        // Get the external storage directory for pictures
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create a timestamp for the file name
        String timeStamp = Timestamps.currentTimestamp();

        // Create a file with the prefix "JPEG_" and the suffix ".jpg"
        File imageFile = null;
        try
        {
            imageFile = File.createTempFile(
                    "pet_" + timeStamp + "_",
                    ".jpg",
                    storageDir
            );
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.w("logger", e.getMessage());
        }

        // Return the file
        return imageFile;
    }

    public void clear()
    {
        // Reset URI
        imageUri = null;

        // Reset the preview image
        Glide.with(previewHolder.getContext())
                .load(R.drawable.placeholder_camera)
                .into(previewHolder);

        // Reset button text
        btnTakePhoto.setText(context.getString(R.string.button_text_take_photo));
    }

    public void prefillEntries(ContentValues contentValues)
    {
        String image = contentValues.getAsString(PetsModel.COLUMN_IMAGE);

        if (Extensions.StrNullOrEmpty(image))
            return;

        imageUri = Uri.parse(image);
        loadImageFromURI(imageUri);
    }

    public Uri getImageUri() {
        return imageUri;
    }

    private void loadImageFromURI(Uri uri)
    {
        Glide.with(previewHolder.getContext())
                .load(uri)
                .placeholder(R.drawable.placeholder_camera)
                .centerCrop()
                .override(250,250)
                .into(previewHolder);
    }

    public Button getTakePhotoButton() {
        return btnTakePhoto;
    }
//    public void hintOnCameraButton()
//    {
//        Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
//        btnTakePhoto.requestFocus();
//        btnTakePhoto.startAnimation(pulse);
//    }
}