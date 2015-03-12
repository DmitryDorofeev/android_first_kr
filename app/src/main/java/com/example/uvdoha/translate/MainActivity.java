package com.example.uvdoha.translate;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends FragmentActivity implements InputFragment.onTranslateSucceedListener {
    private static final String LOG_TAG = "LOG";
    private AskForTranslateTask translateTask;
    private ProgressDialog translateProgressDialog;
    private GetLanguagesTask getLanguagesTask;
    private boolean withResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        withResult = (findViewById(R.id.outputFrame) != null);
        if (withResult) {
            showResult("");
        }

        translateProgressDialog = new ProgressDialog(this);
        translateProgressDialog.setMessage(getString(R.string.wait_for_translate));
        translateProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        translateProgressDialog.setIndeterminate(true);
        translateProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                translateTask.cancel(true);
                getLanguagesTask.cancel(true);
            }
        });

        ProgressDialog getLangsProgressDialog = new ProgressDialog(this);
        getLangsProgressDialog.setMessage(getString(R.string.wait_for_getting_langs));
        getLangsProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        getLangsProgressDialog.setIndeterminate(true);
        getLanguagesTask = new GetLanguagesTask(getLangsProgressDialog, this);
        getLanguagesTask.execute("ru");
    }

    public void showResult(String result) {
        if (withResult) {
            ResultFragment resultFragment = (ResultFragment) getSupportFragmentManager().findFragmentById(R.id.outputFrame);
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
            translateTask = new AskForTranslateTask(translateProgressDialog, this);
            translateTask.execute(input, fromLang, toLang);
        }
    }
}


// TODO toast, если не выбран язык
// TODO если поле ввода пустое, содержит только пробелы и переносы строк или языки оригинала и
// перевода одинаковые, лишний запрос на сервер не делать
