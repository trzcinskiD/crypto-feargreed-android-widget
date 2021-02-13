package com.example.cryptofeargreed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupHyperlink()
    }

    fun setupHyperlink() {
        val linkTextView1 = findViewById<TextView>(R.id.alternative_link_id)
        val linkTextView2 = findViewById<TextView>(R.id.btctools_link_id)
        linkTextView1.setMovementMethod(LinkMovementMethod.getInstance())
        linkTextView2.setMovementMethod(LinkMovementMethod.getInstance())
    }
}