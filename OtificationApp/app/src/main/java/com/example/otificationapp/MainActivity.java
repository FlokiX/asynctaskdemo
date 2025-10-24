package com.example.otificationapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextView contentTextView;
    private static final String CHANNEL_ID = "red_app_channel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("App name");

        // Инициализация элементов
        contentTextView = findViewById(R.id.contentTextView);
        MaterialButton editTextButton = findViewById(R.id.editTextButton);
        MaterialButton notificationButton = findViewById(R.id.notificationButton);

        // Обработчики кнопок
        editTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTextDialog();
            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification();
            }
        });

        // Создание канала уведомлений
        createNotificationChannel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit_text) {
            showEditTextDialog();
            return true;
        } else if (id == R.id.action_show_notification) {
            showNotification();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditTextDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        builder.setView(dialogView);

        TextInputEditText dialogEditText = dialogView.findViewById(R.id.dialogEditText);

        // Установка текущего текста в диалог
        String currentText = contentTextView.getText().toString();
        if (!currentText.equals("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut")) {
            dialogEditText.setText(currentText);
        }

        AlertDialog dialog = builder.create();

        // Обработчики кнопок
        dialogView.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = dialogEditText.getText().toString();
                if (!newText.trim().isEmpty()) {
                    contentTextView.setText(newText);
                    Toast.makeText(MainActivity.this, "Текст успішно оновлено!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Введіть текст!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showNotification() {
        String text = contentTextView.getText().toString().trim();

        if (text.equals("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut") || text.isEmpty()) {
            Toast.makeText(this, "Текстове поле порожнє! Спочатку відредагуйте текст.", Toast.LENGTH_LONG).show();
            return;
        }

        // Создание красивого уведомления
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("From my app")
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(getResources().getColor(R.color.primary_red))
                .setAutoCancel(true)
                .setShowWhen(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Проверка разрешения на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                notificationManager.notify(NOTIFICATION_ID, builder.build());
                Toast.makeText(this, "Сповіщення успішно відправлено!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Дозвольте сповіщення для цього додатку!", Toast.LENGTH_LONG).show();
            }
        } else {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
            Toast.makeText(this, "Сповіщення успішно відправлено!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Red App Notifications";
            String description = "Channel for beautiful red app notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(getResources().getColor(R.color.primary_red));
            channel.enableVibration(true);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}