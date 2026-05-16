package com.example.trello.data.repository
import androidx.room.withTransaction
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.entities.EtiquetaXTarea
import com.example.trello.data.entities.Tarea
import com.example.trello.data.relations.TareaConEtiquetas
import com.example.trello.data.enums.tarea.Estado
import com.example.trello.data.enums.tarea.Prioridad
import com.example.trello.data.enums.tarea.TareaOrden
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TareaRepository @Inject constructor(
    private val db: AppDatabase,
    private val etiquetaXTareaRepository: EtiquetaXTareaRepository
){
    suspend fun getTareaConEtiquetaById(idTarea: Int): TareaConEtiquetas?{
        return try {
            db
                .tareaDao()
                .getTareaConEtiquetasPorTarea(idTarea = idTarea)
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
            db.withTransaction {
                val dao = db.tareaDao()
                val idsEtiquetasUnicas = idsEtiquetas.distinct()
                dao.validarEtiquetas(idsEtiquetas = idsEtiquetasUnicas)

                val idTareaGenerado = dao.insertTarea(tarea)
                idsEtiquetasUnicas.forEach { idEtiqueta ->
                    etiquetaXTareaRepository.asociarEtiquetaATarea(
                        EtiquetaXTarea(
                            idTarea = idTareaGenerado.toInt(),
                            idEtiqueta = idEtiqueta
                        )
                    )
                }
                idTareaGenerado
            }
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
            db.withTransaction {
                val dao = db.tareaDao()
                val existe = dao.existeTarea(idTarea = tarea.idTarea)
                if (!existe) {
                    throw IllegalArgumentException("La tarea no existe.")
                }

                val idsEtiquetasUnicas = idsEtiquetas.distinct()
                dao.validarEtiquetas(idsEtiquetas = idsEtiquetasUnicas)

                val filasActualizadas = dao.updateTarea(tarea)
                if (filasActualizadas == 0) {
                    throw IllegalArgumentException("No se pudo actualizar la tarea.")
                }

                val relacion = dao.getTareaConEtiquetasPorTarea(idTarea = tarea.idTarea)
                    ?: throw IllegalArgumentException("No se encontro la tarea.")

                val actuales = relacion.etiquetas.map { it.idEtiqueta }.toSet()
                val nuevas = idsEtiquetasUnicas.toSet()
                val etiquetasAEliminar = actuales - nuevas
                val etiquetasAAgregar = nuevas - actuales

                etiquetasAEliminar.forEach { idEtiqueta ->
                    etiquetaXTareaRepository.quitarEtiquetaTarea(
                        idTarea = tarea.idTarea,
                        idEtiqueta = idEtiqueta
                    )
                }

                etiquetasAAgregar.forEach { idEtiqueta ->
                    etiquetaXTareaRepository.asociarEtiquetaATarea(
                        EtiquetaXTarea(
                            idTarea = tarea.idTarea,
                            idEtiqueta = idEtiqueta
                        )
                    )
                }

                filasActualizadas
            }
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }


    suspend fun insertTarea(
        tarea: Tarea,
    ): Long?{
        return try {
            db
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
            db
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
            db
                .tareaDao()
                .deleteTarea(tarea = tarea)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    fun getTareasFiltradasFlow(
        query: String?,
        estado: Estado?,
        prioridad: Prioridad?,
        etiquetaId: Int?,
        orden: TareaOrden,
    ): Flow<List<TareaConEtiquetas>> {
        val dao = db.tareaDao()
        val q = query
        val e = estado?.name
        val p = prioridad?.name
        return when (orden) {
            TareaOrden.TITULO -> dao.getTareasFiltradas_PorTitulo(q, e, p, etiquetaId)
            TareaOrden.PRIORIDAD -> dao.getTareasFiltradas_PorPrioridad(q, e, p, etiquetaId)
            TareaOrden.FECHA_CREACION -> dao.getTareasFiltradas_PorFechaCreacion(q, e, p, etiquetaId)
            TareaOrden.FECHA_VENCIMIENTO -> dao.getTareasFiltradas_PorFechaVencimiento(q, e, p, etiquetaId)
        }
    }
}