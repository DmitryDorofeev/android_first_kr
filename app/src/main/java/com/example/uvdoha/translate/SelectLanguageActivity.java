package com.example.uvdoha.translate;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SelectLanguageActivity extends ListActivity {

    String[] languages = new String[] {"Русский", "Английский", "Китайский", "Эльфийский", "Оркский", "JavaScript"};
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, languages);

        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Intent i = new Intent();
        i.putExtra("language", item);
        setResult(Activity.RESULT_OK, i);
        finish();
    }

}
