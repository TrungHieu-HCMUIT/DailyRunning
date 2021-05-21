package com.example.dailyrunning.Authentication;

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

import com.example.dailyrunning.Model.UserInfo;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.HomeViewModel;
import com.example.dailyrunning.Utils.LoginViewModel;
import com.example.dailyrunning.Utils.UserViewModel;
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

public class RegisterAddInfoFragment extends Fragment {

    private View rootView;
    private NumberPicker mHeightPicker;
    private NumberPicker mWeightPicker;
    private TextInputLayout mDOBTextInputLayout;
    private TextInputLayout mNameTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    private int mYear, mMonth, mDay;
    private final int MALE = 0;
    private final int FEMALE = 1;
    private Button mSaveButton;
    private CheckBox mMaleCheckBox;
    private CheckBox mFemaleCheckBox;
    private DatabaseReference mUserInfoRef;
    private SimpleDateFormat mDateFormat;
    private LoginViewModel mLoginViewModel;
    private NavController mNavController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_add_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initView();
        mUserInfoRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mLoginViewModel=new ViewModelProvider(getActivity()).get(LoginViewModel.class);
        mNavController=Navigation.findNavController(getActivity(),R.id.login_fragment_container);
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
                mYear=year;
                mMonth=month;
                mDay=dayOfMonth;
                String dobString = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month+1) + "/" + year;
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
        mWeightPicker.setMinValue(30);
        mWeightPicker.setMaxValue(200);
        mWeightPicker.setValue(50);
        mHeightPicker.setValue(160);

    }

    private void viewFunctional() {
        rootView.findViewById(R.id.back_button).setOnClickListener(v -> {
            getActivity().onBackPressed();
        });
        showInfo();

        mSaveButton.setOnClickListener(v -> {
            String emailString = mEmailTextInputLayout.getEditText().getText().toString().trim();
            String nameString = mNameTextInputLayout.getEditText().getText().toString().trim();
            Integer gender = mMaleCheckBox.isChecked() ? 0 : (mFemaleCheckBox.isChecked() ? 1 : 0);
            String dob = mDOBTextInputLayout.getEditText().getText().toString();
            int height = mHeightPicker.getValue();
            int weight = mWeightPicker.getValue();
            if (validateData(emailString, nameString, gender, dob, height, weight)) {
                UserInfo mNewInfo=mLoginViewModel.tempUser;
                mNewInfo.setDisplayName(nameString);
                mNewInfo.setEmail(emailString);
                mNewInfo.setGender(gender);
                try {
                    mNewInfo.setDob(mDateFormat.parse(dob)  );
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mNewInfo.setHeight(height);
                mNewInfo.setWeight(weight);
                mUserInfoRef.child(mNewInfo.getUserID()).setValue(mNewInfo).addOnCompleteListener(task->{
                    if(task.isSuccessful())
                    {
                        updateFirebaseUser(mNewInfo);
                        Boolean isFromRegister=mLoginViewModel.isFromRegister;
                        mLoginViewModel.getNewUser().postValue(mNewInfo);
                        mLoginViewModel.isFromRegister=isFromRegister;
                        mNavController.navigate(R.id.action_registerAddInfoFragment_to_loginFragment);
                    }
                    else if(!task.isSuccessful())
                    {
                        Toast.makeText(getContext(),"Cập nhật thông tin thất bại",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


    }

    private void showInfo() {
        mEmailTextInputLayout.getEditText().setText(mLoginViewModel.tempUser.getEmail());
        mNameTextInputLayout.getEditText().setText(mLoginViewModel.tempUser.getDisplayName());
    }

    private void updateFirebaseUser(UserInfo mNewInfo) {
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        mUser.updateEmail(mNewInfo.getEmail());
        UserProfileChangeRequest mRequest=new UserProfileChangeRequest.Builder().setDisplayName(mNewInfo.getDisplayName()).build();
        mUser.updateProfile(mRequest);
        Toast.makeText(getContext(),"Cập nhật thông tin thành công",Toast.LENGTH_SHORT).show();
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
        mDOBTextInputLayout = rootView.findViewById(R.id.update_dob_text_input_layout);
        mEmailTextInputLayout = rootView.findViewById(R.id.update_email_text_input_layout);
        mNameTextInputLayout = rootView.findViewById(R.id.update_name_text_input_layout);
        mSaveButton = rootView.findViewById(R.id.save_button);
        mMaleCheckBox = rootView.findViewById(R.id.male_radio_button);
        mFemaleCheckBox = rootView.findViewById(R.id.female_radio_button);
        mDateFormat=new SimpleDateFormat("dd/MM/yyyy");
    }
}