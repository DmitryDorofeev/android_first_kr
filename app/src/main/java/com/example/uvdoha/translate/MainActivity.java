package com.example.uvdoha.translate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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


public class MainActivity extends FragmentActivity implements InputFragment.onTranslateSucceedListener {
    private static final String LOG_TAG = "LOG";
    private ProgressDialog progressDialog;
    private AskForTranslate task;

    private boolean withResult;

    ResultFragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        withResult = (findViewById(R.id.outputFrame) != null);
        if (withResult) {
            showResult("");
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.wait_for_translate));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.cancel(true);
            }
        });
    }

    void showResult(String result) {
        if (withResult) {
            resultFragment = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.outputFrame);
            if (resultFragment == null) {
                resultFragment = new ResultFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.outputFrame, resultFragment).commit();
            }
            resultFragment.setResult(result);
        }
        else {
            startActivity(new Intent(MainActivity.this, ResultActivity.class).putExtra("result", result));
        }
    }

    @Override
    public void translate(String input, String fromLang, String toLang) {
        // TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)
        Log.d(LOG_TAG, "askForTranslate: '" + input+ "', from: '" + fromLang + "' to: '" + toLang + "'");
        task = new AskForTranslate();
        task.execute(input, fromLang, toLang);
    }

    private class AskForTranslate extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            if (params.length == 3) {
                StringBuilder lang = new StringBuilder();
                if (params[1].equals(getResources().getString(R.string.ru))) {
                    lang.append("ru");
                } else if (params[1].equals(getResources().getString(R.string.en))) {
                    lang.append("en");
                }
                lang.append("-");
                if (params[2].equals(getResources().getString(R.string.ru))) {
                    lang.append("ru");
                } else if (params[2].equals(getResources().getString(R.string.en))) {
                    lang.append("en");
                }

                JSONObject jsonResult = null;
                if (params.length > 0) {
                    jsonResult = doRequestWithTextAndLang(params[0], lang.toString());
                }
                try {
                    if (jsonResult != null && jsonResult.getInt("code") == 200) {
                        JSONArray textArray = jsonResult.getJSONArray("text");
                        result = textArray.join(" ");
                    } else {
                        publishProgress(getResources().getString(R.string.translate_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (values.length > 0) {
                Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            if (aString != null) {
                setResult(aString);
            }
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(LOG_TAG, getResources().getString(R.string.translation_cancelled));
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, getResources().getString(R.string.translation_cancelled), Toast.LENGTH_SHORT).show();
        }

        JSONObject doRequestWithTextAndLang(String text, String lang) {
            String resultJson;
            StringBuilder buf = new StringBuilder();
            String line;
            JSONObject json = null;
            try {
                URL url = new URL(getResources().getString(R.string.apiURL) +
                        "?key=" + getResources().getString(R.string.apiKey) +
                        "&text=" + URLEncoder.encode(text, "utf-8") +
                        "&lang=" + lang);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                int responseCode = httpsURLConnection.getResponseCode();

                switch (responseCode) {
                    case HttpsURLConnection.HTTP_OK:
                        try {
                            InputStream in = new BufferedInputStream(httpsURLConnection.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            while ((line = reader.readLine()) != null) {
                                buf.append(line);
                            }
                            resultJson = buf.toString();
                            json = new JSONObject(resultJson);
                            Log.d(LOG_TAG, "Result = " + resultJson);
                        }
                        finally {
                            httpsURLConnection.disconnect();
                        }
                        break;
                    default:
                        publishProgress(getResources().getString(R.string.connection_error));
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void setResult(String result) {
        showResult(result);
    }
}


// TODO при длинном тексте элементы едут за экран и становятся недоступными
// TODO toast, если не выбран язык
// TODO если поле ввода пустое, содержит только пробелы и переносы строк или языки оригинала и
// перевода одинаковые, лишний запрос на сервер не делать
// TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)