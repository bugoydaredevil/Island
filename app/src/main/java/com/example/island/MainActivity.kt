package com.example.island

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dp = resources.displayMetrics.density
        val pad = (20 * dp).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }

        layout.addView(Button(this).apply {
            text = "1. Bigyan ng 'Display over other apps'"
            setOnClickListener {
                startActivity(Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                ))
            }
        })

        layout.addView(Button(this).apply {
            text = "2. Bigyan ng Notification access"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        })

        layout.addView(Button(this).apply {
            text = "3. Simulan ang Island"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startForegroundService(Intent(this@MainActivity, IslandService::class.java))
                    Toast.makeText(this@MainActivity, "Tumatakbo na", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Kulang pa ang overlay permission", Toast.LENGTH_LONG).show()
                }
            }
        })

        setContentView(layout)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf("android.permission.POST_NOTIFICATIONS"), 1)
        }
    }
}
