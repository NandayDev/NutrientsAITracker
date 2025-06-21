package dev.nanday.nutrients_ai_tracker.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepositoryImpl
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase
import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app, AppDatabase::class.java, "food-db"
        ).build()
    }

    @Provides
    fun provideDao(db: AppDatabase): NutrientsDao = db.dao()

    @Provides
    @Singleton
    fun provideFoodRepository(dao: NutrientsDao): DailyIntakeRepository {
        return DailyIntakeRepositoryImpl(dao)
    }
}