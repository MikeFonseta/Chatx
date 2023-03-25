package com.example.chat2023.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.chat2023.R;
import com.example.chat2023.controller.AuthenticationController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);

        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);

        final List<TextInputLayout> textInputLayouts = new ArrayList<>();
        textInputLayouts.add(usernameTextInput);
        textInputLayouts.add(passwordTextInput);

        MaterialButton registerButton = view.findViewById(R.id.signUp_button);
        registerButton.setOnClickListener(view1 -> {
            boolean noErrors = true;
            for (TextInputLayout textInputLayout : textInputLayouts) {
                if (isEmpty(textInputLayout.getEditText())) {
                    textInputLayout.setError(getResources().getString(R.string.no_input));
                    noErrors = false;
                } else {
                    textInputLayout.setError(null);
                }
            }
            if (noErrors) {
                AuthenticationController.register(getActivity(), usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        MaterialButton backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(view1 -> getParentFragmentManager().popBackStack());

        return view;
    }

    private boolean isEmpty(EditText text) {
        return TextUtils.isEmpty(text.getText().toString());
    }
}