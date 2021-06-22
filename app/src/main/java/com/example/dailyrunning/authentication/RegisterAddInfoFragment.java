package com.example.dailyrunning.authentication;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
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

import com.example.dailyrunning.databinding.FragmentRegisterAddInfoBinding;
import com.example.dailyrunning.model.Activity;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.Deflater;

import static android.app.Activity.RESULT_OK;

public class RegisterAddInfoFragment extends Fragment {

    private View rootView;
    private int mYear, mMonth, mDay;
    private final int MALE = 0;
    private final int FEMALE = 1;

    private DatabaseReference mUserInfoRef;
    private SimpleDateFormat mDateFormat;
    private LoginViewModel mLoginViewModel;
    private NavController mNavController;
    private FragmentRegisterAddInfoBinding binding;
    private FragmentActivity mParent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentRegisterAddInfoBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mParent= (FragmentActivity) getContext();
        rootView = view;
        initView();
        mUserInfoRef= FirebaseDatabase.getInstance().getReference().child("UserInfo");
        mLoginViewModel=new ViewModelProvider(mParent).get(LoginViewModel.class);
        binding.setLoginViewModel(mLoginViewModel);
        binding.setLifecycleOwner(getActivity());
        mNavController=Navigation.findNavController(getActivity(),R.id.login_fragment_container);
        setUp();

        viewFunctional();
    }
    private void setUp() {
        setUpDatePicker();
        setUpTextInputLayout();
        setUpGenderCheckBox();
    }
    private void setUpGenderCheckBox() {
        binding.femaleRadioButton.setOnClickListener(v->{
            if (binding.femaleRadioButton.isChecked())
                binding.maleRadioButton.setChecked(false);
        });

        binding.maleRadioButton.setOnClickListener(v->{
            if (binding.maleRadioButton.isChecked())
                binding.femaleRadioButton.setChecked(false);
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
                String emailString=binding.updateEmailTextInputLayout.getEditText().getText().toString().trim();
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
                {
                    binding.updateEmailTextInputLayout.setError("Email không hợp lệ");
                }
                else
                {
                    binding.updateEmailTextInputLayout.setError(null);

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
        binding.updateDobTextInputLayout.getEditText().setOnClickListener(v -> {
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                mYear=year;
                mMonth=month;
                mDay=dayOfMonth;
                String dobString = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month+1) + "/" + year;
                binding.updateDobTextInputLayout.getEditText().setText(dobString);
            }, mYear, mMonth, mDay);

            mDatePickerDialog.show();
        });
    }

    private void completeLogin()
    {
        getActivity().setResult(RESULT_OK);
        mParent.finish();
    }

    private void viewFunctional() {

        binding.saveButton.setOnClickListener(v -> {

            UserInfo mNewInfo=mLoginViewModel.getNewUser().getValue();
            mLoginViewModel.onUpdateInfoClick(new LoginViewModel.TaskCallBack() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    completeLogin();
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(getContext(), "Cập nhật thông tin thất bại"+ exception.toString(), Toast.LENGTH_SHORT).show();
                    exception.printStackTrace();
                }
            },getContext());
        });


    }







    private void initView() {
        mDateFormat=new SimpleDateFormat("dd/MM/yyyy");
    }
}