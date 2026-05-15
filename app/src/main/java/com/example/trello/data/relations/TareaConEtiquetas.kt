package com.example.trello.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.trello.data.entities.Etiqueta
import com.example.trello.data.entities.EtiquetaXTarea
import com.example.trello.data.entities.Tarea

data class TareaConEtiquetas (
    @Embedded
    val tarea: Tarea,

    @Relation(
        parentColumn = "idTarea",
        entityColumn = "idEtiqueta",
        associateBy = Junction(
            value = EtiquetaXTarea::class,
            parentColumn = "idTarea",
            entityColumn = "idEtiqueta"
        )
    )
    val etiquetas: List<Etiqueta>
)

