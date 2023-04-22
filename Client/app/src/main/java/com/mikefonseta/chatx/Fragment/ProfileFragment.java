package com.mikefonseta.chatx.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.R;


public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView textView = view.findViewById(R.id.username);
        textView.setText(AuthenticationController.getUser().getUsername());
        return view;
    }

}