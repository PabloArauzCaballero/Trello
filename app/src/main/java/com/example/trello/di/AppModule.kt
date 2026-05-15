package com.example.trello.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.trello.data.database.AppDatabase
import com.example.trello.data.database.AppDatabase.Companion.DB_NAME
import com.example.trello.data.repository.EtiquetaRepository
import com.example.trello.data.repository.TareaRepository
import com.example.trello.data.repository.UsuarioRepository
import com.example.trello.data.repository.EtiquetaXUsuarioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideUsuarioRepository(db: AppDatabase): UsuarioRepository{
        return UsuarioRepository(db)
    }

    @Provides
    @Singleton
    fun provideTareaRepository(db: AppDatabase): TareaRepository{
        return TareaRepository(db)
    }

    @Provides
    @Singleton
    fun provideEtiquetaRepository(db: AppDatabase): EtiquetaRepository{
        return EtiquetaRepository(db)
    }

    @Provides
    @Singleton
    fun provideUsuarioXEtiquetaRepository(db: AppDatabase): EtiquetaXUsuarioRepository{
        return EtiquetaXUsuarioRepository(db)
    }


    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }
}