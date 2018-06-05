package com.example.android.yamba;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
        setContentView(R.layout.activity_status);

        mPostButton = findViewById(R.id.status_button);
        mTextStatus = findViewById(R.id.status_text);
        mTextCount = findViewById(R.id.status_text_count);

        mPostButton.setOnClickListener(this);
        mTextStatus.addTextChangedListener(this);

        mDefaultColor = mTextCount.getTextColors().getDefaultColor();

        mTextStatus.setText(getIntent()
                .getStringExtra(StatusUpdateService.EXTRA_MESSAGE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yamba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                startService(new Intent(this, RefreshService.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String status = mTextStatus.getText().toString();

        //Send the update to our background service
        Intent intent = new Intent(this, StatusUpdateService.class);
        intent.putExtra(StatusUpdateService.EXTRA_MESSAGE, status);

        startService(intent);

        //Clear the UI
        mTextStatus.getText().clear();
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
        mTextCount.setText(String.valueOf(count));

        if (count < 10) {
            mTextCount.setTextColor(Color.RED);
        } else {
            mTextCount.setTextColor(mDefaultColor);
        }

        mPostButton.setEnabled(count >= 0);
    }
}
