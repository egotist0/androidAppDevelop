package com.bluixe.lecture2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class Details : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val info = intent.extras?.getString("info")
        val textView = findViewById<TextView>(R.id.details)
        textView.text = info
    }


}