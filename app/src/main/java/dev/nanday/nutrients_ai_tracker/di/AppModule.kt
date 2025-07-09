package dev.nanday.nutrients_ai_tracker.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.nanday.nutrients_ai_tracker.BuildConfig
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepository
import dev.nanday.nutrients_ai_tracker.data.DailyIntakeRepositoryImpl
import dev.nanday.nutrients_ai_tracker.data.FoodRepository
import dev.nanday.nutrients_ai_tracker.data.FoodRepositoryImpl
import dev.nanday.nutrients_ai_tracker.data.NutritionalTargetRepository
import dev.nanday.nutrients_ai_tracker.data.NutritionalTargetRepositoryImpl
import dev.nanday.nutrients_ai_tracker.data.OpenAiRepository
import dev.nanday.nutrients_ai_tracker.data.OpenAiRepositoryImpl
import dev.nanday.nutrients_ai_tracker.data.local.AppDatabase
import dev.nanday.nutrients_ai_tracker.data.local.NutrientsDao
import dev.nanday.nutrients_ai_tracker.data.remote.OpenAiClient
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideFoodRepository(dao: NutrientsDao): FoodRepository {
        return FoodRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDailyIntakeRepository(dao: NutrientsDao): DailyIntakeRepository {
        return DailyIntakeRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideOpenAiClient(retrofit: Retrofit): OpenAiClient {
        return retrofit.create(OpenAiClient::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenAiRepository(client: OpenAiClient): OpenAiRepository = OpenAiRepositoryImpl(client)

    @Provides
    @Singleton
    fun provideNutritionalTargetRepository(
        @ApplicationContext context: Context
    ): NutritionalTargetRepository {
        return NutritionalTargetRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE // Logga tutto: URL, headers, body
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/") // o qualsiasi base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}