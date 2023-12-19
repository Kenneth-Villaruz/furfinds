package appdev.technologies.furfindspetshop;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import appdev.technologies.furfindspetshop.database.DBSchema;
import appdev.technologies.furfindspetshop.database.PetsModel;
import appdev.technologies.furfindspetshop.eventhandlers.OnViewPagerFragmentCreated;
import appdev.technologies.furfindspetshop.helpers.Extensions;
import appdev.technologies.furfindspetshop.helpers.ValidationContainer;

public class PetRegistrationFragment2 extends Fragment
{
    private EditText inputPetDescription;
    private EditText inputPetPrice;

    private RadioGroup forSaleGroup;
    private RadioButton forSaleYes;
    private RadioButton forSaleNo;
    private String selectedIsForSale;

    private RadioGroup forAdoptGroup;
    private RadioButton forAdoptYes;
    private RadioButton forAdoptNo;
    private String selectedIsForAdopt;

    private Spinner healthSpinner;
    private ArrayAdapter<String> healthAdapter;
    private String selectedHealth;

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
        View view = inflater.inflate(R.layout.pet_registration_fragment2, container, false);

        forSaleGroup = view.findViewById(R.id.pet_option_sale_group);
        forSaleGroup.setOnCheckedChangeListener(forSaleGroupCheckedChangedListener());

        forSaleYes = view.findViewById(R.id.option_sale_yes);
        forSaleNo = view.findViewById(R.id.option_sale_no);

        forAdoptGroup = view.findViewById(R.id.pet_option_adoption_group);
        forAdoptGroup.setOnCheckedChangeListener(forAdoptGroupCheckedChangedListener());

        forAdoptYes = view.findViewById(R.id.option_adopt_yes);
        forAdoptNo = view.findViewById(R.id.option_adopt_no);

        healthSpinner = view.findViewById(R.id.pet_health_spinner);
        healthAdapter = Extensions.buildSelectMenu(context, R.array.pet_health_conditions_select);

        healthSpinner.setAdapter(healthAdapter);
        healthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                selectedHealth = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        inputPetDescription = view.findViewById(R.id.input_pet_details);
        inputPetPrice = view.findViewById(R.id.input_pet_price);

        // Event that can be used by activities, when fragment is ready
        //
        if (onFragmentReady != null)
            onFragmentReady.ready();

