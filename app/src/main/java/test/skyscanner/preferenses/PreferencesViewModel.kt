package test.skyscanner.preferenses

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.serialization.ExperimentalSerializationApi
import test.skyscanner.common.db.Database
import test.skyscanner.common.db.countries.CountryEntity
import test.skyscanner.common.db.currency.CurrencyEntity
import test.skyscanner.common.entities.Country
import test.skyscanner.common.entities.Currency
import test.skyscanner.common.mvi.MviViewModel
import test.skyscanner.preferenses.PreferencesContract.CountryItemClickIntent
import test.skyscanner.preferenses.PreferencesContract.CurrencyItemClickIntent
import test.skyscanner.preferenses.PreferencesContract.DataLoadedCommand
import test.skyscanner.preferenses.PreferencesContract.DataSavedCommand
import test.skyscanner.preferenses.PreferencesContract.ExitEffect
import test.skyscanner.preferenses.PreferencesContract.SaveClickIntent
import test.skyscanner.preferenses.PreferencesContract.State
import java.io.IOException


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class PreferencesViewModel(
    private val database: Database,
    private val dataStore: DataStore<Preferences>,
) : MviViewModel<State>() {

    private var selectedCountry: Country? = null
    private var selectedCurrency: Currency? = null

    init {
        setup {
            initialState = State()

            intent<FirstBindIntent> {
                flatMapConcat {
                    val countriesFlow = flow { emit(database.countriesDao().getAll()) }
                    val currenciesFlow = flow { emit(database.currencyDao().getAll()) }

                    countriesFlow.zip(currenciesFlow) { countries, currencies ->
                        Pair(countries.map(CountryEntity::toDto), currencies.map(CurrencyEntity::toDto))
                    }.flowOn(Dispatchers.IO)
                        .flatMapConcat { (counties, currencies) ->
                            dataStore.data
                                .catch {
                                    if (it is IOException) {
                                        it.printStackTrace()
                                        emit(emptyPreferences())
                                    } else throw it
                                }.flatMapConcat { prefs ->
                                    selectedCountry = counties.find { it.code == prefs[KEY_COUNTRY].orEmpty() }
                                    selectedCurrency = currencies.find { it.code == prefs[KEY_CURRENCY].orEmpty() }

                                    flowOf(DataLoadedCommand(counties, currencies,
                                        selectedCountry?.toString().orEmpty(),
                                        selectedCurrency?.toString().orEmpty()))
                                }
                        }
                }
            }

            intent<CountryItemClickIntent> {
                flatMapLatest {
                    selectedCountry = it.country
                    emptyFlow()
                }
            }

            intent<CurrencyItemClickIntent> {
                flatMapLatest {
                    selectedCurrency = it.currency
                    emptyFlow()
                }
            }

            intent<SaveClickIntent> {
                flatMapLatest {
                    flow {
                        emit(dataStore.edit { prefs ->
                            prefs[KEY_COUNTRY] = selectedCountry?.code.orEmpty()
                            prefs[KEY_CURRENCY] = selectedCurrency?.code.orEmpty()
                        })
                    }.flatMapConcat { flowOf(DataSavedCommand) }
                }
            }

            reduce {
                when (it) {
                    is DataLoadedCommand -> state {
                        this.copy(
                            countries = it.countries,
                            currencies = it.currencies,
                            selectedCountry = it.selectedCountry,
                            selectedCurrency = it.selectedCurrency
                        )
                    }
                    is DataSavedCommand -> actions { listOf(ExitEffect) }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    companion object {
        val KEY_COUNTRY = stringPreferencesKey("country")
        val KEY_CURRENCY = stringPreferencesKey("currency")
    }
}