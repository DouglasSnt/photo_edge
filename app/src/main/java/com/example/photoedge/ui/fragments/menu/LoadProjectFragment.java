package com.example.photoedge.ui.fragments.menu;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.helpers.ProjectManager;
import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.activity.MainActivity;
import com.example.photoedge.ui.list.LoadedProjectsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LoadProjectFragment extends Fragment {

    public LoadProjectFragment() {
        super(R.layout.load_project_fragment);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.load_projects_progressbar).setVisibility(View.VISIBLE);

        MainActivity.SHARED_EXECUTOR.execute(() -> {
            FragmentActivity activity = requireActivity();
            RecyclerView projectsRv = view.findViewById(R.id.loaded_projects_recycler_view);
            LoadedProjectsAdapter adapter = new LoadedProjectsAdapter(activity);

            List<ProjectModel> projectList = ProjectManager.loadAllProjects(activity, false);
            activity.runOnUiThread(() -> {
                view.findViewById(R.id.load_projects_progressbar).setVisibility(View.GONE);

                if (projectList.isEmpty()) {
                    view.findViewById(R.id.project_status_tv).setVisibility(View.VISIBLE);
                } else {
                    adapter.setProjectModelList(projectList);
                    projectsRv.setAdapter(adapter);
                    projectsRv.setLayoutManager(new LinearLayoutManager(activity));
                }
            });
        });
    }

}
