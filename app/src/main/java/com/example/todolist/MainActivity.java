package com.example.todolist;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DBhelper db;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    ArrayList<Task> list;
    FloatingActionButton fab;

    private static final String CHANNEL_ID = "todo_channel"; // Notification channel ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DBhelper(this);
        recyclerView = findViewById(R.id.recyclerViewTasks);
        fab = findViewById(R.id.fabAddTask);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createNotificationChannel();

        loadTasks();

        fab.setOnClickListener(v -> showAddDialog());
    }


    private void loadTasks() {
        list = db.getAllTasks();
        adapter = new TaskAdapter(this, list, db);
        recyclerView.setAdapter(adapter);
    }


    public void showAddDialog() {
        showTaskDialog(null);
    }


    public void showEditDialog(Task task) {
        showTaskDialog(task);
    }


    private void showTaskDialog(Task existingTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        builder.setView(view);

        EditText etTitle = view.findViewById(R.id.etTitle);
        EditText etDesc = view.findViewById(R.id.etDescription);
        Spinner spPriority = view.findViewById(R.id.spPriority);
        Button btnDate = view.findViewById(R.id.btnDueDate);
        TextView tvDate = view.findViewById(R.id.tvSelectedDate);
        Button btnSave = view.findViewById(R.id.btnSaveTask);


        if (existingTask != null) {
            etTitle.setText(existingTask.getTitle());
            etDesc.setText(existingTask.getDescription());
            tvDate.setText(existingTask.getDueDate());
        }

        final Calendar cal = Calendar.getInstance();
        btnDate.setOnClickListener(v -> {
            DatePickerDialog picker = new DatePickerDialog(MainActivity.this,
                    (view1, y, m, d) -> tvDate.setText(d + "/" + (m + 1) + "/" + y),
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            picker.show();
        });

        AlertDialog dialog = builder.create();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String date = tvDate.getText().toString().trim();
            String priority = spPriority.getSelectedItem().toString();

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingTask == null) {

                db.addTask(new Task(0, title, desc, date, priority, false));
                Toast.makeText(this, "Task added", Toast.LENGTH_SHORT).show();
                showNotification("New Task Added", "Task: " + title);
            } else {

                Task updated = new Task(existingTask.getId(), title, desc, date, priority, existingTask.isCompleted());
                db.updateTask(updated);
                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                showNotification("Task Updated", "Task: " + title);
            }

            loadTasks();
            dialog.dismiss();
        });

        dialog.show();
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ToDo Notifications";
            String description = "Channel for ToDo task alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
