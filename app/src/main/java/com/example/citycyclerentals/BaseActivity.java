package com.example.citycyclerentals;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

public class BaseActivity extends AppCompatActivity {
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loader = new ProgressBar(this);
    }

    public void toggleLoader(boolean show) {
        if (loader != null) {
            loader.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}
