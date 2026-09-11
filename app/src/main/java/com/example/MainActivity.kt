package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuthSession
import com.example.viewmodel.CardProViewModel
import com.example.viewmodel.UserRole

import com.example.util.AppLanguage
import com.example.util.AppSettingsManager
import com.example.util.tr

enum class NavigationTab(
    val titleTa: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
) {
    DASHBOARD("முகப்பு", "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    NEW_ORDER("புதிய ஆர்டர்", "New Order", Icons.Filled.AddCircle, Icons.Outlined.AddCircleOutline, "nav_new_order"),
    TRACKING("நிலவரம்", "Tracking", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping, "nav_tracking"),
    RATE_CARD("விலை", "Rate Card", Icons.Filled.PriceCheck, Icons.Outlined.PriceCheck, "nav_rate_card"),
    FILES("கோப்புகள்", "Files", Icons.Filled.Folder, Icons.Outlined.Folder, "nav_files")
}

class MainActivity : ComponentActivity() {
    private val viewModel: CardProViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val session by viewModel.currentSession.collectAsState()
                val currentSession = session
                if (currentSession == null) {
                    LoginScreen(viewModel = viewModel)
                } else {
                    MainApp(viewModel = viewModel, session = currentSession)
                }
            }
        }
    }
}

@Composable
fun MainApp(viewModel: CardProViewModel, session: AuthSession) {
    var currentTab by remember { mutableStateOf(NavigationTab.DASHBOARD) }
    var trackingFilter by remember { mutableStateOf<String?>(null) }

    val availableTabs = remember(session.role) {
        if (session.role == UserRole.ADMIN) {
            NavigationTab.values().toList()
        } else {
            listOf(
                NavigationTab.DASHBOARD,
                NavigationTab.NEW_ORDER,
                NavigationTab.TRACKING,
                NavigationTab.RATE_CARD
            )
        }
    }

    LaunchedEffect(availableTabs) {
        if (currentTab !in availableTabs) {
            currentTab = NavigationTab.DASHBOARD
        }
    }

    val currentLang by AppSettingsManager.language.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                availableTabs.forEach { tab ->
                    val selected = currentTab == tab
                    val tabLabel = if (currentLang == AppLanguage.TAMIL) tab.titleTa else tab.titleEn
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            currentTab = tab
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tabLabel
                            )
                        },
                        label = { Text(tabLabel) },
                        modifier = Modifier.testTag(tab.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            label = "tab_transition"
        ) { tab ->
            when (tab) {
                NavigationTab.DASHBOARD -> DealerDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToNewOrder = { currentTab = NavigationTab.NEW_ORDER },
                    onNavigateToTracking = { status ->
                        trackingFilter = status
                        currentTab = NavigationTab.TRACKING
                    },
                    onNavigateToRateCard = { currentTab = NavigationTab.RATE_CARD },
                    onNavigateToFileManagement = { currentTab = NavigationTab.FILES }
                )
                NavigationTab.NEW_ORDER -> NewOrderScreen(
                    viewModel = viewModel,
                    onOrderSubmitted = {
                        trackingFilter = null
                        currentTab = NavigationTab.TRACKING
                    },
                    onBack = { currentTab = NavigationTab.DASHBOARD }
                )
                NavigationTab.TRACKING -> OrderTrackingScreen(
                    viewModel = viewModel,
                    initialStatusFilter = trackingFilter,
                    onNavigateToNewOrder = { currentTab = NavigationTab.NEW_ORDER }
                )
                NavigationTab.RATE_CARD -> RateCardSopScreen(
                    viewModel = viewModel,
                    onNavigateToNewOrder = { currentTab = NavigationTab.NEW_ORDER }
                )
                NavigationTab.FILES -> {
                    if (session.role == UserRole.ADMIN) {
                        FileManagementScreen(viewModel = viewModel)
                    } else {
                        DealerDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToNewOrder = { currentTab = NavigationTab.NEW_ORDER },
                            onNavigateToTracking = { status ->
                                trackingFilter = status
                                currentTab = NavigationTab.TRACKING
                            },
                            onNavigateToRateCard = { currentTab = NavigationTab.RATE_CARD },
                            onNavigateToFileManagement = {}
                        )
                    }
                }
            }
        }
    }
}

