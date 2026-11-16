package com.example.island

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IslandService : Service() {
    private lateinit var wm: WindowManager
    private lateinit var pill: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var showingNotif = false

    private val clockTick = object : Runnable {
        override fun run() {
            if (!showingNotif) pill.text = timeNow()
            handler.postDelayed(this, 1000)
        }
    }

    private val hideNotif = Runnable {
        showingNotif = false
        pill.text = timeNow()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        instance = this

        val channel = NotificationChannel("island", "Island", NotificationManager.IMPORTANCE_MIN)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        startForeground(1, Notification.Builder(this, "island")
            .setContentTitle("Island is running")
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .build())

        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val dp = resources.displayMetrics.density

        pill = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 14f
            setPadding((16 * dp).toInt(), (8 * dp).toInt(), (16 * dp).toInt(), (8 * dp).toInt())
            background = GradientDrawable().apply {
                setColor(Color.BLACK)
                cornerRadius = 100 * dp
            }
            text = timeNow()
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = (10 * dp).toInt()
        }

        wm.addView(pill, params)
        handler.post(clockTick)
    }

    private fun showNotif(text: String) {
        showingNotif = true
        pill.text = text
        handler.removeCallbacks(hideNotif)
        handler.postDelayed(hideNotif, 4000)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        wm.removeView(pill)
        instance = null
        super.onDestroy()
    }

    companion object {
        var instance: IslandService? = null

        fun show(text: String) {
            instance?.showNotif(text)
        }

        private fun timeNow(): String =
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
    }
}
