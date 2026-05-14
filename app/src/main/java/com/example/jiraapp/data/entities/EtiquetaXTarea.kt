package com.example.jiraapp.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
@Entity(
    tableName = "etiquetaxtarea",
    foreignKeys = [
        ForeignKey(
            entity = Tarea::class,
            parentColumns = ["idTarea"],
            childColumns = ["idTarea"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = Etiqueta::class,
            parentColumns = ["idEtiqueta"],
            childColumns = ["idEtiqueta"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )
    ],
    indices = [
        Index(value = ["idTarea"]),
        Index(value = ["idEtiqueta"]),
    ]
)

class EtiquetaXTarea (
    val idEtiqueta: Int,
    val idTarea: Int
)