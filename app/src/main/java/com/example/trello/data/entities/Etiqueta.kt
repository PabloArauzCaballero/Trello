package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "etiqueta",
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
class Etiqueta (
    val idEtiqueta: Int,

    var idUsuario: Int,
    val nombre: String
)