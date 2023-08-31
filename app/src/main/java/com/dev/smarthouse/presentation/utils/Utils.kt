package com.dev.smarthouse.presentation.utils

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

fun makeSmallVibration(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrator.vibrate(CombinedVibration.createParallel(VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)))
    } else {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}