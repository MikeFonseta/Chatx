package com.mikefonseta.chatx.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mikefonseta.chatx.Activity.AuthenticationActivity;
import com.mikefonseta.chatx.Controller.AuthenticationController;
import com.mikefonseta.chatx.Network.ConnectionHandler;
import com.mikefonseta.chatx.R;


public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView textView = view.findViewById(R.id.username);
        textView.setText(AuthenticationController.getUser().getUsername());
        Button button = view.findViewById(R.id.logout_button);
        button.setOnClickListener(view1 -> logout());
    }

    private void logout() {
        ConnectionHandler.doRequest(AuthenticationController.getLogoutRequest());
        AuthenticationController.logout();
        requireActivity().getApplicationContext().deleteSharedPreferences("credentials");
        requireActivity().finish();
        startActivity(new Intent(getActivity(), AuthenticationActivity.class));
    }
}