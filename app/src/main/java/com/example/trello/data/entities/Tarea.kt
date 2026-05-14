package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import java.util.Date

@Entity (
    tableName = "tareas",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["idUsuario"],
            childColumns = ["idUsuario"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
    ],
    indices = [
        Index(value = ["idUsuario"])
    ]
)

data class Tarea (
    @PrimaryKey (autoGenerate = true)
    var idTarea: Int = 0,

    var idUsuario: Int,
    val titulo: String,
    val descripcion: String ?=null,
    val fechaVencimiento: Date ?=null,
    val prioridad: Prioridad = Prioridad.MEDIA,
    val estado: Estado = Estado.PENDIENTE,
    val fechaCreacion: Date = Date(),

)