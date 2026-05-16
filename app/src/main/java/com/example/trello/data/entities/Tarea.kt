package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import java.util.Date

@Entity(
    tableName = "tareas"
)

data class Tarea (
    @PrimaryKey (autoGenerate = true)
    var idTarea: Int = 0,

    val titulo: String,
    val descripcion: String ?=null,
    val fechaVencimiento: Date ?=null,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val estado: Estado = Estado.PENDIENTE,
    val fechaCreacion: Date = Date(),

)