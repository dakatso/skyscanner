package test.skyscanner.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.viewmodel.ext.android.viewModel
import test.skyscanner.R
import test.skyscanner.common.mvi.MviView
import test.skyscanner.search.SearchActivity
import test.skyscanner.splash.SplashContract.StartSearchScreen
import test.skyscanner.splash.SplashContract.State

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class SplashActivity : AppCompatActivity(R.layout.activity_splash), MviView<State> {
    override val viewModel: SplashViewModel by viewModel()

    init {
        init()
    }

    override fun intents(): Flow<Any> = emptyFlow()
    override fun render(state: State) {}

    override fun actions(action: Any) {
        when (action) {
            is StartSearchScreen -> {
                startActivity(Intent(this, SearchActivity::class.java))
                finish()
            }
        }
    }
}