package com.example.chat2023.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.chat2023.HomeActivity;
import com.example.chat2023.R;
import com.example.chat2023.entity.User;
import com.example.chat2023.util.ConnectionService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);

        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);

        MaterialButton loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(view1 -> {
            if (isEmpty(usernameEditText) || isEmpty(passwordEditText)) {

            } else {
                User user = new User(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                JSONObject login = user.login();
                System.out.println("json: " + login.toString());
                Intent loginIntent = new Intent(view.getContext(), ConnectionService.class);
                loginIntent.setAction(ConnectionService.LOGIN);
                loginIntent.putExtra("json", login.toString());
                getActivity().startService(loginIntent);
            }

            Intent i = new Intent(getActivity(), HomeActivity.class);
            startActivity(i);
        });

        return view;
    }

    private boolean isEmpty(EditText text) {
        return TextUtils.isEmpty(text.getText().toString());
    }
}