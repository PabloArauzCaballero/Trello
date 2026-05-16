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
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Transaction
    @Query("""
        SELECT * FROM tareas 
    """)
    suspend fun getTareasConEtiquetas(): List<TareaConEtiquetas>

    @Transaction
    @Query("""
        SELECT * FROM tareas 
    """)
    fun getTareasConEtiquetasFlow(): Flow<List<TareaConEtiquetas>>

    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE idTarea = :idTarea
        LIMIT 1
    """)
    suspend fun getTareaConEtiquetasPorTarea(
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
        WHERE idTarea = :idTarea
    """)
    suspend fun deleteTareaById(
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
        WHERE idEtiqueta IN (:idsEtiquetas)
    """)
    suspend fun getIdsEtiquetas(
        idsEtiquetas: List<Int>
    ): List<Int>

    @Query("""
        SELECT EXISTS(
            SELECT 1 
            FROM tareas
            WHERE idTarea = :idTarea
        )
    """)
    suspend fun existeTarea(
        idTarea: Int
    ): Boolean

    suspend fun validarEtiquetas(
        idsEtiquetas: List<Int>
    ) {
        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        if (idsEtiquetasUnicas.isEmpty()) {
            return
        }

        val idsEtiquetasValidas = getIdsEtiquetas(
            idsEtiquetas = idsEtiquetasUnicas
        ).toSet()

        val idsEtiquetasInvalidas = idsEtiquetasUnicas.filterNot { idEtiqueta ->
            idEtiqueta in idsEtiquetasValidas
        }

        if (idsEtiquetasInvalidas.isNotEmpty()) {
            throw IllegalArgumentException(
                "Las siguientes etiquetas no existen: $idsEtiquetasInvalidas"
            )
        }
    }

    @Transaction
    suspend fun insertTareaConEtiquetas(
        tarea: Tarea,
        idsEtiquetas: List<Int>
    ): Long {
        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        validarEtiquetas(
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
        val existeTarea = existeTarea(
            idTarea = tarea.idTarea
        )

        if (!existeTarea) {
            throw IllegalArgumentException(
                "La tarea no existe."
            )
        }

        val idsEtiquetasUnicas = idsEtiquetas.distinct()

        validarEtiquetas(
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


    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE (:query IS NULL OR :query = '' OR LOWER(titulo) LIKE '%' || LOWER(:query) || '%')
        AND (:estado IS NULL OR estado = :estado)
        AND (:prioridad IS NULL OR prioridad = :prioridad)
        AND (:etiquetaId IS NULL OR EXISTS (
            SELECT 1 FROM etiquetaxtarea et
            WHERE et.idTarea = tareas.idTarea AND et.idEtiqueta = :etiquetaId
        ))
        ORDER BY titulo ASC
    """)
    fun getTareasFiltradas_PorTitulo(
        query: String?, estado: String?, prioridad: String?, etiquetaId: Int?
    ): Flow<List<TareaConEtiquetas>>

    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE (:query IS NULL OR :query = '' OR LOWER(titulo) LIKE '%' || LOWER(:query) || '%')
        AND (:estado IS NULL OR estado = :estado)
        AND (:prioridad IS NULL OR prioridad = :prioridad)
        AND (:etiquetaId IS NULL OR EXISTS (
            SELECT 1 FROM etiquetaxtarea et
            WHERE et.idTarea = tareas.idTarea AND et.idEtiqueta = :etiquetaId
        ))
        ORDER BY CASE prioridad
            WHEN 'ALTA' THEN 1
            WHEN 'MEDIA' THEN 2
            WHEN 'BAJA' THEN 3
        END ASC
    """)
    fun getTareasFiltradas_PorPrioridad(
        query: String?, estado: String?, prioridad: String?, etiquetaId: Int?
    ): Flow<List<TareaConEtiquetas>>

    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE (:query IS NULL OR :query = '' OR LOWER(titulo) LIKE '%' || LOWER(:query) || '%')
        AND (:estado IS NULL OR estado = :estado)
        AND (:prioridad IS NULL OR prioridad = :prioridad)
        AND (:etiquetaId IS NULL OR EXISTS (
            SELECT 1 FROM etiquetaxtarea et
            WHERE et.idTarea = tareas.idTarea AND et.idEtiqueta = :etiquetaId
        ))
        ORDER BY fechaCreacion DESC
    """)
    fun getTareasFiltradas_PorFechaCreacion(
        query: String?, estado: String?, prioridad: String?, etiquetaId: Int?
    ): Flow<List<TareaConEtiquetas>>

    @Transaction
    @Query("""
        SELECT * FROM tareas
        WHERE (:query IS NULL OR :query = '' OR LOWER(titulo) LIKE '%' || LOWER(:query) || '%')
        AND (:estado IS NULL OR estado = :estado)
        AND (:prioridad IS NULL OR prioridad = :prioridad)
        AND (:etiquetaId IS NULL OR EXISTS (
            SELECT 1 FROM etiquetaxtarea et
            WHERE et.idTarea = tareas.idTarea AND et.idEtiqueta = :etiquetaId
        ))
        ORDER BY (fechaVencimiento IS NULL) ASC, fechaVencimiento ASC
    """)
    fun getTareasFiltradas_PorFechaVencimiento(
        query: String?, estado: String?, prioridad: String?, etiquetaId: Int?
    ): Flow<List<TareaConEtiquetas>>
}