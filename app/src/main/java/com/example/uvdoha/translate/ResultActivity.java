package com.example.uvdoha.translate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by BDV on 05.03.2015.
 */
public class ResultActivity extends FragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }
        if (savedInstanceState == null) {  // первый запуск активити
            ResultFragment result = new ResultFragment();
            Intent i = getIntent();
            if (i != null) {
                String resultText = i.getStringExtra("result");
                if (resultText != null) {
                    result.setResult(resultText);
                }
            }
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, result).commit();
        }
    }
}
