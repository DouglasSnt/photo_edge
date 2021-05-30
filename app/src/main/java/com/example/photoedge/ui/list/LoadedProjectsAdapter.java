package com.example.photoedge.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoedge.R;
import com.example.photoedge.models.ProjectModel;

import java.util.List;

public class LoadedProjectsAdapter extends RecyclerView.Adapter<LoadProjectItemViewHolder> {

    List<ProjectModel> projectModelList;
    FragmentActivity activity;
    EditProjectListener listener = new EditProjectListener() {
        @Override
        public void onProjectDeleted(int position) {
            projectModelList.remove(position);
            notifyItemRemoved(position);
            if (projectModelList.isEmpty()) {
                activity.findViewById(R.id.project_status_tv).setVisibility(View.VISIBLE);
            }
        }
    };

    public LoadedProjectsAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public LoadProjectItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_info_item, parent, false);
        return new LoadProjectItemViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadProjectItemViewHolder holder, int position) {
        holder.setInfoOnView(projectModelList.get(position), activity, listener);
    }

    @Override
    public int getItemCount() {
        if (projectModelList == null) return 0;
        return projectModelList.size();
    }

    public void setProjectModelList(List<ProjectModel> projectModelList) {
        this.projectModelList = projectModelList;
    }

    interface EditProjectListener {
        void onProjectDeleted(int position);
    }
}
