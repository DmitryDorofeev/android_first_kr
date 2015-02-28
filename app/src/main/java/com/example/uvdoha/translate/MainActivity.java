package com.example.uvdoha.translate;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    String[] languages = {"Русский", "Английский"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinnerFrom = (Spinner) findViewById(R.id.spinnerFromLang);
        spinnerFrom.setAdapter(adapter);
        spinnerFrom.setPrompt(getString(R.string.select_original_lang));
        spinnerFrom.setSelection(0);

        Spinner spinnerTo = (Spinner) findViewById(R.id.spinnerToLang);
        spinnerTo.setAdapter(adapter);
        spinnerTo.setPrompt(getString(R.string.select_result_lang));
        spinnerTo.setSelection(1);

        Button buttonTranslate = (Button) findViewById(R.id.buttonTranslate);
        buttonTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView inputField = (TextView) findViewById(R.id.editTextInput);
                String originalText = inputField.getText().toString();

                String translatedText = translate(originalText);

                TextView outputField =  (TextView) findViewById(R.id.editTextResult);
                outputField.setText(translatedText);
            }
        });
    }

    String translate(String originalText) {
        // TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)
        // TODO магия
        return "42";
    }
}

// TODO при длинном тексте элементы едут за экран и становятся недоступными