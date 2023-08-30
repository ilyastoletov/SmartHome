package com.dev.smarthouse.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.dev.smarthouse.R

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = 21.sp,
        lineHeight = 26.sp,
        fontFamily = FontFamily(Font(R.font.circe_regular)),
        fontWeight = FontWeight(400),
        color = Color(0xFF333333),
        textAlign = TextAlign.Center,
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
    lineHeight = 16.sp,
    fontFamily = FontFamily(Font(R.font.circe_regular)),
    fontWeight = FontWeight(400),
    color = Color(0xFF333333),
    textAlign = TextAlign.Center,
    )
)