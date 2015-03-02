package com.example.uvdoha.translate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private static final int SELECT_FROM_CODE = 1;
    private static final int SELECT_TO_CODE = 2;
    private ProgressDialog progressDialog;
    private TextView outputField;
    private TranslateTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTranslate = (Button) findViewById(R.id.buttonTranslate);
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView inputField = (TextView) findViewById(R.id.editTextInput);
                String originalText = inputField.getText().toString();
                task = new TranslateTask();
                task.execute(originalText);
            }
        });

        Button selectFromButton = (Button) findViewById(R.id.select_from_button);
        selectFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectLanguageActivity.class);
                startActivityForResult(intent, SELECT_FROM_CODE);
            }
        });

        Button selectToButton = (Button) findViewById(R.id.select_to_button);
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
        int textViewId = 0;
        if (requestCode == SELECT_FROM_CODE) {
            textViewId = R.id.fromLangText;
        } else if (requestCode == SELECT_TO_CODE) {
            textViewId = R.id.toLangText;
        }
        TextView text = (TextView) findViewById(textViewId);
        text.setText(data.getStringExtra("language"));
    }


    class TranslateTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String originalText = "";
            if (params.length > 0) {
                originalText = params[0];
            }

            // TODO перевод

            return "42";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            outputField.setText(result);
            progressDialog.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getApplicationContext(), "Отменено", Toast.LENGTH_SHORT).show();
        }
    }
}


// TODO при длинном тексте элементы едут за экран и становятся недоступными
// TODO toast, если не выбран язык
// TODO если поле ввода пустое, содержит только пробелы и переносы строк или языки оригинала и
// перевода одинаковые, лишний запрос на сервер не делать
// TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)