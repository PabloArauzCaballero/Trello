package com.example.trello.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val idUsuario: Int = 0,
    val nombre: String,
    val email: String? = null,
    val fechaCreacion: Date = Date(),
)