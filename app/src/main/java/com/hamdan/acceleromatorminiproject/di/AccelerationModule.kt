package com.hamdan.acceleromatorminiproject.di

import android.content.Context
import androidx.room.Room
import com.hamdan.acceleromatorminiproject.data.AccelerationDao
import com.hamdan.acceleromatorminiproject.data.AccelerationDatabase
import com.hamdan.acceleromatorminiproject.domain.AccelerationRepository
import com.hamdan.acceleromatorminiproject.domain.AccelerationRepositoryImpl
import com.hamdan.acceleromatorminiproject.usecase.FetchHighScoresImpl
import com.hamdan.acceleromatorminiproject.usecase.FetchHighScoresUseCase
import com.hamdan.acceleromatorminiproject.usecase.SaveScoreImpl
import com.hamdan.acceleromatorminiproject.usecase.SaveScoreUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object AccelerationModule {
    @Provides
    fun providesAccelerationDB(@ApplicationContext context: Context): AccelerationDatabase {
        return Room.databaseBuilder(
            context,
            AccelerationDatabase::class.java,
            AccelerationDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun providesAccelerationDao(accelerationDatabase: AccelerationDatabase): AccelerationDao {
        return accelerationDatabase.accelerationDao()
    }

    @Provides
    fun providesAccelerationRepo(accelerationDao: AccelerationDao): AccelerationRepository {
        return AccelerationRepositoryImpl(accelerationDao)
    }

    @Provides
    fun providesSaveHighScoreUseCase(accelerationRepository: AccelerationRepository): SaveScoreUseCase {
        return SaveScoreImpl(accelerationRepository)
    }

    @Provides
    fun providesFetchHighScoreUseCase(accelerationRepository: AccelerationRepository): FetchHighScoresUseCase {
        return FetchHighScoresImpl(accelerationRepository)
    }
}
