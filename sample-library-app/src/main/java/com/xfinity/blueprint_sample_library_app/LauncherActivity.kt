/*
 * Copyright 2017 Comcast Cable Communications Management, LLC
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xfinity.blueprint_sample_library_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.launcher_activity)

        findViewById<Button>(R.id.simple_screen_button).setOnClickListener {
            startActivity(Intent(this, StaticScreenActivity::class.java))
        }

        findViewById<Button>(R.id.dynamic_screen_button).setOnClickListener {
            startActivity(Intent(this, DynamicScreenActivity::class.java))
        }

        findViewById<Button>(R.id.bp_arch_button).setOnClickListener {
            startActivity(Intent(this, ArchitectSampleActivity::class.java))
        }
    }
}