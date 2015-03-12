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
    private Map<String, ArrayList<String>> langsPairs; // Список направлений переводов (az - (ru, en, ...))
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
            if (langsPairs.get(fromLangCode) == null) {
                langsPairs.put(fromLangCode, new ArrayList<String>());
            }
            langsPairs.get(fromLangCode).add(toLangCode);
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

    private String getNameByCode(String code) {
        for (Map.Entry<String,String> nameCodeEntry : langNameToCode.entrySet()) {
            final String codeEntry = nameCodeEntry.getValue();
            if (codeEntry.equals(code)) {
                return nameCodeEntry.getKey();
            }
        }

        return "";
    }

    public String[] getFromNamesByToCode(String toCode) {
        ArrayList<String> fromLangsCodeArrayList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> langPair : langsPairs.entrySet()) {
            final String fromLangCode = langPair.getKey();
            final ArrayList<String> toLangCodeArray = langPair.getValue();
            for (String toLangCode : toLangCodeArray) {
                if (toLangCode.equals(toCode)) {
                    fromLangsCodeArrayList.add(fromLangCode);
                }
            }
        }

        return getNamesArrayByCodeArrayList(fromLangsCodeArrayList);
    }

    public String[] getToNamesByFromCode(String fromCode) {
        ArrayList<String> toLangsCodeArrayList = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> langPair : langsPairs.entrySet()) {
            final String fromLangCode = langPair.getKey();
            final ArrayList<String> toLangCodeArray = langPair.getValue();
            for (String toLangCode : toLangCodeArray) {
                if (fromLangCode.equals(fromCode)) {
                    toLangsCodeArrayList.add(toLangCode);
                }
            }
        }

        return getNamesArrayByCodeArrayList(toLangsCodeArrayList);
    }

    private String[] getNamesArrayByCodeArrayList(ArrayList<String> codeArrayList) {
        ArrayList<String> langsNamesArray = new ArrayList<>();
        for (String langCode : codeArrayList) {
            final String langName = getInstance().getNameByCode(langCode);
            langsNamesArray.add(langName);
        }

        return langsNamesArray.toArray(new String[langsNamesArray.size()]);
    }
}
