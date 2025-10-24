package com.example.asynctaskdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity implements MyAsyncTask.TaskListener {

    private TextInputEditText iterationsEditText;
    private TextInputEditText delayEditText;
    private TextView statusTextView;
    private LinearProgressIndicator progressBar;
    private TextView progressTextView;
    private TextView logTextView;
    private MaterialButton startStopButton;
    private MaterialButton cancelButton;

    private MyAsyncTask asyncTask;
    private boolean isTaskRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        iterationsEditText = findViewById(R.id.iterationsEditText);
        delayEditText = findViewById(R.id.delayEditText);
        statusTextView = findViewById(R.id.statusTextView);
        progressBar = findViewById(R.id.progressBar);
        progressTextView = findViewById(R.id.progressTextView);
        logTextView = findViewById(R.id.logTextView);
        startStopButton = findViewById(R.id.startStopButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupClickListeners() {
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTaskRunning) {
                    startTask();
                } else {
                    stopTask();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (asyncTask != null) {
                    if (asyncTask.isPaused()) {
                        resumeTask();
                    } else {
                        pauseTask();
                    }
                }
            }
        });
    }

    private void startTask() {
        try {
            int iterations = Integer.parseInt(iterationsEditText.getText().toString());
            int delay = Integer.parseInt(delayEditText.getText().toString());

            if (iterations <= 0 || delay <= 0) {
                addToLog("Ошибка: параметры должны быть положительными числами");
                return;
            }

            asyncTask = new MyAsyncTask(this);
            asyncTask.execute(iterations, delay);

            isTaskRunning = true;
            updateUIForRunningState();
        } catch (NumberFormatException e) {
            addToLog("Ошибка: введите корректные числа");
        }
    }

    private void stopTask() {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    private void pauseTask() {
        if (asyncTask != null) {
            asyncTask.pauseTask();
        }
    }

    private void resumeTask() {
        if (asyncTask != null) {
            asyncTask.pauseTask(); // toggle pause/resume
        }
    }

    private void updateUIForRunningState() {
        startStopButton.setText("Stop");
        startStopButton.setIconResource(R.drawable.ic_stop);
        cancelButton.setEnabled(true);
        cancelButton.setText("Pause");
        cancelButton.setIconResource(R.drawable.ic_pause);
    }

    private void updateUIForStoppedState() {
        startStopButton.setText("Start");
        startStopButton.setIconResource(R.drawable.ic_play);
        cancelButton.setEnabled(false);
        cancelButton.setText("Pause");
        isTaskRunning = false;
    }

    private void updateUIForPausedState() {
        cancelButton.setText("Resume");
        cancelButton.setIconResource(R.drawable.ic_play);
    }

    private void updateUIForResumedState() {
        cancelButton.setText("Pause");
        cancelButton.setIconResource(R.drawable.ic_pause);
    }

    private void addToLog(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String currentText = logTextView.getText().toString();
                String timestamp = java.text.DateFormat.getTimeInstance().format(new java.util.Date());
                String newText = currentText + "\n[" + timestamp + "] " + message;
                logTextView.setText(newText);

                // Прокручиваем до конца
                final int scrollAmount = logTextView.getLayout().getLineTop(logTextView.getLineCount()) - logTextView.getHeight();
                if (scrollAmount > 0) {
                    logTextView.scrollTo(0, scrollAmount);
                }
            }
        });
    }


    @Override
    public void onTaskStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("Running");
                statusTextView.setTextColor(getColor(R.color.colorPrimary));
                progressBar.setProgress(0);
                progressTextView.setText("Прогресс: 0%");
            }
        });
    }

    @Override
    public void onTaskProgress(int progress, int currentIteration, int totalIterations) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
                progressTextView.setText(String.format("Прогресс: %d%% (%d/%d)",
                        progress, currentIteration, totalIterations));
            }
        });
    }

    @Override
    public void onTaskPaused() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("Paused");
                statusTextView.setTextColor(getColor(android.R.color.holo_orange_dark));
                updateUIForPausedState();
            }
        });
    }

    @Override
    public void onTaskResumed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("Running");
                statusTextView.setTextColor(getColor(R.color.colorPrimary));
                updateUIForResumedState();
            }
        });
    }

    @Override
    public void onTaskCompleted(String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("Completed");
                statusTextView.setTextColor(getColor(android.R.color.holo_green_dark));
                progressBar.setProgress(100);
                progressTextView.setText("Прогресс: 100%");
                updateUIForStoppedState();
            }
        });
    }

    @Override
    public void onTaskCancelled(String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText("Cancelled");
                statusTextView.setTextColor(getColor(android.R.color.holo_red_dark));
                updateUIForStoppedState();
            }
        });
    }

    @Override
    public void onTaskLog(String message) {
        addToLog(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asyncTask != null && !asyncTask.isCancelled()) {
            asyncTask.cancel(true);
        }
    }
}