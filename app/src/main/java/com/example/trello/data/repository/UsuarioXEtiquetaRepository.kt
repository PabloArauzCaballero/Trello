package com.example.trello.data.repository
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.EtiquetaXTarea
import javax.inject.Inject

class UsuarioXEtiquetaRepository @Inject constructor(
    private val db: AppDatabase
) {
    suspend fun asociarEtiquetaATarea(etiquetaXTarea: EtiquetaXTarea): Long?{
        return try {
            db
                .etiquetaXTareaDao()
                .asociarEtiquetaATarea(etiquetaXTarea = etiquetaXTarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun quitarTodasLasEtiquetasDeTarea(idTarea: Int): Int?{
        return try {
            db
                .etiquetaXTareaDao()
                .quitarTodasLasEtiquetasDeTarea(idTarea = idTarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    suspend fun quitarEtiquetaTarea(
        idTarea: Int,
        idEtiqueta: Int,
    ): Int?{
        return try {
            db
                .etiquetaXTareaDao()
                .quitarEtiquetaDeTarea(idTarea = idTarea, idEtiqueta = idEtiqueta)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}