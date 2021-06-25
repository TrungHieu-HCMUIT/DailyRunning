package com.example.dailyrunning.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.R;
import com.example.dailyrunning.databinding.FragmentListUserBinding;
import com.example.dailyrunning.home.find.OtherUserProfileViewModel;
import com.example.dailyrunning.home.find.UserRowAdapter;
import com.example.dailyrunning.model.UserInfo;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;


public class ListUserFragment extends Fragment {

    FragmentListUserBinding binding;
    ListUserViewModel mLisUserViewModel;
    OtherUserProfileViewModel mOtherUserProfileViewModel;
    NavController mNavController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentListUserBinding.inflate(inflater,container,false);
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLisUserViewModel=new ViewModelProvider((ViewModelStoreOwner) getContext()).get(ListUserViewModel.class);
        mOtherUserProfileViewModel=new ViewModelProvider((ViewModelStoreOwner) getContext()).get(OtherUserProfileViewModel.class);
        mNavController= Navigation.findNavController(view);
        binding.setListUserViewModel(mLisUserViewModel);
        binding.setLifecycleOwner((LifecycleOwner) getContext());
        UserRowAdapter adapter=new UserRowAdapter(user -> {
            if(user.getUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                return;
            mOtherUserProfileViewModel.onUserSelected(user);
            mNavController.navigate(R.id.action_listUserFragment_to_otherUserProfile);
        },mLisUserViewModel.users.getValue());
        binding.listUserRecyclerView.setAdapter(adapter);
        mLisUserViewModel.users.observe((LifecycleOwner) getContext(), adapter::updateUserList);
        binding.backButton.setOnClickListener(v->{
            getActivity().onBackPressed();
        });

    }
}