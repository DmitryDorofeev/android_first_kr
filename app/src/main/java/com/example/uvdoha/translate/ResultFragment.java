package com.example.uvdoha.translate;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by dmitry on 03.03.15.
 */
public class ResultFragment extends Fragment {

    public void setResult(String result) {
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        if (this.getArguments() == null) {
            this.setArguments(new Bundle());
        }
        this.getArguments().putBundle("result", bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);
        TextView resultText = (TextView) view.findViewById(R.id.result_text);
        resultText.setText(getArguments().getBundle("result").getString("result"));
        Log.d("skjdfghjlkds", "azaz" + getArguments().getBundle("result").getString("result"));
        return view;
    }
}
