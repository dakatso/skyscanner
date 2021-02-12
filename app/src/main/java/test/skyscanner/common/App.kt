package test.skyscanner.common

import android.app.Application
import android.content.res.Configuration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import test.skyscanner.R
import test.skyscanner.common.di.Modules.appModule

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        locale = getString(R.string.locale)

        startKoin {
            androidLogger(Level.INFO)
            androidContext(this@App)
            modules(appModule)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        locale = getString(R.string.locale)
    }

    companion object {
        lateinit var locale: String
    }
}