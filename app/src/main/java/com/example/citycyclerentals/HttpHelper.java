package com.example.citycyclerentals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpHelper {
    public static String sendPostRequest(String requestURL, HashMap<String, String> postDataParams) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Prepare data
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : postDataParams.entrySet()) {
                if (postData.length() != 0) postData.append("&");
                postData.append(entry.getKey()).append("=").append(entry.getValue());
            }

            // Send data
            OutputStream os = conn.getOutputStream();
            os.write(postData.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            // Read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return result.toString();
    }
}
