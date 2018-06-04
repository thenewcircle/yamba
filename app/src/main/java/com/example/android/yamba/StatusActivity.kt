package com.example.android.yamba

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class StatusActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {
    private var defaultColor: Int = 0

    private lateinit var postButton: Button
    private lateinit var textStatus: EditText
    private lateinit var textCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status)

        postButton = findViewById(R.id.status_button)
        textStatus = findViewById(R.id.status_text)
        textCount = findViewById(R.id.status_text_count)

        postButton.setOnClickListener(this)
        textStatus.addTextChangedListener(this)

        defaultColor = textCount.textColors.defaultColor

        textStatus.text = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_yamba, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onClick(v: View) {
        Toast.makeText(this, "Click!", Toast.LENGTH_SHORT).show()
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        val count = 140 - s.length
        textCount.text = "$count"

        if (count < 10) {
            textCount.setTextColor(Color.RED)
        } else {
            textCount.setTextColor(defaultColor)
        }

        postButton.isEnabled = count >= 0
    }
}
