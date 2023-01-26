package com.example.chat2023.ui.profilo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat2023.databinding.FragmentProfiloBinding;

public class ProfiloFragment extends Fragment {

    private FragmentProfiloBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfiloViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ProfiloViewModel.class);

        binding = FragmentProfiloBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textProfilo;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}