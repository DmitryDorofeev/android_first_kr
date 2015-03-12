package com.example.uvdoha.translate;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;


public class AskForTranslateTask extends AsyncTask<String, String, String> {
    private ProgressDialog progressDialog;
    private static final String LOG_TAG = "LOG";
    private MainActivity mainActivity;

    public AskForTranslateTask(ProgressDialog progressDialog, MainActivity mainActivity) {
        this.progressDialog = progressDialog;
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        // params[0] - оригинальный текст
        // params[1] - код языка оригинала
        // params[2] - код языка перевода

        if (params.length != 3) {
            return null;
        }
        Log.d(LOG_TAG, "params[0]: " + params[0]);
        Log.d(LOG_TAG, "params[1]: " + params[1]);
        Log.d(LOG_TAG, "params[2]: " + params[2]);

        JSONObject jsonResult = doRequestWithTextAndLang(params[0], params[1] + "-" + params[2]);
        try {
            if (jsonResult != null && jsonResult.getInt("code") == HttpsURLConnection.HTTP_OK) {
                JSONArray textArray = jsonResult.getJSONArray("text");
                String result = textArray.join(" ");
                result = result.substring(1); // Удаляем первую кавычку
                result = result.substring(0, result.length() - 1); // Удаляем последнюю кавычку
                result = result.trim(); // Удаляем пробелы вначале и вконце
                return result;
            } else {
                final String msg = mainActivity.getResources().getString(R.string.translate_error);
                Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String aString) {
        super.onPostExecute(aString);
        if (aString != null) {
            mainActivity.showResult(aString);
        }
        progressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(LOG_TAG, mainActivity.getResources().getString(R.string.translation_cancelled));
        progressDialog.dismiss();
        Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.translation_cancelled),
                Toast.LENGTH_SHORT).show();
    }

    private JSONObject doRequestWithTextAndLang(String text, String lang) {
        // TODO проверять текст на всякую ерунду. Это ж дырка в безопасности
        try {
            URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/translate" +
                    "?key=trnsl.1.1.20150302T070606Z.ae16c8413b9d2123.03d6ce974ad95e39f0acb8ece6411819c3bab05d" +
                    "&text=" + URLEncoder.encode(text, "utf-8") +
                    "&lang=" + lang);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            final int responseCode = httpsURLConnection.getResponseCode();

            switch (responseCode) {
                case HttpsURLConnection.HTTP_OK:
                    try {
                        InputStream in = new BufferedInputStream(httpsURLConnection.getInputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder buf = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buf.append(line);
                        }
                        String resultJson = buf.toString();
                        Log.d(LOG_TAG, "Result = " + resultJson);
                        return new JSONObject(resultJson);
                    }
                    finally {
                        httpsURLConnection.disconnect();
                    }
                default:
                    final String msg = mainActivity.getResources().getString(R.string.connection_error);
                    Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
