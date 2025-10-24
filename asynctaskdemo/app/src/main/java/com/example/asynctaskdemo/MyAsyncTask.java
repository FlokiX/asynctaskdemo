package com.example.asynctaskdemo;

import android.os.AsyncTask;
import android.util.Log;

public class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

    public interface TaskListener {
        void onTaskStarted();
        void onTaskProgress(int progress, int currentIteration, int totalIterations);
        void onTaskPaused();
        void onTaskResumed();
        void onTaskCompleted(String result);
        void onTaskCancelled(String result);
        void onTaskLog(String message);
    }

    private volatile boolean isPaused = false;
    private int totalIterations;
    private int delay;
    private TaskListener listener;

    public MyAsyncTask(TaskListener listener) {
        this.listener = listener;
    }

    public void pauseTask() {
        isPaused = !isPaused;
        if (isPaused) {
            publishProgress(-1);
        } else {
            publishProgress(-2);
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (listener != null) {
            listener.onTaskStarted();
            listener.onTaskLog("AsyncTask: onPreExecute() - подготовка к выполнению");
        }
    }

    @Override
    protected String doInBackground(Integer... params) {
        totalIterations = params[0];
        delay = params[1];

        if (listener != null) {
            listener.onTaskLog("AsyncTask: doInBackground() - начато выполнение");
            listener.onTaskLog("Параметры: " + totalIterations + " итераций, задержка " + delay + "мс");
        }

        for (int i = 0; i < totalIterations; i++) {
            if (isCancelled()) {
                return "Задача отменена пользователем";
            }

            while (isPaused && !isCancelled()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return "Задача прервана";
                }
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return "Задача прервана";
            }

            // Публикуем прогресс
            int progress = (i + 1) * 100 / totalIterations;
            publishProgress(progress, i + 1);
        }

        return "Задача завершена успешно";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if (values[0] == -1) {
            if (listener != null) {
                listener.onTaskPaused();
            }
            return;
        } else if (values[0] == -2) {
            if (listener != null) {
                listener.onTaskResumed();
            }
            return;
        }

        int progress = values[0];
        int currentIteration = values[1];

        if (listener != null) {
            listener.onTaskProgress(progress, currentIteration, totalIterations);

            if (currentIteration % 10 == 0) {
                listener.onTaskLog("Выполнено итераций: " + currentIteration + "/" + totalIterations);
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onTaskCompleted(result);
            listener.onTaskLog("AsyncTask: onPostExecute() - " + result);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (listener != null) {
            listener.onTaskCancelled("Задача отменена");
        }
    }

    @Override
    protected void onCancelled(String result) {
        super.onCancelled(result);
        if (listener != null) {
            listener.onTaskCancelled(result);
        }
    }
}