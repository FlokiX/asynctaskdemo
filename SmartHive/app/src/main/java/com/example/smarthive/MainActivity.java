package com.example.smarthive;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnLightOn, btnLightOff, btnClearLog;
    private TextView tvLog;

    private static final String SERVER_IP = "192.168.0.104";
    private static final int SERVER_PORT = 8080;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLightOn = findViewById(R.id.btnLightOn);
        btnLightOff = findViewById(R.id.btnLightOff);
        btnClearLog = findViewById(R.id.btnClearLog);
        tvLog = findViewById(R.id.tvLog);

        // Подключаемся к серверу при запуске приложения
        new ConnectTask().execute();

        btnLightOn.setOnClickListener(v -> sendCommand("ON"));
        btnLightOff.setOnClickListener(v -> sendCommand("OFF"));
        btnClearLog.setOnClickListener(v -> tvLog.setText(""));
    }

    private void connectToServer() {
        new ConnectTask().execute();
    }

    private void sendCommand(String command) {
        if (!isConnected) {
            logMessage("❌ Нет подключения к серверу");
            return;
        }
        new SendCommandTask().execute(command);
    }

    private void logMessage(String message) {
        runOnUiThread(() -> {
            String timeStamp = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            tvLog.append("[" + timeStamp + "] " + message + "\n");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    private void disconnect() {
        if (isConnected) {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isConnected = false;
    }

    // AsyncTask для подключения к серверу
    private class ConnectTask extends AsyncTask<Void, String, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                logMessage("🔄 Подключение к " + SERVER_IP + ":" + SERVER_PORT + "...");
                socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                isConnected = true;
                logMessage("✅ Успешно подключено к серверу");
                new ReceiveMessagesTask().execute();
                return true;
            } catch (IOException e) {
                logMessage("❌ Ошибка подключения: " + e.getMessage());
                return false;
            }
        }
    }

    // AsyncTask для отправки команд
    private class SendCommandTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... commands) {
            String command = commands[0];
            if (out != null) {
                out.println(command);
                logMessage("📤 Отправлено: " + command);
            }
            return null;
        }
    }

    // AsyncTask для приема сообщений от сервера
    private class ReceiveMessagesTask extends AsyncTask<Void, String, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String message;
                while ((message = in.readLine()) != null && isConnected) {
                    publishProgress(message);
                }
            } catch (IOException e) {
                if (isConnected) {
                    publishProgress("❌ Ошибка чтения: " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... messages) {
            for (String message : messages) {
                logMessage("📥 Сервер: " + message);
            }
        }
    }
}
