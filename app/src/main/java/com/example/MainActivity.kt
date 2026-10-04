package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.PtmRecord
import com.example.ui.PtmViewModel
import com.example.ui.screens.CerdikGuideScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MonthlyReportScreen
import com.example.ui.screens.PtmFormScreen
import com.example.ui.screens.RecordDetailScreen
import com.example.ui.screens.RecordsListScreen
import com.example.ui.theme.MyApplicationTheme

sealed class Screen {
    data object Dashboard : Screen()
    data object Form : Screen()
    data object Records : Screen()
    data object Report : Screen()
    data object Guide : Screen()
    data class Detail(val record: PtmRecord) : Screen()
}

enum class NavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    BERANDA("beranda", "Beranda", Icons.Filled.Home, Icons.Outlined.Home),
    RIWAYAT("riwayat", "Riwayat", Icons.Filled.Description, Icons.Outlined.Description),
    LAPORAN("laporan", "Laporan", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    PANDUAN("panduan", "CERDIK", Icons.Filled.MenuBook, Icons.Outlined.MenuBook)
}

class MainActivity : ComponentActivity() {

    private val viewModel: PtmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: PtmViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Dashboard) }

    // Intercept hardware back button when on sub-screens
    BackHandler(enabled = currentScreen !is Screen.Dashboard) {
        currentScreen = Screen.Dashboard
    }

    val showBottomBar = currentScreen !is Screen.Form && currentScreen !is Screen.Detail

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    val activeItem = when (currentScreen) {
                        is Screen.Dashboard -> NavItem.BERANDA
                        is Screen.Records -> NavItem.RIWAYAT
                        is Screen.Report -> NavItem.LAPORAN
                        is Screen.Guide -> NavItem.PANDUAN
                        else -> NavItem.BERANDA
                    }

                    NavItem.values().forEach { item ->
                        val isSelected = (activeItem == item)
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentScreen = when (item) {
                                    NavItem.BERANDA -> Screen.Dashboard
                                    NavItem.RIWAYAT -> Screen.Records
                                    NavItem.LAPORAN -> Screen.Report
                                    NavItem.PANDUAN -> Screen.Guide
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            onNavigateToForm = { currentScreen = Screen.Form },
                            onNavigateToRecords = { currentScreen = Screen.Records },
                            onNavigateToReport = { currentScreen = Screen.Report },
                            onNavigateToGuide = { currentScreen = Screen.Guide },
                            onRecordClick = { currentScreen = Screen.Detail(it) }
                        )
                    }

                    is Screen.Form -> {
                        PtmFormScreen(
                            viewModel = viewModel,
                            onNavigateBack = { currentScreen = Screen.Dashboard },
                            onSavedSuccess = {
                                currentScreen = Screen.Records
                            }
                        )
                    }

                    is Screen.Records -> {
                        RecordsListScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { currentScreen = Screen.Detail(it) },
                            onNavigateToForm = { currentScreen = Screen.Form },
                            onNavigateToEdit = { currentScreen = Screen.Form }
                        )
                    }

                    is Screen.Report -> {
                        MonthlyReportScreen(
                            viewModel = viewModel,
                            onRecordClick = { currentScreen = Screen.Detail(it) }
                        )
                    }

                    is Screen.Guide -> {
                        CerdikGuideScreen()
                    }

                    is Screen.Detail -> {
                        RecordDetailScreen(
                            record = screen.record,
                            onNavigateBack = { currentScreen = Screen.Dashboard }
                        )
                    }
                }
            }
        }
    }
}
