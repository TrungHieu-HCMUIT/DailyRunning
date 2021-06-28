package com.example.dailyrunning.user;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.dailyrunning.databinding.FragmentUpdateInfoBinding;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.home.HomeViewModel;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


public class UpdateInfoFragment extends Fragment  {

    private final int MALE = 0;
    private final int FEMALE = 1;
    private int mYear, mMonth, mDay;

    private View rootView;
    private DatabaseReference mUserInfoRef;
    private SimpleDateFormat mDateFormat;
    private FragmentUpdateInfoBinding binding;
    private HomeViewModel mHomeViewModel;

    private UserViewModel mUserViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUpdateInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initView();
        mUserInfoRef = FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mHomeViewModel.mHomeActivity.getValue().hideNavBar();
        binding.setUserViewModel(mUserViewModel);
        binding.setLifecycleOwner(getActivity());
        setUp();

        viewFunctional();
    }

    private void setUp() {
        setUpGenderCheckBox();
        setUpDatePicker();
        setUpTextInputLayout();
        binding.changePasswordTextView.setOnClickListener(v->{
            Navigation.findNavController(requireView()).navigate(R.id.action_updateInfoFragment_to_changePasswordFragment);
        });
    }


    private void setUpTextInputLayout() {
        binding.updateEmailTextInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String emailString = binding.updateEmailTextInputLayout.getEditText().getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
                    binding.updateEmailTextInputLayout.setError("Email không hợp lệ");
                } else {
                    binding.updateEmailTextInputLayout.setError(null);

                }
            }
        });
    }

    private void setUpDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        Date userDob=mUserViewModel.getCurrentUser().getValue().getDob();
        if ( userDob!= null) {
            c.setTime(userDob);
        }
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        binding.updateDobTextInputLayout.getEditText().setOnClickListener(v -> {
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                mYear = year;
                mMonth = month;
                mDay = dayOfMonth;
                String dobString = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month + 1) + "/" + year;
                binding.updateDobTextInputLayout.getEditText().setText(dobString);
            }, mYear, mMonth, mDay);

            mDatePickerDialog.show();
        });
    }

    private void setUpGenderCheckBox() {
        binding.femaleRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                binding.maleRadioButton.setChecked(false);
        });
        binding.maleRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                binding.femaleRadioButton.setChecked(false);
        });
    }


    private void viewFunctional() {

        binding.saveButton.setOnClickListener(v -> {
            String emailString = binding.updateEmailTextInputLayout.getEditText().getText().toString().trim();
            String nameString = binding.updateNameTextInputLayout.getEditText().getText().toString().trim();
            boolean gender = binding.maleRadioButton.isChecked();
            String dob = binding.updateDobTextInputLayout.getEditText().getText().toString();
            int height = binding.heightPicker.getValue();
            int weight = binding.weightPicker.getValue();
            if (validateData(emailString, nameString, gender, dob, height, weight)) {
                UserInfo mNewInfo = mUserViewModel.getCurrentUser().getValue();
                mNewInfo.setDisplayName(nameString);
                mNewInfo.setEmail(emailString);
                mNewInfo.setGender(gender);
                try {
                    mNewInfo.setDob(mDateFormat.parse(dob));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mNewInfo.setHeight(height);
                mNewInfo.setWeight(weight);
                mUserViewModel.updateInfo(mNewInfo, result -> {
                    if (result) {
                        Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                        NavController mNavController = Navigation.findNavController(getActivity(), R.id.home_fragment_container);
                        mNavController.popBackStack();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }


    private boolean validateData(String emailString, String nameString, boolean gender, String dob, int height, int weight) {
        if (TextUtils.isEmpty(emailString) || TextUtils.isEmpty(nameString)) {
            Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.updateEmailTextInputLayout.getError() != null || binding.updateNameTextInputLayout.getError() != null)
            return false;
        return true;
    }


    private void initView() {
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }



}