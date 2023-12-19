package appdev.technologies.furfindspetshop;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.ValidationContainer;

public class PetRegistrationFragment1 extends Fragment
{
    private EditText inputPetName;
    private EditText inputAge;

    private Spinner ageUnitsSpinner;
    private ArrayAdapter<String> ageUnitsAdapter;
    private String selectedAgeUnits;

    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;
    private String selectedCategory = "";

    private RadioGroup genderGroup;
    private RadioButton genderMale;
    private RadioButton genderFemale;
    private RadioButton genderNeuter;
    private String selectedGender = "";

    private Context context;
    private OnViewPagerFragmentCreated onFragmentReady;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pet_registration_fragment1, container, false);

        inputPetName = view.findViewById(R.id.input_pet_name);
        inputAge     = view.findViewById(R.id.input_pet_age);

        ageUnitsSpinner = view.findViewById(R.id.age_units_spinner);
        ageUnitsAdapter = Extensions.buildSelectMenu(context, R.array.pet_age_unit_select);
        ageUnitsSpinner.setAdapter(ageUnitsAdapter);

        categorySpinner = view.findViewById(R.id.pet_category_spinner);
        categoryAdapter = Extensions.buildSelectMenu(context, R.array.pet_categories);
        categorySpinner.setAdapter(categoryAdapter);

        genderGroup = view.findViewById(R.id.pet_gender_group);

        genderMale = view.findViewById(R.id.option_gender_male);
        genderFemale = view.findViewById(R.id.option_gender_female);
        genderNeuter = view.findViewById(R.id.option_gender_neuter);

        bindEvents();

        // Event that can be used by activities, when fragment is ready
        //
        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }
    //
    // View Events
    //
    private void bindEvents()
    {
        ageUnitsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedAgeUnits = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        genderGroup.setOnCheckedChangeListener(genderGroupCheckedChangedListener());
    }

    private RadioGroup.OnCheckedChangeListener genderGroupCheckedChangedListener()
    {
        return (group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked)
                selectedGender = checkedRadioButton.getText().toString();
        };
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }
    //
    // Input Value Getters
    //
    public String getPetName() {
        return inputPetName.getText().toString();
    }

    public String getAge() {
        return inputAge.getText().toString();
    }

    public String getAgeUnits()
    {
        if (Extensions.StrNullOrEmpty(selectedAgeUnits))
            return "";

        if (selectedAgeUnits.equals(context.getString(R.string.item_choose)))
            return "";

        return selectedAgeUnits;
    }

    public String getGender()
    {
        if (Extensions.StrNullOrEmpty(selectedGender))
            return "";

        if (!selectedGender.equals(PetsModel.GENDER_MALE) && !selectedGender.equals(PetsModel.GENDER_FEMALE))
            selectedGender = PetsModel.GENDER_NEUTER;

        return selectedGender;
    }

    public String getCategory()
    {
        if (Extensions.StrNullOrEmpty(selectedCategory))
            return "";

        if (selectedCategory.equals(context.getString(R.string.item_choose)))
            return "";

        return selectedCategory;
    }
    //
    // Behaviours
    //
    public void clearInputs()
    {
        inputPetName.setText("");
        inputAge.setText("");

        ageUnitsSpinner.setSelection(0);

        selectedGender = "";
        genderGroup.setOnCheckedChangeListener(null);
        genderGroup.clearCheck();
        genderGroup.setOnCheckedChangeListener(genderGroupCheckedChangedListener());

        selectedCategory = "";
        categorySpinner.setSelection(0);
    }

    public List<ValidationContainer> getValidatedFields()
    {
        List<ValidationContainer> fields = new ArrayList<>();

        // 1st parameter -> Key,   used as Database Table Column / Field;
        // 2nd parameter -> View,  will be used to focus and animate later on
        // 3rd parameter -> Value, which will be saved into the database

        fields.add(new ValidationContainer(PetsModel.COLUMN_NAME,       inputPetName,    getPetName()));
        fields.add(new ValidationContainer(PetsModel.COLUMN_AGE,        inputAge,        getAge()));
        fields.add(new ValidationContainer(PetsModel.COLUMN_AGE_UNITS,  ageUnitsSpinner, getAgeUnits()));
        fields.add(new ValidationContainer(PetsModel.COLUMN_SEX,        genderGroup,     getGender()));
        fields.add(new ValidationContainer(PetsModel.COLUMN_CATEGORY,   categorySpinner, getCategory()));

        for (ValidationContainer v : fields)
        {
            if (Extensions.StrNullOrEmpty(v.getValue()))
            {
                List<ValidationContainer> failedEntry = new ArrayList<>();
                failedEntry.add(new ValidationContainer(v.getKey(), v.getView(), null));

                return failedEntry;
            }
        }

        return fields;
    }

    public void prefillEntries(ContentValues contentValues)
    {
        inputPetName.setText(contentValues.getAsString(PetsModel.COLUMN_NAME));
        inputAge.setText(contentValues.getAsString(PetsModel.COLUMN_AGE));

        int ageUnitIndex = ageUnitsAdapter.getPosition(contentValues.getAsString(PetsModel.COLUMN_AGE_UNITS));
        ageUnitsSpinner.setSelection(ageUnitIndex);

        String gender = contentValues.getAsString(PetsModel.COLUMN_SEX);
        switch (gender)
        {
            case PetsModel.GENDER_MALE:
                genderMale.setChecked(true);
                break;
            case PetsModel.GENDER_FEMALE:
                genderFemale.setChecked(true);
                break;
            case PetsModel.GENDER_NEUTER:
            default:
                genderNeuter.setChecked(true);
                break;
        }

        int categoryIndex = categoryAdapter.getPosition(contentValues.getAsString(PetsModel.COLUMN_CATEGORY));
        categorySpinner.setSelection(categoryIndex);
    }
}