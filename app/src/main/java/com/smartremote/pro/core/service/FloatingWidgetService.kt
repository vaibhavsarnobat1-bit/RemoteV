package com.smartremote.pro.core.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import com.smartremote.pro.R
import com.smartremote.pro.SmartRemoteApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FloatingWidgetService : Service() {

    private var windowManager: WindowManager? = null
    private var floatingView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 200
        }

        // Programmatic lightweight floating remote pill
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundResource(R.drawable.widget_background)
            setPadding(12, 12, 12, 12)
        }

        val powerBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_power)
            setBackgroundResource(R.drawable.widget_button_bg)
            setOnClickListener { sendQuickCmd("power") }
        }
        val volDownBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_volume_down)
            setBackgroundResource(R.drawable.widget_button_bg)
            setOnClickListener { sendQuickCmd("volDown") }
        }
        val volUpBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_volume_up)
            setBackgroundResource(R.drawable.widget_button_bg)
            setOnClickListener { sendQuickCmd("volUp") }
        }

        layout.addView(powerBtn)
        layout.addView(volDownBtn)
        layout.addView(volUpBtn)

        floatingView = layout

        // Touch listener for dragging the floating pill
        layout.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(floatingView, params)
                        return true
                    }
                }
                return false
            }
        })

        windowManager?.addView(floatingView, params)
    }

    private fun sendQuickCmd(cmd: String) {
        val app = applicationContext as? SmartRemoteApp
        val irTransmitter = app?.appContainer?.irTransmitter
        val deviceRepo = app?.appContainer?.deviceRepository

        CoroutineScope(Dispatchers.IO).launch {
            val devices = deviceRepo?.discoverNetworkDevices()
            val primaryDevice = devices?.firstOrNull()
            if (primaryDevice != null && deviceRepo != null && irTransmitter != null) {
                val hex = deviceRepo.getPreloadedCommands(primaryDevice.brand, primaryDevice.type)[cmd]
                if (hex != null) {
                    irTransmitter.transmitHex(hex)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (floatingView != null) {
            windowManager?.removeView(floatingView)
        }
    }
}
