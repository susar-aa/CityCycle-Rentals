package com.example.citycyclerentals.utils;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FetchData extends AsyncTask<Void, Void, String> {
    private static final String TAG = "FetchData";

    private String url;
    private String requestMethod;
    private Map<String, String> postData;
    private OnDataFetchedListener listener;

    // Interface for handling the response
    public interface OnDataFetchedListener {
        void onResponse(String response);
    }

    // Constructor for GET request
    public FetchData(String url, OnDataFetchedListener listener) {
        this.url = url;
        this.listener = listener;
        this.requestMethod = "GET";
    }

    // Constructor for POST request
    public FetchData(String url, Map<String, String> postData, OnDataFetchedListener listener) {
        this.url = url;
        this.postData = postData;
        this.listener = listener;
        this.requestMethod = "POST";
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod(requestMethod);

            if ("POST".equalsIgnoreCase(requestMethod) && postData != null) {
                conn.setDoOutput(true);
                StringBuilder postDataString = new StringBuilder();
                for (Map.Entry<String, String> entry : postData.entrySet()) {
                    if (postDataString.length() != 0) postDataString.append('&');
                    postDataString.append(entry.getKey()).append('=').append(entry.getValue());
                }
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = postDataString.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } else {
                Log.e(TAG, "HTTP error code: " + responseCode);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception fetching data", e);
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    Log.e(TAG, "Exception closing reader", e);
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onResponse(result);
        }
    }
}