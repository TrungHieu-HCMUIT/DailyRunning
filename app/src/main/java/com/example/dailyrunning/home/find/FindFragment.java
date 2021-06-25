package com.example.dailyrunning.home.find;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentFindBinding;
import com.example.dailyrunning.home.HomeViewModel;
import com.example.dailyrunning.model.UserInfo;
import com.example.dailyrunning.user.UserViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FindFragment extends Fragment {

    private static final String TAG = "FindFragment";

    private FragmentFindBinding binding;
    private HomeViewModel mHomeViewModel;
    private UserViewModel mUserViewModel;

    private NavController mNavController;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private ArrayList<UserInfo> mUserList_original = new ArrayList<>();
    private ArrayList<UserInfo> mUserList_result = new ArrayList<>();
    private UserRowAdapter mAdapter;
    private OtherUserProfileViewModel mOtherUserProfileViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFindBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Firebase setup
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // Init ViewModel
        mHomeViewModel = new ViewModelProvider(getActivity()).get(HomeViewModel.class);
        mUserViewModel = new ViewModelProvider(getActivity()).get(UserViewModel.class);
        mOtherUserProfileViewModel = new ViewModelProvider(getActivity()).get(OtherUserProfileViewModel.class);
        mNavController = Navigation.findNavController(view);

        initWidget();

        initRecyclerView();
    }

    private void initWidget() {
        // region Edittext
        binding.searchUserEdt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        binding.searchUserEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.searchUserEdt.getText().toString().isEmpty()) {
                    binding.resultRv.setVisibility(View.INVISIBLE);
                    binding.clearTextIcon.setVisibility(View.INVISIBLE);
                }
                else {
                    binding.resultRv.setVisibility(View.VISIBLE);
                    binding.clearTextIcon.setVisibility(View.VISIBLE);
                    setupRecyclerView();
                }
            }
        });
        // endregion

        // region back arrow icon
        binding.backArrowIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        // endregion

        // region clear text icon
        binding.clearTextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchUserEdt.setText(null);
                binding.noResultTv.setVisibility(View.INVISIBLE);
            }
        });
        // endregion
    }

    private void initRecyclerView() {
        mAdapter = new UserRowAdapter(new UserRowAdapter.OnUserClick() {
            @Override
            public void onUserClick(UserInfo user) {
                mOtherUserProfileViewModel.onUserSelected(user);
                mNavController.navigate(R.id.action_findFragment_to_otherUserProfile);
            }
        }, mUserList_result);
        binding.resultRv.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.resultRv.setAdapter(mAdapter);

        databaseReference.child("UserInfo").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e("firebase", "Error getting data", task.getException());
            }
            else if (mUserList_original.size() == 0){
                for (DataSnapshot userSnapshot: task.getResult().getChildren()) {
                    UserInfo user = userSnapshot.getValue(UserInfo.class);
                    mUserList_original.add(user);
                }
            }
        });
    }

    private void setupRecyclerView() {
        mUserList_result.clear();

        for (UserInfo user: mUserList_original) {
            String name = user.getDisplayName();
            if (name.toLowerCase().contains(binding.searchUserEdt.getText().toString().toLowerCase().trim())
                && !name.equals(mUserViewModel.getCurrentUser().getValue().getDisplayName())) {
                        mUserList_result.add(user);
            }
        }
        if (mUserList_result.size() == 0
            && binding.searchUserEdt.getText().toString().trim().length() != 0) {
            binding.noResultTv.setVisibility(View.VISIBLE);
        }
        else {
            binding.noResultTv.setVisibility(View.INVISIBLE);
        }
        mAdapter.notifyDataSetChanged();
    }
}
