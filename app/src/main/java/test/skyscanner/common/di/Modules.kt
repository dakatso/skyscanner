package test.skyscanner.common.di

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import test.skyscanner.common.db.Database
import test.skyscanner.common.network.ApiService
import test.skyscanner.preferenses.PreferencesViewModel
import test.skyscanner.quotes.QuotesViewModel
import test.skyscanner.search.SearchViewModel
import test.skyscanner.splash.SplashViewModel

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
object Modules {
    val appModule = module {
        single {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(loggingInterceptor)
                        .build()
                )
                .addConverterFactory(Json { ignoreUnknownKeys = true }.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(ApiService::class.java)
        }
        single { PreferenceDataStoreFactory.create { get<Context>().preferencesDataStoreFile(DATASTORE) } }

        single { Room.databaseBuilder(get(), Database::class.java, Database.NAME).build() }

        viewModel { SplashViewModel(get(), get()) }
        viewModel { SearchViewModel(get(), get()) }
        viewModel { PreferencesViewModel(get(), get()) }
        viewModel { QuotesViewModel(it[0]) }

    }

    private const val BASE_URL = "https://partners.api.skyscanner.net/apiservices/"
    private const val DATASTORE = "datastore"
}