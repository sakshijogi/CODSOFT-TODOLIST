package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private ArrayList<Task> list;
    private Context context;
    private DBhelper db;

    public TaskAdapter(Context ctx, ArrayList<Task> list, DBhelper db) {
        this.context = ctx;
        this.list = list;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Task task = list.get(pos);
        h.tvTitle.setText(task.getTitle());
        h.tvDesc.setText(task.getDescription());
        h.tvDate.setText("Due: " + task.getDueDate());
        h.cbCompleted.setChecked(task.isCompleted());

        h.cbCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            db.updateTask(task);
        });

        h.btnDelete.setOnClickListener(v -> {
            db.deleteTask(task.getId());
            list.remove(pos);
            notifyItemRemoved(pos);
        });

        h.btnEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showEditDialog(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        CheckBox cbCompleted;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvTaskDescription);
            tvDate = itemView.findViewById(R.id.tvTaskDate);
            cbCompleted = itemView.findViewById(R.id.cbCompleted);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
