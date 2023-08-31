package com.dev.smarthouse.presentation.ui.failure

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.dev.smarthouse.R
import com.dev.smarthouse.presentation.theme.Typography

@Composable
fun NetworkFailureScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Image(painter = painterResource(id = R.drawable.baseline_warning_24), contentDescription = "No internet warning picture")
        Text(text = stringResource(id = R.string.no_internet), style = Typography.titleMedium)
    }
}