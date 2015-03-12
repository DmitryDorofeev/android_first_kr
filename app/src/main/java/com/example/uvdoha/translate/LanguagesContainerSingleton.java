package com.example.uvdoha.translate;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class LanguagesContainerSingleton {
    private static final String LOG_TAG = "LOG";
    private Map<String, String> langsPairs; // Список направлений переводов (az - ru)
    private Map<String, String> langNameToCode; // Расшифровки кодов языков (Русский - ru)

    private LanguagesContainerSingleton() {}

    private static class SingletonHolder {
        private static final LanguagesContainerSingleton INSTANCE = new LanguagesContainerSingleton();
    }

    public static LanguagesContainerSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setLangsPairsAndNames(JSONObject result) throws JSONException {
        Log.d(LOG_TAG, result.toString());

        langsPairs = new HashMap<>();
        JSONArray directionsArray = result.getJSONArray("dirs");
        for (int i = 0; i < directionsArray.length(); i++) {
            final String langPairStr = directionsArray.getString(i);
            final String[] langPair = langPairStr.split("\\-");
            final String fromLangCode = langPair[0];
            final String toLangCode = langPair[1];
            langsPairs.put(fromLangCode, toLangCode);
        }

        langNameToCode = new HashMap<>();
        JSONObject codeToNamesJson = result.getJSONObject("langs");
        Iterator<?> langCodes = codeToNamesJson.keys();
        while( langCodes.hasNext() ) {
            final String langCode = (String)langCodes.next();
            final String langName = codeToNamesJson.getString(langCode);
            langNameToCode.put(langName, langCode);
        }
    }

    public String[] getAllLangsNames() {
        Set<String> namesSet = langNameToCode.keySet();
        return namesSet.toArray(new String[namesSet.size()]);
    }

    public String getCodeByName(String name) {
        return langNameToCode.get(name);
    }
}
