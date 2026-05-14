package com.example.trello.data.repository
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.Etiqueta
import javax.inject.Inject

class EtiquetaRepository @Inject constructor(
    private val db: AppDatabase
){
    suspend fun getEtiquetasPorUsuario(idUsuario: Int): List<Etiqueta>{
        return try {
            db
                .etiquetaDao()
                .getEtiquetas(idUsuario)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getEtiquetasPorUsuarioYId(idUsuario: Int, idEtiqueta: Int): Etiqueta?{
        return try{
            db
                .etiquetaDao()
                .getEtiquetaById(idEtiqueta = idEtiqueta, idUsuario=idUsuario)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun insertEtiqueta(etiqueta: Etiqueta): Long?{
        return try {
            db
                .etiquetaDao()
                .insertEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun updateEtiqueta(etiqueta: Etiqueta): Int?{
        return try {
            db
                .etiquetaDao()
                .updateEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEtiqueta(etiqueta: Etiqueta): Int?{
        return try {
            db
                .etiquetaDao()
                .deleteEtiqueta(etiqueta)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteEtiquetaById(idEtiqueta: Int, idUsuario: Int): Int?{
        return try {
            db
                .etiquetaDao()
                .deleteEtiquetaById(idEtiqueta, idUsuario = idUsuario)
        } catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}