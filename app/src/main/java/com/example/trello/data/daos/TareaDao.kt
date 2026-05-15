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

    @Update
    suspend fun updateTarea(tarea: Tarea): Int

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

    @Query("""
        DELETE FROM etiquetaxtarea
        WHERE idTarea = :idTarea
    """)
    suspend fun deleteEtiquetasDeTarea(idTarea: Int): Int

    @Query("""
        SELECT idEtiqueta 
        FROM etiqueta
        WHERE idUsuario = :idUsuario
        AND idEtiqueta IN (:idsEtiquetas)
    """)
    suspend fun getIdsEtiquetasDelUsuario(
        idUsuario: Int,
        idsEtiquetas: List<Int>
    ): List<Int>

    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM tareas
            WHERE idUsuario = :idUsuario
            AND idTarea = :idTarea
        )
    """)
    suspend fun existeTareaDelUsuario(
        idUsuario: Int,
        idTarea: Int
    ): Boolean

    suspend fun validarEtiquetasDelUsuario(
        idUsuario: Int,
        idsEtiquetas: List<Int>
    ) {
        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        if (idsEtiquetasUnicas.isEmpty()) {
            return
        }

        val idsEtiquetasValidas = getIdsEtiquetasDelUsuario(
            idUsuario = idUsuario,
            idsEtiquetas = idsEtiquetasUnicas
        ).toSet()

        val idsEtiquetasInvalidas = idsEtiquetasUnicas.filterNot { idEtiqueta ->
            idEtiqueta in idsEtiquetasValidas
        }

        if (idsEtiquetasInvalidas.isNotEmpty()) {
            throw IllegalArgumentException(
                "Las siguientes etiquetas no existen o no pertenecen al usuario: $idsEtiquetasInvalidas"
            )
        }
    }

    @Transaction
    suspend fun insertTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>
    ): Long {
        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        validarEtiquetasDelUsuario(
            idUsuario = tarea.idUsuario,
            idsEtiquetas = idsEtiquetasUnicas
        )

        val idTareaGenerado = insertTarea(tarea)

        idsEtiquetasUnicas.forEach { idEtiqueta ->
            insertEtiquetaXTarea(
                EtiquetaXTarea(
                    idTarea = idTareaGenerado.toInt(),
                    idEtiqueta = idEtiqueta
                )
            )
        }

        return idTareaGenerado
    }

    @Transaction
    suspend fun updateTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>
    ): Int {
        val existeTareaDelUsuario = existeTareaDelUsuario(
            idUsuario = tarea.idUsuario,
            idTarea = tarea.idTarea
        )

        if (!existeTareaDelUsuario) {
            throw IllegalArgumentException(
                "La tarea no existe o no pertenece al usuario."
            )
        }

        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        validarEtiquetasDelUsuario(
            idUsuario = tarea.idUsuario,
            idsEtiquetas = idsEtiquetasUnicas
        )

        val filasActualizadas = updateTarea(tarea)

        if (filasActualizadas == 0) {
            throw IllegalArgumentException(
                "No se pudo actualizar la tarea."
            )
        }

        deleteEtiquetasDeTarea(tarea.idTarea)

        idsEtiquetasUnicas.forEach { idEtiqueta ->
            insertEtiquetaXTarea(
                EtiquetaXTarea(
                    idTarea = tarea.idTarea,
                    idEtiqueta = idEtiqueta
                )
            )
        }

        return filasActualizadas
    }
}