package com.example.trello.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.trello.data.entities.EtiquetaXTarea
import com.example.trello.data.entities.Tarea
import com.example.trello.data.relations.TareaConEtiquetas

@Dao
interface TareaDao {
    @Transaction
    @Query("""
        SELECT * FROM tareas 
        WHERE idUsuario = :idUsuario
    """)
    suspend fun getTareasConEtiquetas(
        idUsuario: Int
    ): List<TareaConEtiquetas>

    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE idUsuario = :idUsuario
        AND idTarea = :idTarea
        LIMIT 1
    """)
    suspend fun getTareaConEtiquetasPorTarea(
        idUsuario: Int,
        idTarea: Int
    ): TareaConEtiquetas?

    @Insert
    suspend fun insertTarea(tarea: Tarea): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEtiquetaXTarea(relacion: EtiquetaXTarea): Long

    @Transaction
    suspend fun insertTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>
    ): Long {
        val idTareaGenerado = insertTarea(tarea)

        idsEtiquetas.forEach {idEtiqueta ->
            insertEtiquetaXTarea(
                EtiquetaXTarea(
                    idTarea = idTareaGenerado.toInt(),
                    idEtiqueta = idEtiqueta
                )
            )
        }

        return idTareaGenerado
    }

    @Update
    suspend fun updateTarea(tarea: Tarea): Int


    @Query("""
        DELETE FROM etiquetaxtarea
        WHERE idTarea = :idTarea
    """)
    suspend fun deleteEtiquetasDeTarea(idTarea: Int): Int


    @Transaction
    suspend fun  updateTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>
    ): Int {
        val filasActualizadas = updateTarea(tarea)

        deleteEtiquetasDeTarea(tarea.idTarea)

        idsEtiquetas.forEach { idEtiqueta->
            insertEtiquetaXTarea(
                EtiquetaXTarea(
                    idTarea = tarea.idTarea,
                    idEtiqueta = idEtiqueta
                )
            )
        }

        return filasActualizadas
    }

    @Delete
    suspend fun deleteTarea(tarea: Tarea): Int

    @Query("""
        DELETE FROM tareas
        WHERE idUsuario = :idUsuario
        AND idTarea = :idTarea
    """)
    suspend fun deleteTareaById(
        idUsuario: Int,
        idTarea: Int
    ): Int
}