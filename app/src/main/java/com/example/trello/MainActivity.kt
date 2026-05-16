package com.example.trello

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trello.ui.theme.TrelloTheme
import com.example.trello.ui.theme.screens.EtiquetasScreen
import com.example.trello.ui.theme.screens.FiltroScreen
import com.example.trello.ui.theme.screens.FormScreen
import com.example.trello.ui.theme.screens.HomeScreen
import com.example.trello.ui.theme.screens.NavScreens
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigationApp()
        }
    }

    @Composable
    fun NavigationApp(navController: NavHostController = rememberNavController()) {
        TrelloTheme {
            NavHost(
                navController = navController,
                startDestination = NavScreens.HOME
            ) {
                composable(NavScreens.HOME) {
                    HomeScreen(navController = navController)
                }
                composable(
                    route = NavScreens.FORM_ROUTE,
                    arguments = listOf(
                        navArgument("taskId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val taskId = entry.arguments?.getInt("taskId")?.takeIf { it != -1 }
                    FormScreen(navController = navController, taskId = taskId)
                }
                composable(NavScreens.ETIQUETAS) {
                    EtiquetasScreen(navController = navController)
                }
                composable(NavScreens.FILTRO) {
                    FiltroScreen(navController = navController)
                }
            }
        }
    }
}
