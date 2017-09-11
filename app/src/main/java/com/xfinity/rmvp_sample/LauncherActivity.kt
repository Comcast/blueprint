package com.xfinity.rmvp_sample

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)

        (findViewById(R.id.simple_screen_button) as Button).setOnClickListener({
            startActivity(Intent(this, StaticScreenActivity::class.java))
        })

        (findViewById(R.id.dynamic_screen_button) as Button).setOnClickListener({
            startActivity(Intent(this, DynamicScreenActivity::class.java))
        })
    }
}