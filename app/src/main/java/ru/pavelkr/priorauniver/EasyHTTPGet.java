package ru.pavelkr.priorauniver;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by pavel on 20.04.2017.
 */

public class EasyHTTPGet extends AsyncTask<String, Void, String>{
    public static final String BASE_URL = "https://api.telegram.org/bot356...................."; //здесь секртная ссылка на бота
    private OnCompleteListener listener = null;
    public void setOnCompleteListener(OnCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... targetURL) {
        HttpURLConnection connection = null;
        try {
            //Create connection
            URL url = new URL(targetURL[0]);
            connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    protected void onPostExecute(String res) {
        if (listener != null) {
            listener.onComplete(res);
        }
    }

    public static void easy_send(int chat_id, String mes) {
        EasyHTTPGet g = new EasyHTTPGet();
        try {
            g.execute(EasyHTTPGet.BASE_URL + "sendMessage?chat_id=" + chat_id + "&text=" +
                    URLEncoder.encode(mes, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    interface OnCompleteListener {
        void onComplete(String resp);
    }
}
