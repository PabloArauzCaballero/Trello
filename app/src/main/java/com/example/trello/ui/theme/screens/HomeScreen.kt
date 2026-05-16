package com.example.trello.ui.theme.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    TareaScreen(
        onNavigateToForm = { navController.navigate(NavScreens.formRoute()) },
        onNavigateToEdit = { taskId -> navController.navigate(NavScreens.formRoute(taskId)) },
        onNavigateToEtiquetas = { navController.navigate(NavScreens.ETIQUETAS) },
        onNavigateToFiltro = { navController.navigate(NavScreens.FILTRO) }
    )
}
