package com.example.jiraapp.data.database

import androidx.room.TypeConverter
import com.example.jiraapp.data.enums.tarea.Estado
import com.example.jiraapp.data.enums.tarea.Prioridad
import java.util.Date

class Converters {
    @TypeConverter
    fun fromDate(date: Date?): Long?{
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date?{
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromPrioridad(prioridad: Prioridad?): String?{
        return prioridad?.name
    }

    @TypeConverter
    fun toPrioridad(value: String?): Prioridad?{
        return value?.let { Prioridad.valueOf(it) }
    }
    @TypeConverter
    fun fromEstadoTarea(estado: Estado?): String? {
        return estado?.name
    }

    @TypeConverter
    fun toEstadoTarea(value: String?): Estado? {
        return value?.let { Estado.valueOf(it) }
    }
}