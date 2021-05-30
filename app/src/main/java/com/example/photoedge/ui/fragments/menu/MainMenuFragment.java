package com.example.photoedge.ui.fragments.menu;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoedge.R;

import org.jetbrains.annotations.NotNull;

public class MainMenuFragment extends Fragment {

    public MainMenuFragment() {
        super(R.layout.fragment_main_menu);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View createProjectBtn = view.findViewById(R.id.constraints_new_project);
        View loadProjectBtn = view.findViewById(R.id.constraints_load_project);

        createProjectBtn.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(R.id.action_mainMenuFragment_to_createProjectFragment));
        loadProjectBtn.setOnClickListener(v -> Navigation.findNavController(requireActivity(), R.id.nav_host).navigate(R.id.action_mainMenuFragment_to_loadProjectFragment));
    }
}


