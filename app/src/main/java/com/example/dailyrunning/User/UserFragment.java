package com.example.dailyrunning.User;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.dailyrunning.R;
import com.example.dailyrunning.Utils.MedalAdapter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.taosif7.android.ringchartlib.RingChart;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class UserFragment extends Fragment {
    private static final int RC_PHOTO_PICKER = 101;
    private static final String EMAIL_PROVIDER_ID="password";
    private FirebaseAuth mFirebaseAuth;
    private RecyclerView mMedalRecycleView;
    private View rootView;
    private SegmentTabLayout tab_layout;
    private ViewPager2 statisticalViewPager2;
    private RingChart mRingChart;
    private CircleImageView avatarView;
    private TextView userTextView;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mAvatarStorageReference;
    private FirebaseUser mCurrentUser;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        rootView = view;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage=FirebaseStorage.getInstance();
        mAvatarStorageReference=mFirebaseStorage.getReference().child("avatar_photos");
        mCurrentUser=mFirebaseAuth.getCurrentUser();
        findView();

        userTextView.setOnClickListener(v -> {
            mFirebaseAuth.signOut();
        });

        setUpUpdateAvatar();

        setUpRingChart();
        setUpMedalRecycleView();
        setUpTabLayout();

        return view;
    }

    private void setUpUpdateAvatar() {
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentUser.getProviderId().equals(EMAIL_PROVIDER_ID)) { //người dùng đăng nhập bằng email mới set avatar được
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
                }
                else // nếu không thì lấy avatar link với tài khoản facebook hoặc google của người dùng
                {

                }
            }
        });
    }


    private void findView() {
        userTextView = (TextView) rootView.findViewById(R.id.user_textView);
        avatarView=rootView.findViewById(R.id.avatarView);
        mRingChart = rootView.findViewById(R.id.chart_concentric);
        tab_layout = rootView.findViewById(R.id.tl_2);
        statisticalViewPager2 = rootView.findViewById(R.id.statistical_viewPager2);
        mMedalRecycleView = rootView.findViewById(R.id.medal_recycleView);

    }


    private void setUpRingChart() {
        mRingChart.showLabels(false);
        mRingChart.startAnimateLoading();


        new CountDownTimer(3000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mRingChart.stopAnimateLoading(0.6f);

            }
        }.start();

    }

    private void setUpTabLayout() {
        tab_layout.setTabData(new String[]{"Theo tuần", "Theo tháng", "Theo năm"});
        StatisticalViewPagerAdapter statisticalViewPagerAdapter = new StatisticalViewPagerAdapter(this);
        statisticalViewPager2.setAdapter(statisticalViewPagerAdapter);
        tab_layout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                statisticalViewPager2.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        ViewPager2.OnPageChangeCallback pageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tab_layout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        };
        statisticalViewPager2.registerOnPageChangeCallback(pageChangeCallback);

    }

    private void setUpMedalRecycleView() {
        mMedalRecycleView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

        List<Integer> medalIDs = new ArrayList<>();
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);
        medalIDs.add(R.drawable.medal_1);
        medalIDs.add(R.drawable.medal_2);
        medalIDs.add(R.drawable.medal_3);
        medalIDs.add(R.drawable.medal_4);
        medalIDs.add(R.drawable.medal_5);

        MedalAdapter adapter = new MedalAdapter(medalIDs);
        mMedalRecycleView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode==RC_PHOTO_PICKER && resultCode==RESULT_OK)
        {
            Uri selectedImageUri = data.getData();
            StorageReference photoRef=mAvatarStorageReference.child(selectedImageUri.getLastPathSegment());
            photoRef.putFile(selectedImageUri).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri userAvatarUri= taskSnapshot.getUploadSessionUri();
                    UserProfileChangeRequest profileUpdates  =new UserProfileChangeRequest.Builder().setPhotoUri(userAvatarUri).build();
                    mCurrentUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Glide.with(avatarView.getContext()).load(mCurrentUser.getPhotoUrl()).into(avatarView);
                        }
                    });
                }
            });
        }
    }
}
