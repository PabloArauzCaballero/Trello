package com.example.trello.ui.theme.screens

object NavScreens {
    const val HOME = "HOME"
    const val FORM = "FORM"
    const val FORM_ROUTE = "FORM?taskId={taskId}"
    const val ETIQUETAS = "ETIQUETAS"
    const val FILTRO = "FILTRO"

    fun formRoute(taskId: Int? = null): String {
        return if (taskId == null) FORM else "FORM?taskId=$taskId"
    }
}