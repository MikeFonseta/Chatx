package com.example.chat2023.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chat2023.R;

public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences preferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        String username = preferences.getString("username", null);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView textView = view.findViewById(R.id.username);
        textView.setText(username);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}