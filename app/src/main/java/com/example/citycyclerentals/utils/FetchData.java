package com.example.citycyclerentals.utils;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchData extends AsyncTask<Void, Void, String> {
    private String url;
    private OnDataFetchedListener listener;

    // Interface for handling the response
    public interface OnDataFetchedListener {
        void onResponse(String response);
    }

    // Constructor
    public FetchData(String url, OnDataFetchedListener listener) {
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onResponse(result);
        }
    }
}
