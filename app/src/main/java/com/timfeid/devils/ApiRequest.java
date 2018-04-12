package com.timfeid.devils;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

class ApiRequest extends ObservableThread {
    String baseUrl = "https://statsapi.web.nhl.com/api/v1/";
    List<Pair<String, String>> params = new ArrayList<Pair<String, String>>();
    StringBuilder output;
    String endpoint;

    public void addParam(String key, String value) {
        params.add(Pair.create(key, value));
    }

    public ApiRequest(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getOutput() {
        return output.toString();
    }

    public String buildQuery() {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair pair : params)
        {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            try {

                result.append(URLEncoder.encode((String) pair.first, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode((String) pair.second, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                // handle this bitch
            }
        }
        return result.toString();
    }

    @Override
    public void doWork() {
        output =  new StringBuilder();
        String lineOutput;
        Log.d("GETTING_URL", buildUrl(endpoint));
        HttpURLConnection connection = null;
        BufferedReader br = null;
        try {
            URL url = new URL(buildUrl(endpoint));
            connection = (HttpURLConnection) url
                    .openConnection();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }

            while ((lineOutput = br.readLine()) != null) {
                output.append(lineOutput);
            }

            Log.d("YOYO", output.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String buildUrl(String endpoint) {
        return this.baseUrl + endpoint + "?" + this.buildQuery();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getEndpoint() {
        return endpoint;
    }
}