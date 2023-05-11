package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mikefonseta.chatx.Activity.MainActivity;
import com.mikefonseta.chatx.Adapter.TabAdapter;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Controller.ChatController;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;

public class HomeFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (AuthenticationController.isLogged()) {
            ((MainActivity) requireActivity()).getSupportActionBar().setTitle(AuthenticationController.getUser().getUsername());
            int user_id = AuthenticationController.getUser().getUser_id();
            ConnectionHandler.getInstance().doRequest(ChatController.getRoomsRequest(user_id));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = view.findViewById(R.id.viewPager2);
        viewPager.setAdapter(new TabAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(getTitle(position))).attach();
    }

    private String getTitle(int position) {
        if (position == 0)
            return "Chat";
        else
            return "Altre";
    }
}
