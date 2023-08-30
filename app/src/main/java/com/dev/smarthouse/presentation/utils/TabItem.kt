package com.dev.smarthouse.presentation.utils

import androidx.compose.runtime.Composable
import com.dev.smarthouse.presentation.screens.cameras.view.CamerasScreen
import com.dev.smarthouse.presentation.screens.doors.view.DoorsScreen

typealias ComposableFun = @Composable () -> Unit

sealed class TabItem(val title: String, val screen: ComposableFun) {
    object Cameras : TabItem(title = "Камеры", screen = { CamerasScreen() })
    object Doors : TabItem(title = "Двери", screen = { DoorsScreen() })
}