        return view;
    }

    private RadioGroup.OnCheckedChangeListener forSaleGroupCheckedChangedListener()
    {
        return (group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked)
                selectedIsForSale = checkedRadioButton.getText().toString();

            if (isChecked)
            {
                String value = checkedRadioButton.getText().toString();

                if (value.equals(context.getString(R.string.button_text_yes)))
                    showInputPrice(true);
                else
                {
                    showInputPrice(false);
                    inputPetPrice.setText("");
                }
                selectedIsForSale = value;
            }
        };
    }

    private RadioGroup.OnCheckedChangeListener forAdoptGroupCheckedChangedListener()
    {
        return (group, checkedId) -> {
            RadioButton checkedRadioButton = group.findViewById(checkedId);
            boolean isChecked = checkedRadioButton.isChecked();

            if (isChecked)
                selectedIsForAdopt = checkedRadioButton.getText().toString();
        };
    }

    public void setOnFragmentReady(OnViewPagerFragmentCreated listener) {
        this.onFragmentReady = listener;
    }

    private void showInputPrice(boolean show)
    {
        if (show)
            inputPetPrice.setVisibility(View.VISIBLE);
        else
            inputPetPrice.setVisibility(View.INVISIBLE);
    }

    public String getPrice() {
        return inputPetPrice.getText().toString();
    }

    public String getIsForSale() {
        return selectedIsForSale;
    }

    public String getIsForAdopt() {
        return selectedIsForAdopt;
    }

    public String getHealth()
    {
        if (Extensions.StrNullOrEmpty(selectedHealth))
            return "";

        if (selectedHealth.equals(context.getString(R.string.item_choose)))
            return "";

        return selectedHealth;
    }

    public String getDescription() {
        return inputPetDescription.getText().toString();
    }

    public void clearInputs()
    {
        selectedHealth = "";
        inputPetDescription.setText("");
        healthSpinner.setSelection(0);

        // Clear for sale questions
        selectedIsForSale = "";
        forSaleGroup.setOnCheckedChangeListener(null);
        forSaleGroup.clearCheck();
        forSaleGroup.setOnCheckedChangeListener(forSaleGroupCheckedChangedListener());

        inputPetPrice.setText("");
        showInputPrice(false);

        // Clear for adopt questions
        selectedIsForAdopt = "";
        forAdoptGroup.setOnCheckedChangeListener(null);
        forAdoptGroup.clearCheck();
        forAdoptGroup.setOnCheckedChangeListener(forAdoptGroupCheckedChangedListener());
    }

    public void prefillEntries(ContentValues contentValues)
    {
        int isForSale = Integer.parseInt(contentValues.getAsString(PetsModel.COLUMN_FOR_SALE));

        switch (isForSale)
        {
            case DBSchema.DATA_TRUE:
                forSaleYes.setChecked(true);
                inputPetPrice.setText( contentValues.getAsString(PetsModel.COLUMN_SALE_PRICE) );
                break;
            case DBSchema.DATA_FALSE:
                forSaleNo.setChecked(true);
                break;
        }

        int isForAdopt = Integer.parseInt(contentValues.getAsString(PetsModel.COLUMN_FOR_ADOPTION));

        switch (isForAdopt)
        {
            case DBSchema.DATA_TRUE:
                forAdoptYes.setChecked(true);
                break;
            case DBSchema.DATA_FALSE:
                forAdoptNo.setChecked(true);
                break;
        }

        int healthIndex = healthAdapter.getPosition(contentValues.getAsString(PetsModel.COLUMN_HEALTH_CONDITION));
        healthSpinner.setSelection(healthIndex);

        String details = contentValues.getAsString(PetsModel.COLUMN_DESCRIPTION).replaceAll("<br>", "\n");
        inputPetDescription.setText(details);
    }

    public List<ValidationContainer> getValidatedFields()
    {
        List<ValidationContainer> fields = new ArrayList<>();

        // 1st parameter -> Key,   used as Database Table Column / Field;
        // 2nd parameter -> View,  will be used to focus and animate later on
        // 3rd parameter -> Value, which will be saved into the database

        String isForSale  = DBSchema.mapYesNo( getIsForSale() );
        String isForAdopt = DBSchema.mapYesNo( getIsForAdopt() );

        fields.add(new ValidationContainer(PetsModel.COLUMN_FOR_SALE,         forSaleGroup,         isForSale));
        fields.add(new ValidationContainer(PetsModel.COLUMN_SALE_PRICE,       inputPetPrice,        getPrice(), "forSale"));
        fields.add(new ValidationContainer(PetsModel.COLUMN_FOR_ADOPTION,     forAdoptGroup,        isForAdopt));
        fields.add(new ValidationContainer(PetsModel.COLUMN_HEALTH_CONDITION, healthSpinner,        getHealth()));
        fields.add(new ValidationContainer(PetsModel.COLUMN_DESCRIPTION,      inputPetDescription,  getDescription()));

        for (ValidationContainer v : fields)
        {
            // Check the field if it has a tag "for sale"
            boolean hasTag_forSale = !Extensions.StrNullOrEmpty(v.getTagName()) && v.getTagName().equals("forSale");

            // if it does, check its option if ForSale (YES) or not (NO).
            if (hasTag_forSale)
            {
                // If not for sale, skip the checking
                if (!isForSale.equals( String.valueOf(DBSchema.DATA_TRUE) ))
                    continue;
            }

            if (Extensions.StrNullOrEmpty(v.getValue()))
            {
                List<ValidationContainer> failedEntry = new ArrayList<>();
                failedEntry.add(new ValidationContainer(v.getKey(), v.getView(), null));

                return failedEntry;
            }
        }

        return fields;
    }
}