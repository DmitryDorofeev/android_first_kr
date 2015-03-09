package com.example.uvdoha.translate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

    private void showResult(String result) {
        if (withResult) {
            resultFragment = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.outputFrame);
            if (resultFragment == null) {
                resultFragment = new ResultFragment();
            }
            resultFragment.setResult(result);
            getSupportFragmentManager().beginTransaction().replace(R.id.outputFrame, resultFragment).commit();
        }
        else {
            startActivity(new Intent(MainActivity.this, ResultActivity.class).putExtra("result", result));
        }
    }

    private boolean checkInput(String input) {
        char[] chars = input.trim().toCharArray();
        for (char c : chars) {
            if (Character.isLetter(c)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void translate(String input, String fromLang, String toLang) {
        Log.d(LOG_TAG, "askForTranslate: '" + input + "', from: '" + fromLang + "' to: '" + toLang + "'");
        if (fromLang.equals(toLang)) {
            showResult(input);
        } else if (checkInput(input)) {
            task = new AskForTranslate();
            task.execute(input, fromLang, toLang);
        }
    }

    private class AskForTranslate extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params.length != 3) {
                return null;
            }

            // TODO убрать захардкоженные языки
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

            JSONObject jsonResult = doRequestWithTextAndLang(params[0], lang.toString());
            try {
                if (jsonResult != null && jsonResult.getInt("code") == 200) {
                    JSONArray textArray = jsonResult.getJSONArray("text");
                    String result = textArray.join(" ");
                    result = result.substring(1); // Удаляем первую кавычку
                    result = result.substring(0, result.length()-1); // Удаляем последнюю кавычку
                    result = result.trim(); // Удаляем пробелы вначале и вконце
                    return result;
                } else {
                    publishProgress(getResources().getString(R.string.translate_error));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
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
                showResult(aString);
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

        private JSONObject doRequestWithTextAndLang(String text, String lang) {
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
                        publishProgress(getResources().getString(R.string.connection_error));
                        break;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}


// TODO toast, если не выбран язык
// TODO если поле ввода пустое, содержит только пробелы и переносы строк или языки оригинала и
// перевода одинаковые, лишний запрос на сервер не делать
