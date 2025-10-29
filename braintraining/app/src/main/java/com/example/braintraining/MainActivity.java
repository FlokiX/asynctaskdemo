package com.example.braintraining;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefManager = new SharedPrefManager(this);
        databaseHelper = new DatabaseHelper(this);

        initializeViews();
        updateStatistics();
    }

    private void initializeViews() {
        // Настройка приветствия
        TextView welcomeText = findViewById(R.id.welcomeText);
        String username = sharedPrefManager.getUsername();
        welcomeText.setText("Привет, " + username + "!");

        // Карточки игр
        CardView cardMath = findViewById(R.id.cardMath);
        CardView cardPictograms = findViewById(R.id.cardPictograms);
        CardView cardMemory = findViewById(R.id.cardMemory);
        CardView cardResults = findViewById(R.id.cardResults);

        cardMath.setOnClickListener(v -> startGame(GameType.ARITHMETIC));
        cardPictograms.setOnClickListener(v -> startGame(GameType.PICTOGRAMS));
        cardMemory.setOnClickListener(v -> startGame(GameType.MEMORY));
        cardResults.setOnClickListener(v -> startActivity(new Intent(this, ResultsActivity.class)));

        // Кнопка настроек
        findViewById(R.id.settingsButton).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void startGame(GameType gameType) {
        Intent intent = new Intent(this, TrainingActivity.class);
        intent.putExtra("GAME_TYPE", gameType.name());
        startActivity(intent);
    }

    private void updateStatistics() {
        // Обновление статистики
        TextView totalGamesText = findViewById(R.id.totalGamesText);
        TextView averageScoreText = findViewById(R.id.averageScoreText);
        TextView bestScoreText = findViewById(R.id.bestScoreText);

        // Здесь будет логика расчета статистики из базы данных
        totalGamesText.setText("25");
        averageScoreText.setText("87%");
        bestScoreText.setText("100%");
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
    }
}