package com.example.gamebacklogtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.gamebacklogtracker.data.repository.AppGraph
import com.example.gamebacklogtracker.ui.navigation.GameBacklogApp
import com.example.gamebacklogtracker.ui.theme.GameBacklogTrackerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppGraph.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            GameBacklogTrackerTheme {
                GameBacklogApp()
            }
        }
    }
}