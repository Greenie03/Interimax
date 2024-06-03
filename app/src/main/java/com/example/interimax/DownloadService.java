package com.example.interimax;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DownloadService extends Service {

    public interface OnServiceFinishedListener {
        void onServiceFinished();
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private OnServiceFinishedListener listener;
    public static final String ACTION_SERVICE_FINISHED = "ACTION_SERVICE_FINISHED";
    public static final String EXTRA_DOWNLOAD_URL = "download_url";
    public static final String FILE_NAME = "download_url";

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            String downloadUrl = ((Map<String, String>) msg.obj).get("downloadUrl");
            String filename = ((Map<String, String>) msg.obj).get("file_name");

            if (downloadUrl == null || downloadUrl.isEmpty()) {
                Log.e("DownloadService", "Invalid URL");
                stopSelf(msg.arg1);
                return;
            }

            Toast.makeText(getBaseContext(), "Starting download", Toast.LENGTH_SHORT).show();

            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                String contentType = conn.getContentType();
                String fileExtension = getFileExtension(contentType);
                InputStream inputStream = conn.getInputStream();

                byte[] buffer = new byte[1024];
                int n;
                File externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!externalStorage.exists()) {
                    externalStorage.mkdirs();
                }
                File outputFile = new File(externalStorage, filename + fileExtension);
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                while ((n = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, n);
                }

                outputStream.close();
                inputStream.close();
                conn.disconnect();

                Log.d("DownloadService", "File downloaded: " + downloadUrl + fileExtension);
            } catch (Exception e) {
                Log.e("DownloadService", "Error: " + e.getMessage(), e);
            }

            stopSelf(msg.arg1);
        }
    }

    private String getFileExtension(String contentType) {
        if (contentType == null) {
            return "";
        }

        switch (contentType) {
            case "application/json":
                return ".json";
            case "application/pdf":
                return ".pdf";
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            default:
                return "";
        }
    }

    public DownloadService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments", THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String downloadUrl = intent.getStringExtra(EXTRA_DOWNLOAD_URL);
        String filename = intent.getStringExtra(FILE_NAME);
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        Map<String, String> map = new HashMap<>();
        map.put("downloadUrl", downloadUrl);
        map.put("file_name", filename);
        msg.obj = map;
        mServiceHandler.sendMessage(msg);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getBaseContext(), "Download completed", Toast.LENGTH_SHORT).show();
        Intent broadcastIntent = new Intent(ACTION_SERVICE_FINISHED);
        sendBroadcast(broadcastIntent);
    }

    public void setOnServiceFinishedListener(OnServiceFinishedListener listener) {
        this.listener = listener;
    }
}