package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "etiquetaxtarea",
    primaryKeys = ["idTarea", "idEtiqueta"],
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
        Index(value = ["idEtiqueta"])
    ]
)
data class EtiquetaXTarea(
    val idTarea: Int,
    val idEtiqueta: Int
)