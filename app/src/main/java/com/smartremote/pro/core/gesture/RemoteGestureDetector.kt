package com.smartremote.pro.core.gesture

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

enum class RemoteGesture {
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    TAP,
    CIRCLE_MENU
}

class RemoteGestureListener(
    private val onGesture: (RemoteGesture) -> Unit
) : GestureDetector.SimpleOnGestureListener() {

    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100

    override fun onDown(e: MotionEvent): Boolean = true

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        onGesture(RemoteGesture.TAP)
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null) return false

        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x

        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    onGesture(RemoteGesture.SWIPE_RIGHT)
                } else {
                    onGesture(RemoteGesture.SWIPE_LEFT)
                }
                return true
            }
        } else {
            if (abs(diffY) > swipeThreshold && abs(velocityY) > swipeVelocityThreshold) {
                if (diffY > 0) {
                    onGesture(RemoteGesture.SWIPE_DOWN)
                } else {
                    onGesture(RemoteGesture.SWIPE_UP)
                }
                return true
            }
        }
        return false
    }
}
