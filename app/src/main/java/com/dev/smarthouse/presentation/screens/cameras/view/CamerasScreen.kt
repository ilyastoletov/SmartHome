package com.dev.smarthouse.presentation.screens.cameras.view

import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.IconButton
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.domain.model.Camera
import com.dev.domain.model.Room
import com.dev.smarthouse.R
import com.dev.smarthouse.presentation.screens.cameras.contract.CameraContract
import com.dev.smarthouse.presentation.screens.cameras.contract.CameraViewModel
import com.dev.smarthouse.presentation.theme.Blue
import com.dev.smarthouse.presentation.theme.Grey
import com.dev.smarthouse.presentation.theme.Typography
import com.dev.smarthouse.presentation.ui.failure.NetworkFailureScreen
import com.dev.smarthouse.presentation.ui.loading.LoadingScreen
import com.dev.smarthouse.presentation.utils.makeSmallVibration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Composable
fun CamerasScreen(viewModel: CameraViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    CamerasScreenContent(state = state, onEvent = viewModel::handleEvents)

}

@Composable
private fun CamerasScreenContent(state: CameraContract.State, onEvent: (CameraContract.Event) -> Unit) {

    onEvent(CameraContract.Event.LoadCameras())

    when(state) {
        is CameraContract.State.Loading -> LoadingScreen()
        is CameraContract.State.CamerasLoaded -> Content(state.data, onEvent)
        is CameraContract.State.NetworkFailure -> NetworkFailureScreen()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(roomsList: List<Room>, onEvent: (CameraContract.Event) -> Unit) {

    val refreshingState = remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshingState.value,
        onRefresh = {
            refreshingState.value = true
            onEvent(CameraContract.Event.LoadCameras(true))
            runBlocking { delay(1000L) }
            refreshingState.value = false
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 10.dp, top = 15.dp)
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn {
            roomsList.forEach { room ->
                item { RoomNameText(name = room.name) }
                items(room.cameras) { camera ->
                    CameraItem(camera = camera, onEvent = onEvent)
                }
            }
        }

        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = refreshingState.value,
            state = pullRefreshState
        )
    }

}

@Composable
private fun RoomNameText(name: String) {
    Text(text = name, style = Typography.headlineMedium)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun CameraItem(camera: Camera, onEvent: (CameraContract.Event) -> Unit) {

    val swipeState = rememberSwipeableState(initialValue = 0)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val starIcon = remember { mutableStateOf<Boolean>(camera.isFavorite) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .swipeable(
                state = swipeState,
                anchors = mapOf(
                    0f to 0,
                    -(60f * density) to 1,
                    (0f * density) to 0
                ),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal
            )
    ) {
        IconButton(onClick = {
            scope.launch {
                swipeState.animateTo(0, tween(600, 0))
            }
            makeSmallVibration(context)
            val newFavoriteValue = !camera.isFavorite
            onEvent(CameraContract.Event.SetCamFavorite(
                roomName = camera.roomName!!,
                cameraId = camera.id,
                value = newFavoriteValue)
            )
            starIcon.value = newFavoriteValue
        },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
                Image(modifier = Modifier
                    .width(35.dp)
                    .height(35.dp)
                    .padding(5.dp)
                    .drawBehind {
                        drawCircle(color = Grey, radius = 40f, style = Stroke(width = 1.5f))
                    },
                    painter = if (starIcon.value) {
                        painterResource(id = R.drawable.outlined_star)
                    } else painterResource(id = R.drawable.star), contentDescription = null)
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 3.dp,
                    spotColor = Color(0x05000000),
                    ambientColor = Color(0x05000000)
                )
                .padding(top = 10.dp, bottom = 15.dp)
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) },
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(15.dp)
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(207.dp)) {
                    AsyncImage(
                        modifier = Modifier.fillMaxWidth(),
                        model = camera.snapshotLink,
                        contentDescription = "camera preview",
                        contentScale = ContentScale.FillBounds,
                    )
                    Image(
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.baseline_play_circle_outline_24),
                        contentDescription = "play circle"
                    )
                    if (starIcon.value) {
                        Image(
                            modifier = Modifier
                                .padding(top = 5.dp, end = 5.dp)
                                .align(Alignment.TopEnd),
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "star"
                        )
                    }
                    if (camera.isRec) {
                        Image(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .padding(top = 4.dp, start = 4.dp)
                                .align(Alignment.TopStart),
                            painter = painterResource(id = R.drawable.rec),
                            contentDescription = "rec"
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp, bottom = 10.dp),
                    text = camera.name,
                    style = Typography.titleMedium
                )
            }
        }
    }

}