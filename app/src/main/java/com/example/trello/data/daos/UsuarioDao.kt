package com.example.trello.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.entities.Usuario

@Dao
interface UsuarioDao {
    @Query ("SELECT * FROM usuarios WHERE idUsuario= :idUsuario")
    suspend fun getUsuario(idUsuario: Int) : Usuario

    @Insert
    suspend fun insertUsuario(usuario: Usuario): Long

    @Update
    suspend fun updateUsuario(usuario: Usuario): Int

    @Delete
    suspend fun deleteUsuario(usuario: Usuario): Int

    @Query("DELETE FROM usuarios WHERE idUsuario = :idUsuario")
    suspend fun deleteUsuarioById(idUsuario: Int): Int



}