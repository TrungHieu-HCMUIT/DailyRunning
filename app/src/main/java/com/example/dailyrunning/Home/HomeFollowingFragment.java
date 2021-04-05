package com.example.dailyrunning.Home;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dailyrunning.Model.Post;
import com.example.dailyrunning.Model.PostDataTest;
import com.example.dailyrunning.R;

import java.util.ArrayList;

public class HomeFollowingFragment extends Fragment {

    private Context context;
    private ArrayList<PostDataTest> postList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PostViewAdapter postViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_following, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.home_following_recycleView);
        populateData();
        postViewAdapter = new PostViewAdapter(context, postList);
        recyclerView.setAdapter(postViewAdapter);
    }

    private void populateData() {
        postList.add(new PostDataTest("https://instagram.fsgn5-3.fna.fbcdn.net/v/t51.2885-19/s320x320/162469737_1662169554172001_2428488417590335889_n.jpg?tp=1&_nc_ht=instagram.fsgn5-3.fna.fbcdn.net&_nc_ohc=JGrV2u-B4WMAX_vWj7a&edm=ABfd0MgAAAAA&ccb=7-4&oh=4d97d7d0a3d967f5b159e46ce5a832bb&oe=608F568C&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-4.fna.fbcdn.net/v/t51.2885-15/e35/c0.179.1440.1440a/s320x320/167590972_748478709365546_921252552525135015_n.jpg?tp=1&_nc_ht=instagram.fsgn5-4.fna.fbcdn.net&_nc_cat=102&_nc_ohc=0j2Edpa0cT4AX9YWask&edm=ABfd0MgAAAAA&ccb=7-4&oh=03e213322ecd681937a9b4574c559214&oe=6091748D&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-7.fna.fbcdn.net/v/t51.2885-15/e35/c0.180.1440.1440a/s320x320/161435862_499058137769199_4901516940441570766_n.jpg?tp=1&_nc_ht=instagram.fsgn5-7.fna.fbcdn.net&_nc_cat=103&_nc_ohc=N5Nwvmnprk4AX-T-HuQ&edm=ABfd0MgAAAAA&ccb=7-4&oh=20163756312ea5b126525f68fc6bd059&oe=60901AF3&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-2.fna.fbcdn.net/v/t51.2885-15/e35/c0.180.1440.1440a/s320x320/157356678_882702789161011_6667965188211897328_n.jpg?tp=1&_nc_ht=instagram.fsgn5-2.fna.fbcdn.net&_nc_cat=107&_nc_ohc=dXHfnYymkKsAX_i_KTj&edm=ABfd0MgAAAAA&ccb=7-4&oh=f62aaa3386748e7654244a1c70b321cc&oe=6090397E&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-1.fna.fbcdn.net/v/t51.2885-15/e35/c0.179.1440.1440a/s320x320/155214179_1086564225192049_2237124637955757215_n.jpg?tp=1&_nc_ht=instagram.fsgn5-1.fna.fbcdn.net&_nc_cat=101&_nc_ohc=fCNmYXy7xuMAX9tb_bR&edm=ABfd0MgAAAAA&ccb=7-4&oh=9752272c411c675588a6ed655e52e854&oe=608F3833&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-4.fna.fbcdn.net/v/t51.2885-15/e35/c0.179.1440.1440a/s320x320/148872844_756921138537494_4413030103841049248_n.jpg?tp=1&_nc_ht=instagram.fsgn5-4.fna.fbcdn.net&_nc_cat=102&_nc_ohc=HctybWOx-YwAX_56_hC&edm=ABfd0MgAAAAA&ccb=7-4&oh=840d72224b4fc9b675cb49031c7df724&oe=608F04E0&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-6.fna.fbcdn.net/v/t51.2885-15/e35/c240.0.960.960a/s320x320/143898486_455423489160105_4407732720224486148_n.jpg?tp=1&_nc_ht=instagram.fsgn5-6.fna.fbcdn.net&_nc_cat=109&_nc_ohc=XDikMjpzrE4AX98kVOP&edm=ABfd0MgAAAAA&ccb=7-4&oh=759bb9c9d354e981e3b1e3ee1fde1f1a&oe=609201F7&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-5.fna.fbcdn.net/v/t51.2885-15/e35/c0.180.1440.1440a/s320x320/127969459_1801245083371704_1668050784062683653_n.jpg?tp=1&_nc_ht=instagram.fsgn5-5.fna.fbcdn.net&_nc_cat=100&_nc_ohc=LEk9V4UMcVkAX-pe6ZK&edm=ABfd0MgAAAAA&ccb=7-4&oh=6ec82f1d2c7dd1dcfb97ce282cfa96de&oe=6091AC46&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-5.fna.fbcdn.net/v/t51.2885-15/e35/c0.140.1126.1126a/s320x320/122404323_200726234764152_1843641344084842837_n.jpg?tp=1&_nc_ht=instagram.fsgn5-5.fna.fbcdn.net&_nc_cat=100&_nc_ohc=onkJS8A8sxIAX-fgkZa&edm=ABfd0MgAAAAA&ccb=7-4&oh=3643d3d03b176451f0eefb819141679f&oe=6091BEB9&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-3.fna.fbcdn.net/v/t51.2885-15/e35/s320x320/87648391_799631553866291_3171351403486211399_n.jpg?tp=1&_nc_ht=instagram.fsgn5-3.fna.fbcdn.net&_nc_cat=111&_nc_ohc=YkhTntTltscAX-tRh8U&edm=ABfd0MgAAAAA&ccb=7-4&oh=6bc3ddf103c443457ae0615b0fabcbac&oe=609119B8&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-7.fna.fbcdn.net/v/t51.2885-15/e35/s320x320/84331842_583887932165319_8118326077425487737_n.jpg?tp=1&_nc_ht=instagram.fsgn5-7.fna.fbcdn.net&_nc_cat=103&_nc_ohc=8RvTW_J8K30AX8sMiy7&edm=ABfd0MgAAAAA&ccb=7-4&oh=22ad398506d4489d445e4755f8d7a9ce&oe=608F071E&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
        postList.add(new PostDataTest("https://instagram.fsgn5-2.fna.fbcdn.net/v/t51.2885-15/e35/c0.180.1440.1440a/s320x320/83318224_175955883637145_5807612225577421478_n.jpg?tp=1&_nc_ht=instagram.fsgn5-2.fna.fbcdn.net&_nc_cat=105&_nc_ohc=vNxxf2331PEAX_7boVB&edm=ABfd0MgAAAAA&ccb=7-4&oh=a594b68be928ffb0f18867dc5e2a35ff&oe=608F6C11&_nc_sid=7bff83", "Trung Hiếu", "2021-12-02 00:00:00", "Mô tả", "10km", "20ph", "20 m/ph", 20, 20));
    }
}