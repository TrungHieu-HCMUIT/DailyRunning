package com.example.dailyrunning.User;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.HomeViewModel;
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
    private int mYear,mMonth,mDay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView=view;
        initView();
        mHomeViewModel.mHomeActivity.getValue().hideNavBar();
        setUpNumberPicker();
        viewFunctional();
    }

    private void setUpNumberPicker() {
        mHeightPicker.setMinValue(130);
        mHeightPicker.setMaxValue(220);
        mWeightPicker.setMinValue(30);
        mWeightPicker.setMaxValue(200);

    }

    private void viewFunctional() {
        rootView.findViewById(R.id.back_button).setOnClickListener(v->{
            getActivity().onBackPressed();
        });
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mDOBTextInputLayout.getEditText().setOnClickListener(v->{
            DatePickerDialog mDatePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                String dobString=String.format("%02d", dayOfMonth)+"/"+String.format("%02d", month)+"/"+year;
                mDOBTextInputLayout.getEditText().setText(dobString);
            }, mYear, mMonth, mDay);
            mDatePickerDialog.show();
        });




    }

    private void initView() {
        mHeightPicker=rootView.findViewById(R.id.height_picker);
        mWeightPicker=rootView.findViewById(R.id.weight_picker);
        mHomeViewModel=new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mDOBTextInputLayout=rootView.findViewById(R.id.update_dob_text_input_layout);
        mEmailTextInputLayout=rootView.findViewById(R.id.update_email_text_input_layout);
        mNameTextInputLayout=rootView.findViewById(R.id.update_name_text_input_layout);
    }
}