package test.skyscanner.splash

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import kotlinx.serialization.ExperimentalSerializationApi
import test.skyscanner.common.db.Database
import test.skyscanner.common.db.countries.CountryEntity
import test.skyscanner.common.db.currency.CurrencyEntity
import test.skyscanner.common.entities.Country
import test.skyscanner.common.entities.Currency
import test.skyscanner.common.mvi.MviViewModel
import test.skyscanner.common.network.ApiService
import test.skyscanner.splash.SplashContract.FinishLoadingCommand
import test.skyscanner.splash.SplashContract.StartSearchScreen
import test.skyscanner.splash.SplashContract.State

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class SplashViewModel(
    private val apiService: ApiService,
    private val database: Database,
) : MviViewModel<State>() {

    init {
        setup {
            initialState = State()

            intent<FirstBindIntent> {
                flatMapConcat {
                    getCountriesFlow().zip(getCurrencyFlow()) { _, _ -> FinishLoadingCommand() }
                        .flowOn(Dispatchers.IO)
                }
            }

            reduce {
                when (it) {
                    is FinishLoadingCommand -> actions { listOf(StartSearchScreen()) }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun getCountriesFlow(): Flow<Unit> {
        fun getFromDbCountiesFlow(): Flow<List<Country>> = flow { emit(database.countriesDao().getAll()) }
            .map { it.map(CountryEntity::toDto) }

        fun getFromApiCountiesFlow(): Flow<List<Country>> = flow { emit(apiService.getCountries()) }
            .map { it.countries }

        fun insertCountiesFlow(countries: List<CountryEntity>): Flow<Unit> = flow {
            emit(database.countriesDao().insert(countries))
        }

        return getFromDbCountiesFlow()
            .flatMapConcat {
                if (it.isEmpty()) getFromApiCountiesFlow().flatMapConcat {
                    insertCountiesFlow(it.map(Country::toDbEntity))
                }
                else flowOf(Unit)
            }
    }

    private fun getCurrencyFlow(): Flow<Unit> {
        fun getFromDbCurrenciesFlow() = flow { emit(database.currencyDao().getAll()) }
            .map { it.map(CurrencyEntity::toDto) }

        fun getFromApiCurrenciesFlow() = flow { emit(apiService.getCurrencies()) }
            .map { it.currencies }

        fun insertCurrenciesFlow(currencies: List<CurrencyEntity>) = flow {
            emit(database.currencyDao().insert(currencies))
        }

        return getFromDbCurrenciesFlow()
            .flatMapConcat {
                if (it.isEmpty()) getFromApiCurrenciesFlow()
                    .flatMapConcat { insertCurrenciesFlow(it.map(Currency::toDbEntity)) }
                else flowOf(Unit)
            }
    }
}