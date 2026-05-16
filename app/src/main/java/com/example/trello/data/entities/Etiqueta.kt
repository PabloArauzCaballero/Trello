package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "etiqueta"
)
class Etiqueta (
    @PrimaryKey(autoGenerate = true)
    val idEtiqueta: Int = 0,

    val nombre: String
)