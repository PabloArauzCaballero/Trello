package com.example.trello.data.repository
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.Usuario
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun insertUsuario(usuario: Usuario): Long?{
        return try {
            db
                .usuarioDao()
                .insertUsuario(usuario = usuario)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUsuario(usuario: Usuario): Int?{
        return try {
            db
                .usuarioDao()
                .updateUsuario(usuario = usuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun getUsuario(idUsuario: Int): Usuario?{
        return try {
            db
                .usuarioDao()
                .getUsuario(idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteUsuario(usuario: Usuario): Int?{
        return try {
            db
                .usuarioDao()
                .deleteUsuario(usuario = usuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteUsuarioById(idUsuario: Int): Int?{
        return try {
            db
                .usuarioDao()
                .deleteUsuarioById(idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}