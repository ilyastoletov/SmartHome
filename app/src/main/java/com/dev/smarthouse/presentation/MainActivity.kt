package com.dev.smarthouse.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material.TabPosition
import com.dev.smarthouse.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import com.dev.smarthouse.presentation.screens.cameras.view.CamerasScreen
import com.dev.smarthouse.presentation.screens.doors.view.DoorsScreen
import com.dev.smarthouse.presentation.theme.Blue
import com.dev.smarthouse.presentation.theme.SmartHouseTheme
import com.dev.smarthouse.presentation.theme.Typography
import com.dev.smarthouse.presentation.theme.White
import com.dev.smarthouse.presentation.utils.TabItem
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartHouseTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainContent()
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainContent() {

    val tabsList: List<TabItem> = listOf(TabItem.Cameras, TabItem.Doors)
    val pagerState = rememberPagerState(pageCount = tabsList.size, initialPage = 0)

    Column(modifier = Modifier
        .fillMaxSize()
        .background(White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(15.dp),
            text = stringResource(id = R.string.tabs_header),
            style = Typography.headlineLarge
        )
        Tabs(tabsList = tabsList, pagerState = pagerState)
        TabContent(tabsList = tabsList, pagerState = pagerState)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tabs(tabsList: List<TabItem>, pagerState: PagerState) {

    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPosition ->
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffset(pagerState, tabPosition),
                height = 5.dp,
                color = Blue
            ) },
        backgroundColor = White
    ) {
        tabsList.forEachIndexed { index, item ->
            Tab(
                selected = index == pagerState.currentPage,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                text = { Text(text = item.title, style = Typography.titleMedium) }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TabContent(tabsList: List<TabItem>, pagerState: PagerState) {
    HorizontalPager(state = pagerState) {page ->
        tabsList[page].screen()
    }
}