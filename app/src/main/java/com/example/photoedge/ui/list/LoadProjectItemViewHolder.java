package com.example.photoedge.ui.list;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.helpers.ProjectManager;
import com.example.photoedge.models.ProjectModel;
import com.example.photoedge.ui.activity.MainActivity;
import com.example.photoedge.ui.viewmodels.ProjectViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LoadProjectItemViewHolder extends RecyclerView.ViewHolder {

    View view;

    public LoadProjectItemViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
    }

    public void setInfoOnView(ProjectModel projectModel, FragmentActivity activity, LoadedProjectsAdapter.EditProjectListener listener) {


        TextView nameTv = view.findViewById(R.id.project_name_info);
        TextView resolutionTv = view.findViewById(R.id.project_resolution_info);
        TextView creationDate = view.findViewById(R.id.project_date_info);
        ImageView preview = view.findViewById(R.id.project_preview);
        AppCompatImageButton deleteBtn = view.findViewById(R.id.delete_project_btn);

        MainActivity.SHARED_EXECUTOR.execute(() -> {
            Bitmap thumbnail = ProjectManager.getThumbnail(activity, projectModel.getProjectName());
            if (thumbnail != null) activity.runOnUiThread(() -> preview.setImageBitmap(thumbnail));
        });

        String creationTimestamp = projectModel.getCreationDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String date = formatter.format(new Date(Long.parseLong(creationTimestamp)));

        String resolution = projectModel.getWidth() + "x" + projectModel.getHeight();

        nameTv.setText(projectModel.getProjectName());
        resolutionTv.setText(resolution);
        creationDate.setText(date);

        deleteBtn.setOnClickListener(v -> showConfirmDeleteDialog(activity, projectModel, listener));

        preview.setOnClickListener(v -> {
            System.currentTimeMillis();
            ProjectViewModel model = new ViewModelProvider(activity).get(ProjectViewModel.class);
            ProjectModel projectModelFull = ProjectManager.loadProjectByName(activity, projectModel.getProjectName(), true);
            activity.runOnUiThread(() -> {
                model.getProjectModel().setValue(projectModelFull);
                Navigation.findNavController(activity, R.id.nav_host).navigate(R.id.action_loadProjectFragment_to_canvasRootFragment);
            });
        });

    }

    public void showConfirmDeleteDialog(Activity activity, ProjectModel projectModel, LoadedProjectsAdapter.EditProjectListener listener) {
        new AlertDialog.Builder(activity, R.style.Theme_PhotoEdge_Dialog)
                .setMessage("Deseja mesmo deletar o projeto \"" + projectModel.getProjectName() + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    ProjectManager.deleteProject(activity, projectModel);
                    listener.onProjectDeleted(getAbsoluteAdapterPosition());
                })
                .setNegativeButton("Não", null)
                .show();
    }


}







