package com.example.chat2023.ui.nuovaStanza;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.chat2023.R;
import com.example.chat2023.databinding.FragmentNuovaStanzaBinding;

public class NuovaStanzaFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NuovaStanzaViewModel dashboardViewModel =
                new ViewModelProvider(this).get(NuovaStanzaViewModel.class);

        View view = inflater.inflate(R.layout.fragment_nuova_stanza, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}