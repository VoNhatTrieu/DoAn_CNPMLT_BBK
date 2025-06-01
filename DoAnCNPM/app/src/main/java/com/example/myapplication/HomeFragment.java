package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;



public class HomeFragment extends Fragment {

    ViewPager2 bannerPager;
    RecyclerView rvCakes;
//    List<Cake> cakeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        bannerPager = view.findViewById(R.id.bannerPager);
        rvCakes = view.findViewById(R.id.rvCakes);

//        bannerPager.setAdapter(new BannerAdapter(requireContext()));

//        cakeList = new ArrayList<>();
//        cakeList.add(new Cake(1, "Bánh Socola", "Sinh nhật", 100.0, R.drawable.choco));
//        cakeList.add(new Cake(2, "Bánh Dâu", "Valentine", 120.0, R.drawable.strawberry));
//
//        CakeAdapter adapter = new CakeAdapter(requireContext(), cakeList);
//        rvCakes.setLayoutManager(new LinearLayoutManager(requireContext()));
//        rvCakes.setAdapter(adapter);

        return view;
    }
}