package com.example.trello.data.repository

import android.content.Context
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.Etiqueta

class EtiquetaRepository (
    private val context: Context
){
    suspend fun getEtiquetasPorUsuario(idUsuario: Int): List<Etiqueta>{
        return try {
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .getEtiquetas(idUsuario)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getEtiquetasPorUsuarioYId(idUsuario: Int, idEtiqueta: Int): Etiqueta?{
        return try{
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .getEtiquetaById(idEtiqueta = idEtiqueta, idUsuario=idUsuario)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun insertEtiqueta(etiqueta: Etiqueta): Long?{
        return try {
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .insertEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateEtiqueta(etiqueta: Etiqueta): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .updateEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEtiqueta(etiqueta: Etiqueta): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .deleteEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEtiquetaById(idEtiqueta: Int, idUsuario: Int): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .etiquetaDao()
                .deleteEtiquetaById(idEtiqueta, idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}