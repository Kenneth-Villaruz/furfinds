package appdev.technologies.furfindspetshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;

public class UserRegistrationPage1 extends Fragment
{
    private EditText inputFname;
    private EditText inputMname;
    private EditText inputLname;
    private EditText inputContact;
    private EditText inputAddress;

    private OnViewPagerFragmentCreated onFragmentReady;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.view_fragment_registration_page1, container, false);

        inputFname = view.findViewById(R.id.input_firstname);
        inputMname = view.findViewById(R.id.input_middlename);
        inputLname = view.findViewById(R.id.input_lastname);
        inputContact = view.findViewById(R.id.input_contact);
        inputAddress = view.findViewById(R.id.input_address);

        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }

    public EditText getInputFname() {
        return inputFname;
    }

    public EditText getInputMname() {
        return inputMname;
    }

    public EditText getInputLname() {
        return inputLname;
    }

    public EditText getInputContact() {
        return inputContact;
    }

    public EditText getInputAddress() {
        return inputAddress;
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }
}