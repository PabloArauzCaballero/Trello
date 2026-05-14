package com.example.trello.data.repository

import android.content.Context
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.Usuario

class UsuarioRepository(
    private val context: Context
) {
    suspend fun insertUsuario(usuario: Usuario): Long?{
        return try {
            AppDatabase
                .getInstance(context)
                .usuarioDao()
                .insertUsuario(usuario = usuario)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUsuario(usuario: Usuario): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .usuarioDao()
                .updateUsuario(usuario = usuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun getUsuario(idUsuario: Int): Usuario?{
        return try {
            AppDatabase
                .getInstance(context)
                .usuarioDao()
                .getUsuario(idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteUsuario(usuario: Usuario): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .usuarioDao()
                .deleteUsuario(usuario = usuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteUsuarioById(idUsuario: Int): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .usuarioDao()
                .deleteUsuarioById(idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}