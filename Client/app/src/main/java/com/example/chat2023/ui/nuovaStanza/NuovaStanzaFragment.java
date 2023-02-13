package com.example.chat2023.ui.nuovaStanza;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat2023.databinding.FragmentNuovaStanzaBinding;

public class NuovaStanzaFragment extends Fragment {

    private FragmentNuovaStanzaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NuovaStanzaViewModel dashboardViewModel =
                new ViewModelProvider(this).get(NuovaStanzaViewModel.class);

        binding = FragmentNuovaStanzaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNuovaStanza;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}