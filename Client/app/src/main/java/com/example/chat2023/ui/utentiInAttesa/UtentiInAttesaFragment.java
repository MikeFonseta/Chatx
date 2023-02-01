package com.example.chat2023.ui.utentiInAttesa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat2023.databinding.FragmentUtentiAttesaBinding;

public class UtentiInAttesaFragment extends Fragment {

    private FragmentUtentiAttesaBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        UtentiInAttesaViewModel notificationsViewModel =
                new ViewModelProvider(this).get(UtentiInAttesaViewModel.class);

        binding = FragmentUtentiAttesaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textAttesa;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}