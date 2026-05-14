package com.example.trello.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trello.data.entities.EtiquetaXTarea

@Dao
interface EtiquetaXTareaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun asociarEtiquetaATarea(
        etiquetaXTarea: EtiquetaXTarea
    ): Long

    @Query("""
        DELETE FROM etiquetaxtarea
        WHERE idTarea = :idTarea
        AND idEtiqueta = :idEtiqueta
    """)
    suspend fun quitarEtiquetaDeTarea(
        idTarea: Int,
        idEtiqueta: Int
    ): Int

    @Query("""
        DELETE FROM etiquetaxtarea
        WHERE idTarea = :idTarea
    """)
    suspend fun quitarTodasLasEtiquetasDeTarea(
        idTarea: Int
    ): Int

}