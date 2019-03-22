package com.example.travelplan;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class ChangeLanguage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.language_page);

        MainActivity.Constants.activityList.add(this);

        RadioGroup radio = (RadioGroup) findViewById(R.id.radio);
        final RadioButton radioButtonen = (RadioButton) findViewById(R.id.radio_en);
        final RadioButton radioButtonzh = (RadioButton) findViewById(R.id.radio_zh);

        if (MainActivity.Constants.langae.equals("en")) {
            radioButtonen.setChecked(true);
        } else {
            radioButtonzh.setChecked(true);
        }

        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_en) {
                    MainActivity.Constants.langae = "en";
                    radioButtonzh.setChecked(false);
                } else {
                    radioButtonen.setChecked(false);
                    MainActivity.Constants.langae = "zh";
                }
            }
        });
    }

    public void onClick(View view) {
        Locale locale = new Locale(MainActivity.Constants.langae);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        Resources resources = getResources();
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        //close all active page
        for (Activity activity : MainActivity.Constants.activityList) {
            activity.finish();
        }
        //go back to main page
        startActivity(new Intent(this, MainActivity.class));
    }
}
