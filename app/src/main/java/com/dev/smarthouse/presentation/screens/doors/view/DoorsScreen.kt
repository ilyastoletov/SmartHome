package com.dev.smarthouse.presentation.screens.doors.view

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import com.dev.smarthouse.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dev.domain.model.Door
import com.dev.smarthouse.presentation.screens.doors.contract.DoorsContract
import com.dev.smarthouse.presentation.screens.doors.contract.DoorsViewModel
import com.dev.smarthouse.presentation.theme.Blue
import com.dev.smarthouse.presentation.theme.Grey
import com.dev.smarthouse.presentation.theme.Typography
import com.dev.smarthouse.presentation.theme.White
import com.dev.smarthouse.presentation.ui.failure.NetworkFailureScreen
import com.dev.smarthouse.presentation.ui.loading.LoadingScreen
import com.dev.smarthouse.presentation.utils.makeSmallVibration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

@Composable
fun DoorsScreen(viewModel: DoorsViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    DoorsScreenContent(state = state, onEvent = viewModel::handleEvents)

}

@Composable
private fun DoorsScreenContent(state: DoorsContract.State, onEvent: (DoorsContract.Event) -> Unit) {

    onEvent(DoorsContract.Event.LoadDoors())

    when(state) {
        is DoorsContract.State.Loading -> LoadingScreen()
        is DoorsContract.State.DoorsLoaded -> Content(doorsList = state.data, onEvent = onEvent)
        is DoorsContract.State.NetworkFailure -> NetworkFailureScreen()
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(doorsList: List<Door>, onEvent: (DoorsContract.Event) -> Unit) {

    val isRefreshing = remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing.value,
        onRefresh = {
            isRefreshing.value = true
            onEvent(DoorsContract.Event.LoadDoors(true))
            runBlocking { delay(1000L) }
            isRefreshing.value = false
        }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(state = pullRefreshState)
        .padding(top = 5.dp, start = 5.dp, end = 5.dp)
    ) {
        LazyColumn {
            items(doorsList) { DoorItem(doorItem = it, onEvent = onEvent) }
        }
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = isRefreshing.value,
            state = pullRefreshState
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DoorItem(doorItem: Door, onEvent: (DoorsContract.Event) -> Unit) {
    
    val starIcon = remember { mutableStateOf<Boolean>(doorItem.isFavorite) }
    val isDoorChangeDialogShown = remember { mutableStateOf<Boolean>(false) }

    val iconSizes = Modifier
        .width(25.dp)
        .height(25.dp)

    val doorName = remember { mutableStateOf(doorItem.name) }

    val scope = rememberCoroutineScope()
    val swipeState = rememberSwipeableState(initialValue = 0)

    val onDoorNameChanged: (String) -> Unit = { newName ->
        onEvent(DoorsContract.Event.DoorSetNewName(doorId = doorItem.id, newName = newName))
        doorName.value = newName
        isDoorChangeDialogShown.value = false
    }

    val onDoorDialogDismiss: () -> Unit = {
        isDoorChangeDialogShown.value = false
    }

    val context = LocalContext.current
    val density = context.resources.displayMetrics.density

    Box(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .swipeable(
            state = swipeState,
            anchors = mapOf(
                0f to 0,
                -(90f * density) to 1,
                (0f * density) to 0
            ),
            thresholds = { _, _ -> FractionalThreshold(0.5f) },
            orientation = Orientation.Horizontal
        ), contentAlignment = Alignment.Center
    ) {
        Row(modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 4.5f.dp)) {
            IconButton(onClick = {
                scope.launch {
                    swipeState.animateTo(0, tween(600, 0))
                }
                makeSmallVibration(context)
                isDoorChangeDialogShown.value = true
            }) {
                Image(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .drawBehind {
                            drawCircle(
                                color = Grey,
                                radius = 35f,
                                style = Stroke(width = 1.5f)
                            )
                        },
                    painter = painterResource(id = R.drawable.baseline_edit_24),
                    contentDescription = "edit name button"
                )
            }
            IconButton(onClick = {
                scope.launch {
                    swipeState.animateTo(0, tween(600, 0))
                }
                makeSmallVibration(context)
                onEvent(DoorsContract.Event.SetDoorFavorite(doorId = doorItem.id, value = !starIcon.value))
                starIcon.value = !starIcon.value
                Log.d("STAR I9ON", starIcon.value.toString())
            }
            ) {

                Image(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .drawBehind {
                            drawCircle(
                                color = Grey,
                                radius = 35f,
                                style = Stroke(width = 1.5f)
                            )
                        },
                    painter = if (starIcon.value) painterResource(id = R.drawable.outlined_star) else painterResource(id = R.drawable.star),
                    contentDescription = "make favorite button"
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .offset { IntOffset(swipeState.offset.value.roundToInt(), 0) },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(1.5f.dp)
        ) {
            Column {
                if (doorItem.snapshotLink != null) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(207.dp)) {
                        AsyncImage(
                            modifier = Modifier.fillMaxWidth(),
                            model = doorItem.snapshotLink,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                        Image(
                            modifier = Modifier
                                .width(60.dp)
                                .height(60.dp)
                                .align(Alignment.Center),
                            painter = painterResource(id = R.drawable.baseline_play_circle_outline_24),
                            contentDescription = "Play button"
                        )
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = doorName.value, style = Typography.titleMedium)
                    Row {
                        if (starIcon.value) {
                            Image(
                                modifier = iconSizes,
                                painter = painterResource(id = R.drawable.star),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                        }
                        Image(
                            modifier = iconSizes,
                            painter = painterResource(id = R.drawable.lock),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    if (isDoorChangeDialogShown.value) {
        ChangeDoorNameDialog(onDismiss = { onDoorDialogDismiss() }, onDoorNameChanged = onDoorNameChanged)
    }

}

@Composable
private fun ChangeDoorNameDialog(onDismiss: () -> Unit, onDoorNameChanged: (String) -> Unit) {
    
    val newDoorName = rememberSaveable { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RoundedCornerShape(5.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                OutlinedTextField(
                    value = newDoorName.value,
                    onValueChange = { text -> newDoorName.value = text },
                    label = { Text(text = "Новое имя", style = Typography.titleSmall) },
                    placeholder = { Text(text = "Введите новое имя двери", style = Typography.titleMedium, color = Grey) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Blue,
                        unfocusedBorderColor = Blue,
                        disabledBorderColor = Blue,
                        cursorColor = Color.Black
                    ),
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .align(Alignment.CenterHorizontally)
                    .clickable {
                        if (newDoorName.value.isNotEmpty() || newDoorName.value.length > 30) {
                            onDoorNameChanged(newDoorName.value)
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Ок", style = Typography.titleMedium, color = Blue)
                }
            }
        }
    }
}