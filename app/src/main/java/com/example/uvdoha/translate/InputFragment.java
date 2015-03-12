package com.example.uvdoha.translate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by BDV on 05.03.2015.
 */
public class InputFragment extends Fragment {
    private static final int SELECT_FROM_CODE = 1;
    private static final int SELECT_TO_CODE = 2;
    private String fromLangCode = "";
    private String toLangCode = "";

    public interface onTranslateSucceedListener {
        public void translate(String input, String fromLang, String toLang);
    }

    onTranslateSucceedListener listener;

    Button selectFromButton;
    Button selectToButton;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, null);

        selectFromButton = (Button) view.findViewById(R.id.select_from_button);
        selectFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectLanguageActivity.class);
                if (toLangCode.equals("")) {
                    intent.putExtra("langsArray", LanguagesContainerSingleton.getInstance().getAllLangsNames());
                } else {
                    intent.putExtra("langsArray", LanguagesContainerSingleton.getInstance().getFromNamesByToCode(toLangCode));
                }
                startActivityForResult(intent, SELECT_FROM_CODE);
            }
        });

        selectToButton = (Button) view.findViewById(R.id.select_to_button);
        selectToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectLanguageActivity.class);
                if (fromLangCode.equals("")) {
                    intent.putExtra("langsArray", LanguagesContainerSingleton.getInstance().getAllLangsNames());
                } else {
                    intent.putExtra("langsArray", LanguagesContainerSingleton.getInstance().getToNamesByFromCode(fromLangCode));
                }
                startActivityForResult(intent, SELECT_TO_CODE);
            }
        });

        final EditText inputEditText = (EditText) view.findViewById(R.id.editTextInput);
        Button btnTranslate = (Button) view.findViewById(R.id.buttonTranslate);
        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.translate(inputEditText.getText().toString(), fromLangCode, toLangCode);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            final String langName = data.getStringExtra("language");
            switch (requestCode) {
                case SELECT_FROM_CODE:
                    fromLangCode = LanguagesContainerSingleton.getInstance().getCodeByName(langName);
                    selectFromButton.setText(langName);
                    break;
                case SELECT_TO_CODE:
                    toLangCode = LanguagesContainerSingleton.getInstance().getCodeByName(langName);
                    selectToButton.setText(langName);
                    break;
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (onTranslateSucceedListener) activity;
    }
}
