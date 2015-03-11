package com.example.uvdoha.translate;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetLanguagesTask extends AsyncTask<String, String, JSONObject> {
    private static final String LOG_TAG = "LOG";
    private ProgressDialog progressDialog;
    private MainActivity mainActivity;

    public GetLanguagesTask(ProgressDialog progressDialog, MainActivity mainActivity) {
        this.progressDialog = progressDialog;
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonResult = doRequestWithLang(params[0]);
        if (jsonResult != null) {
            return jsonResult;
        }

        final String msg = mainActivity.getResources().getString(R.string.get_lang_error);
        Toast.makeText(mainActivity, msg, Toast.LENGTH_SHORT).show();
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        try {
            mainActivity.setLangsPairsAndNames(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
    }

    private JSONObject doRequestWithLang(String lang) {
        // TODO выделить повторяющийся код (см. AskForTranslateTask.doRequestWithTextAndLang())
        try {
            URL url = new URL("https://translate.yandex.net/api/v1.5/tr.json/getLangs" +
                    "?key=trnsl.1.1.20150302T070606Z.ae16c8413b9d2123.03d6ce974ad95e39f0acb8ece6411819c3bab05d" +
                    "&ui=" + lang);
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
