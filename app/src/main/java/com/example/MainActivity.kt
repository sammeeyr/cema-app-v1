package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.navigation.CemaMainApp
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.CemaTheme
import com.example.ui.viewmodel.CemaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CemaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            var showSplash by remember { mutableStateOf(true) }

            CemaTheme(darkTheme = isDarkMode) {
                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    CemaMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

