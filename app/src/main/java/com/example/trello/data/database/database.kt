package com.example.trello.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trello.data.daos.EtiquetaDao
import com.example.trello.data.daos.EtiquetaXTareaDao
import com.example.trello.data.daos.TareaDao
import com.example.trello.data.daos.UsuarioDao
import com.example.trello.data.entities.Etiqueta
import com.example.trello.data.entities.EtiquetaXTarea
import com.example.trello.data.entities.Tarea
import com.example.trello.data.entities.Usuario

@Database(
    entities = [
        Etiqueta::class,
        Usuario::class,
        Tarea::class,
        EtiquetaXTarea::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun etiquetaDao(): EtiquetaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun tareaDao(): TareaDao
    abstract fun etiquetaXTareaDao(): EtiquetaXTareaDao

    companion object {
        const val DB_NAME = "trello"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}