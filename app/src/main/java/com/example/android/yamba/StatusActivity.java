package com.example.android.yamba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class StatusActivity extends AppCompatActivity implements
        View.OnClickListener, TextWatcher {
    private int mDefaultColor;

    private Button mPostButton;
    private EditText mTextStatus;
    private TextView mTextCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Action bar stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_status);

        mPostButton = (Button) findViewById(R.id.status_button);
        mTextStatus = (EditText) findViewById(R.id.status_text);
        mTextCount = (TextView) findViewById(R.id.status_text_count);

        mPostButton.setOnClickListener(this);
        mTextStatus.addTextChangedListener(this);

        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mTextStatus.setText(getIntent()
                .getStringExtra(StatusUpdateService.EXTRA_MESSAGE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Update text color from settings
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        int textColor = prefs.getInt("textColor",
                getResources().getColor(android.R.color.primary_text_light));
        mTextStatus.setTextColor(textColor);
    }

    @Override
    public void onClick(View v) {
        String status = mTextStatus.getText().toString();

        //Send the update to our background service
        Intent intent = new Intent(this, StatusUpdateService.class);
        intent.putExtra(StatusUpdateService.EXTRA_MESSAGE, status);

        startService(intent);

        //We're done here
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = 140 - s.length();
        mTextCount.setText(Integer.toString(count));

        if (count < 10) {
            mTextCount.setTextColor(Color.RED);
        } else {
            mTextCount.setTextColor(mDefaultColor);
        }

        mPostButton.setEnabled(count >= 0);
    }
}
