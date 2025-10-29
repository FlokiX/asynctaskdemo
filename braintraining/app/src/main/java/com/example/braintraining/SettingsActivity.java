package com.example.braintraining;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private EditText usernameInput;
    private ButtonToggleGroup difficultyGroup;
    private SeekBar trainingTimeSeekBar;
    private TextView trainingTimeText;
    private SwitchMaterial soundSwitch, vibrationSwitch, notificationsSwitch;
    private MaterialButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPrefManager = new SharedPrefManager(this);
        initializeViews();
        loadCurrentSettings();
        setupListeners();
    }

    private void initializeViews() {
        usernameInput = findViewById(R.id.usernameInput);
        difficultyGroup = findViewById(R.id.difficultyGroup);
        trainingTimeSeekBar = findViewById(R.id.trainingTimeSeekBar);
        trainingTimeText = findViewById(R.id.trainingTimeText);
        soundSwitch = findViewById(R.id.soundSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        saveButton = findViewById(R.id.saveButton);

        // Кнопка назад
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }

    private void loadCurrentSettings() {
        // Загрузка текущих настроек
        usernameInput.setText(sharedPrefManager.getUsername());

        // Установка сложности
        String difficulty = sharedPrefManager.getDifficulty();
        switch (difficulty) {
            case "Легкая":
                difficultyGroup.check(R.id.btnEasy);
                break;
            case "Средняя":
                difficultyGroup.check(R.id.btnMedium);
                break;
            case "Сложная":
                difficultyGroup.check(R.id.btnHard);
                break;
        }

        // Время тренировки
        int trainingTime = sharedPrefManager.getTrainingTime();
        trainingTimeSeekBar.setProgress(trainingTime);
        trainingTimeText.setText(trainingTime + " минут");

        // Переключатели
        soundSwitch.setChecked(sharedPrefManager.isSoundEnabled());
        vibrationSwitch.setChecked(sharedPrefManager.isVibrationEnabled());
    }

    private void setupListeners() {
        // Слушатель для SeekBar
        trainingTimeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                trainingTimeText.setText(progress + " минут");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Кнопка сохранения
        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty()) {
            username = "Игрок";
        }

        String difficulty = "Средняя";
        int checkedId = difficultyGroup.getCheckedButtonId();
        if (checkedId == R.id.btnEasy) {
            difficulty = "Легкая";
        } else if (checkedId == R.id.btnMedium) {
            difficulty = "Средняя";
        } else if (checkedId == R.id.btnHard) {
            difficulty = "Сложная";
        }

        int trainingTime = trainingTimeSeekBar.getProgress();
        boolean soundEnabled = soundSwitch.isChecked();
        boolean vibrationEnabled = vibrationSwitch.isChecked();
        boolean notificationsEnabled = notificationsSwitch.isChecked();

        // Сохранение настроек
        sharedPrefManager.saveSettings(username, difficulty, soundEnabled, vibrationEnabled, trainingTime);

        Toast.makeText(this, "Настройки сохранены!", Toast.LENGTH_SHORT).show();
        finish();
    }
}