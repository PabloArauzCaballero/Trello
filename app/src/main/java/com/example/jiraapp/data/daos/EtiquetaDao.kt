package com.example.jiraapp.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.jiraapp.data.entities.Etiqueta

@Dao
interface EtiquetaDao {
    @Query("""
        SELECT * FROM etiqueta 
        WHERE idUsuario=:idUsuario
        """)
    suspend fun getEtiquetas(idUsuario: Int): List<Etiqueta>

    @Query("""
        SELECT * FROM etiqueta
        WHERE idEtiqueta = :idEtiqueta 
        AND idUsuario = :idUsuario
        """)
    suspend fun getEtiquetaById(idEtiqueta: Int, idUsuario: Int): Etiqueta?

    @Insert
    suspend fun insertEtiqueta(etiqueta: Etiqueta): Long

    @Update
    suspend fun updateEtiqueta(etiqueta: Etiqueta): Int

    @Delete
    suspend fun deleteEtiqueta(etiqueta: Etiqueta): Int

    @Query("""
        DELETE FROM etiqueta 
        WHERE idEtiqueta = :idEtiqueta
        AND idUsuario = :idUsuario
        """)
    suspend fun deleteEtiquetaById(idEtiqueta: Int, idUsuario: Int): Int
}