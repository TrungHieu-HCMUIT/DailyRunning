package com.example.dailyrunning.User;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.HomeViewModel;
import com.example.dailyrunning.Utils.UserViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;


public class UpdateInfoFragment extends Fragment {


    private View rootView;
    private NumberPicker mHeightPicker;
    private NumberPicker mWeightPicker;
    private HomeViewModel mHomeViewModel;
    private TextInputLayout mDOBTextInputLayout;
    private TextInputLayout mNameTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    private int mYear, mMonth, mDay;
    private UserViewModel mUserViewModel;
    private final int MALE = 0;
    private final int FEMALE = 1;
    private Button mSaveButton;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initView();
        mHomeViewModel.mHomeActivity.getValue().hideNavBar();
        setUp();

        viewFunctional();
    }

    private void setUp() {
        setUpNumberPicker();
        setUpGenderCheckBox();
        setUpDatePicker();
        setUpTextInputLayout();

    }

    private void setUpTextInputLayout() {
        mEmailTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailString=mEmailTextInputLayout.getEditText().getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
                {
                    mEmailTextInputLayout.setError("Email không hợp lệ");
                }
                else
                {
                    mEmailTextInputLayout.setError(null);

                }
            }
        });
    }

    private void setUpDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mDOBTextInputLayout.getEditText().setOnClickListener(v -> {
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                String dobString = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;
                mDOBTextInputLayout.getEditText().setText(dobString);
            }, mYear, mMonth, mDay);
            mDatePickerDialog.show();
        });
    }

    private void setUpGenderCheckBox() {
        mFemaleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mMaleCheckBox.setChecked(false);
        });
        mMaleCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                mFemaleCheckBox.setChecked(false);
        });
    }

    private void setUpNumberPicker() {
        mHeightPicker.setMinValue(130);
        mHeightPicker.setMaxValue(220);
        mHeightPicker.setValue(160);
        mWeightPicker.setMinValue(30);
        mWeightPicker.setMaxValue(200);
        mWeightPicker.setValue(50);

    }

    private void viewFunctional() {
        rootView.findViewById(R.id.back_button).setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        mSaveButton.setOnClickListener(v -> {
            String emailString = mEmailTextInputLayout.getEditText().getText().toString().trim();
            String nameString = mNameTextInputLayout.getEditText().getText().toString().trim();
            Integer gender = mMaleCheckBox.isChecked() ? 0 : (mFemaleCheckBox.isChecked() ? 1 : 0);
            String dob = mDOBTextInputLayout.getEditText().getText().toString();
            int height = mHeightPicker.getValue();
            int weight = mWeightPicker.getValue();
            if (validateData(emailString, nameString, gender, dob, height, weight)) {

            } else {

            }
        });


    }

    private boolean validateData(String emailString, String nameString, Integer gender, String dob, int height, int weight) {
        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(nameString)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (mEmailTextInputLayout.getError() != null || mNameTextInputLayout.getError() != null)
            return false;
        return true;
    }


    private void initView() {
        mHeightPicker = rootView.findViewById(R.id.height_picker);
        mWeightPicker = rootView.findViewById(R.id.weight_picker);
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mDOBTextInputLayout = rootView.findViewById(R.id.update_dob_text_input_layout);
        mEmailTextInputLayout = rootView.findViewById(R.id.update_email_text_input_layout);
        mNameTextInputLayout = rootView.findViewById(R.id.update_name_text_input_layout);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mSaveButton = rootView.findViewById(R.id.save_button);
        mMaleCheckBox = rootView.findViewById(R.id.male_radio_button);
        mFemaleCheckBox = rootView.findViewById(R.id.female_radio_button);
    }
}