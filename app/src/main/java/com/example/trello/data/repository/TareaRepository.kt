package com.example.trello.data.repository

import android.content.Context
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.Tarea
import com.example.trello.data.relations.TareaConEtiquetas

class TareaRepository (
    private val context: Context
){
    suspend fun getTareasConEtiquetas(idUsuario: Int): List<TareaConEtiquetas>?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .getTareasConEtiquetas(idUsuario = idUsuario)
        }catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getTareaConEtiquetaById(idUsuario: Int, idTarea: Int): TareaConEtiquetas?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .getTareaConEtiquetasPorTarea(idUsuario = idUsuario, idTarea = idTarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun insertTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>,
    ): Long?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .insertTareaConEtiquetas(tarea = tarea, idsEtiquetas=idsEtiquetas)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>,
    ): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .updateTareaConEtiquetas(tarea = tarea, idsEtiquetas=idsEtiquetas)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }


    suspend fun insertTarea(
        tarea: Tarea,
    ): Long?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .insertTarea(tarea = tarea,)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateTarea(
        tarea: Tarea,
    ): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .updateTarea(tarea = tarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteTarea(
        tarea: Tarea,
    ): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .deleteTarea(tarea = tarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteTareaById(
        idTarea: Int,
        idUsuario: Int
    ): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .deleteTareaById(idTarea = idTarea, idUsuario = idUsuario)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEtiquetasDeTarea(
        idTarea: Int,
    ): Int?{
        return try {
            AppDatabase
                .getInstance(context)
                .tareaDao()
                .deleteEtiquetasDeTarea(idTarea = idTarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}