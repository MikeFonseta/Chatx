package com.example.chat2023.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView textView = view.findViewById(R.id.sign_up);
        SpannableString signUp = new SpannableString(textView.getText());
        signUp.setSpan(new SignUpSpan(), 14, 24, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(signUp);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        final TextInputLayout usernameTextInput = view.findViewById(R.id.username_text_input);
        final TextInputEditText usernameEditText = view.findViewById(R.id.username_edit_text);

        final TextInputLayout passwordTextInput = view.findViewById(R.id.password_text_input);
        final TextInputEditText passwordEditText = view.findViewById(R.id.password_edit_text);

        final List<TextInputLayout> textInputLayouts = new ArrayList<>();
        textInputLayouts.add(usernameTextInput);
        textInputLayouts.add(passwordTextInput);

        MaterialButton loginButton = view.findViewById(R.id.login_button);

        loginButton.setOnClickListener(view1 -> {
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
                User user = new User(usernameEditText.getText().toString(), passwordEditText.getText().toString());
                JSONObject login = user.login();
                System.out.println("json: " + login.toString());
                Intent loginIntent = new Intent(view.getContext(), ConnectionService.class);
                loginIntent.setAction(ConnectionService.SEND);
                loginIntent.putExtra("json", login.toString());
                getActivity().startService(loginIntent);
            }
        });

        return view;
    }

    private boolean isEmpty(EditText text) {
        return TextUtils.isEmpty(text.getText().toString());
    }

    private final class SignUpSpan extends ClickableSpan {

        @Override
        public void onClick(@NonNull View view) {
            getParentFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).addToBackStack("").commit();
        }
    }
}