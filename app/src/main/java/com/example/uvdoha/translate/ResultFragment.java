package com.example.uvdoha.translate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dmitry on 03.03.15.
 */
public class ResultFragment extends Fragment {

    public void setResult(String result) {
        TextView resultText = (TextView) this.getView().findViewById(R.id.result_text);
        resultText.setText(result);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        return view;
    }
}
