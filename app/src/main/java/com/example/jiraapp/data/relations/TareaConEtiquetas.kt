package com.example.jiraapp.data.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.jiraapp.data.entities.Etiqueta
import com.example.jiraapp.data.entities.EtiquetaXTarea

data class TareaConEtiquetas (
    @Embedded
    val etiqueta: Etiqueta,

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

