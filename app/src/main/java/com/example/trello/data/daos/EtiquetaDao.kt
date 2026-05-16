package com.example.trello.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trello.data.entities.Etiqueta
import kotlinx.coroutines.flow.Flow

@Dao
interface EtiquetaDao {
    @Query("""
        SELECT * FROM etiqueta 
        """)
    suspend fun getEtiquetas(): List<Etiqueta>

    @Query("""
        SELECT * FROM etiqueta 
        """)
    fun getEtiquetasFlow(): Flow<List<Etiqueta>>

    @Query("""
        SELECT * FROM etiqueta
        WHERE idEtiqueta = :idEtiqueta 
        """)
    suspend fun getEtiquetaById(idEtiqueta: Int): Etiqueta?

    @Insert
    suspend fun insertEtiqueta(etiqueta: Etiqueta): Long

    @Update
    suspend fun updateEtiqueta(etiqueta: Etiqueta): Int

    @Delete
    suspend fun deleteEtiqueta(etiqueta: Etiqueta): Int

    @Query("""
        DELETE FROM etiqueta 
        WHERE idEtiqueta = :idEtiqueta
        """)
    suspend fun deleteEtiquetaById(idEtiqueta: Int): Int
}