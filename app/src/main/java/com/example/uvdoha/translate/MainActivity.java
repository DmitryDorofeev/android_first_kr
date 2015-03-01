package com.example.uvdoha.translate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    private static final int SELECT_FROM_CODE = 1;
    private static final int SELECT_TO_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonTranslate = (Button) findViewById(R.id.buttonTranslate);

        Button selectFromButton = (Button) findViewById(R.id.select_from_button);
        Button selectToButton = (Button) findViewById(R.id.select_to_button);

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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FROM_CODE) {
            TextView fromLang = (TextView) findViewById(R.id.fromLangText);
            fromLang.setText(data.getStringExtra("language"));
        }
        if (requestCode == SELECT_TO_CODE) {
            TextView toLang = (TextView) findViewById(R.id.toLangText);
            toLang.setText(data.getStringExtra("language"));
        }
    }

    String translate(String originalText) {
        // TODO передавать языки оригинала и перевода. подумать, в каком виде хранить (енамы, константы, строки)
        // TODO магия
        return "42";
    }
}

// TODO при длинном тексте элементы едут за экран и становятся недоступными