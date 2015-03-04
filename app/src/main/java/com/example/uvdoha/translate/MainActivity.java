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


public class MainActivity extends FragmentActivity {
    private static final String LOG_TAG = "LOG";
    private static final int SELECT_FROM_CODE = 1;
    private static final int SELECT_TO_CODE = 2;
    private ProgressDialog progressDialog;
    private TextView outputField;
    private AskForTranslate task;

    EditText editTextInput;
    EditText editTextResult;
    Button selectFromButton;
    Button selectToButton;
    ResultFragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTranslate = (Button) findViewById(R.id.buttonTranslate);
        selectFromButton = (Button) findViewById(R.id.select_from_button);
        selectToButton = (Button) findViewById(R.id.select_to_button);
        editTextInput = (EditText) findViewById(R.id.editTextInput);
        editTextResult = (EditText) findViewById(R.id.editTextResult);

        resultFragment = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.result);
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askForTranslate(editTextInput.getText().toString());
            }
        });

        selectFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, SELECT_FROM_CODE);
            }
        });

        selectToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, SELECT_TO_CODE);
            }
        });

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

        outputField = (TextView) findViewById(R.id.editTextResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_FROM_CODE:
                    selectFromButton.setText(data.getStringExtra("language"));
                    break;
                case SELECT_TO_CODE:
                    selectToButton.setText(data.getStringExtra("language"));
                    break;
            }
        }
    }

    void askForTranslate(String textToTranslate) {
        // TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)
        Log.d(LOG_TAG, "askForTranslate: '" + textToTranslate + "'");
        task = new AskForTranslate();
        task.execute(textToTranslate);
    }

    private class AskForTranslate extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder lang = new StringBuilder();
            if (selectFromButton.getText().toString().equals(getResources().getString(R.string.ru))) {
                lang.append("ru");
            }
            else if (selectFromButton.getText().toString().equals(getResources().getString(R.string.en))) {
                lang.append("en");
            }
            lang.append("-");
            if (selectToButton.getText().toString().equals(getResources().getString(R.string.ru))) {
                lang.append("ru");
            }
            else if (selectToButton.getText().toString().equals(getResources().getString(R.string.en))) {
                lang.append("en");
            }

            JSONObject jsonResult = null;
            String result = null;
            if (params.length > 0) {
                jsonResult = doRequestWithTextAndLang(params[0], lang.toString());
            }
            try {
                if (jsonResult != null && jsonResult.getInt("code") == 200) {
                    JSONArray textArray = jsonResult.getJSONArray("text");
                    result = textArray.join(" ");
                }
                else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.translate_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String aString) {
            super.onPostExecute(aString);
            setResult(aString);
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(LOG_TAG, getResources().getString(R.string.translation_cancelled));
            // close Dialog
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
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void setResult(String result) {
        resultFragment.setResult(result);
    }
}


// TODO при длинном тексте элементы едут за экран и становятся недоступными
// TODO toast, если не выбран язык
// TODO если поле ввода пустое, содержит только пробелы и переносы строк или языки оригинала и
// перевода одинаковые, лишний запрос на сервер не делать
// TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)