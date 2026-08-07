package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.ui.screens.*
import com.example.ui.viewmodel.CemaTab
import com.example.ui.viewmodel.CemaViewModel

data class CemaNavItem(
    val tab: CemaTab,
    val title: String,
    val iconSelected: ImageVector,
    val iconUnselected: ImageVector
)

val navItems = listOf(
    CemaNavItem(CemaTab.HOME, "Home", Icons.Filled.Home, Icons.Outlined.Home),
    CemaNavItem(CemaTab.BIBLE, "Bible", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook),
    CemaNavItem(CemaTab.STUDY, "Study", Icons.Filled.Book, Icons.Outlined.Book),
    CemaNavItem(CemaTab.SERMONS, "Sermons", Icons.Filled.Headset, Icons.Outlined.Headset),

    CemaNavItem(CemaTab.PROFILE, "Profile", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun CemaMainApp(
    viewModel: CemaViewModel
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Long,
                actionLabel = "Dismiss"
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 4.dp
            ) {
                navItems.forEach { item ->
                    val isSelected = selectedTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.iconSelected else item.iconUnselected,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (selectedTab) {
                CemaTab.HOME -> HomeScreen(viewModel = viewModel, onNavigateTab = { viewModel.selectTab(it) })
                CemaTab.BIBLE -> BibleScreen(viewModel = viewModel)
                CemaTab.STUDY -> StudyGuideScreen(viewModel = viewModel)
                CemaTab.NOTEBOOK -> NotebookScreen(viewModel = viewModel)
                CemaTab.GIVE -> GivingScreen(viewModel = viewModel)
                CemaTab.AI_ASSISTANT -> AiAssistantScreen(viewModel = viewModel)
                CemaTab.PROFILE -> ProfileScreen(viewModel = viewModel, onNavigateTab = { viewModel.selectTab(it) })
                CemaTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                CemaTab.SERMONS -> SermonsScreen(viewModel = viewModel)
                CemaTab.ANNOUNCEMENTS -> AnnouncementsScreen(viewModel = viewModel)
            }
        }
    }
}
