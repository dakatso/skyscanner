package test.skyscanner.search

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import test.skyscanner.common.App
import test.skyscanner.common.entities.Place
import test.skyscanner.common.mvi.MviViewModel
import test.skyscanner.common.network.ApiService
import test.skyscanner.preferenses.PreferencesViewModel.Companion.KEY_COUNTRY
import test.skyscanner.preferenses.PreferencesViewModel.Companion.KEY_CURRENCY
import test.skyscanner.search.SearchContract.CalendarDateClickIntent
import test.skyscanner.search.SearchContract.EmptyPlacesCommand
import test.skyscanner.search.SearchContract.EmptyPreferencesCommand
import test.skyscanner.search.SearchContract.FromItemClickIntent
import test.skyscanner.search.SearchContract.FromPlacesReadyCommand
import test.skyscanner.search.SearchContract.FromTextChangesIntent
import test.skyscanner.search.SearchContract.HideKeyboardEffect
import test.skyscanner.search.SearchContract.QuotesLoadedCommand
import test.skyscanner.search.SearchContract.SearchClickIntent
import test.skyscanner.search.SearchContract.ShowEmptyPlacesDialogEffect
import test.skyscanner.search.SearchContract.ShowPreferencesScreenEffect
import test.skyscanner.search.SearchContract.ShowQuotesEffect
import test.skyscanner.search.SearchContract.State
import test.skyscanner.search.SearchContract.SuggestClickCommand
import test.skyscanner.search.SearchContract.ToItemClickIntent
import test.skyscanner.search.SearchContract.ToPlacesReadyCommand
import test.skyscanner.search.SearchContract.ToTextChangesIntent
import test.skyscanner.search.SearchContract.ToolbarItemClickCommand
import test.skyscanner.search.SearchContract.ToolbarItemClickIntent
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class SearchViewModel(
    private val dataStore: DataStore<Preferences>,
    private val apiService: ApiService,
) : MviViewModel<State>() {

    private var selectedPlaceFrom: Place? = null
    private var selectedPlaceTo: Place? = null
    private var selectedDate = 0L
    private var countryCode: String = ""
    private var currencyCode: String = ""

    init {
        setup {
            initialState = State()

            intent<FirstBindIntent> {
                flatMapConcat {
                    getPreferences(dataStore)
                        .flatMapConcat {
                            if (it.asMap().isEmpty()) flowOf(EmptyPreferencesCommand)
                            else flow {
                                countryCode = it[KEY_COUNTRY].orEmpty()
                                currencyCode = it[KEY_CURRENCY].orEmpty()
                            }
                        }
                }
            }

            intent<ToolbarItemClickIntent> {
                map { ToolbarItemClickCommand }
            }

            intent<FromTextChangesIntent> {
                map { it.text }
                    .handleTextChange()
                    .map(::FromPlacesReadyCommand)
            }

            intent<ToTextChangesIntent> {
                map { it.text }
                    .handleTextChange()
                    .map(::ToPlacesReadyCommand)
            }

            intent<FromItemClickIntent> {
                flatMapConcat {
                    flow {
                        selectedPlaceFrom = it.place
                        emit(SuggestClickCommand)
                    }
                }
            }

            intent<ToItemClickIntent> {
                flatMapConcat {
                    flow {
                        selectedPlaceTo = it.place
                        emit(SuggestClickCommand)
                    }
                }
            }

            intent<CalendarDateClickIntent> {
                flatMapConcat {
                    flow {
                        selectedDate = it.date
                    }
                }
            }

            intent<SearchClickIntent> {

                val getQuotesFlow = flow {
                    emit(
                        apiService.getQuotes(
                            countryCode, currencyCode, App.locale,
                            selectedPlaceFrom?.placeId.orEmpty(),
                            selectedPlaceTo?.placeId.orEmpty(),
                            selectedDate.formatDate()
                        )
                    )
                }

                flatMapLatest {
                    flowOf(selectedPlaceFrom != null && selectedPlaceTo != null)
                        .flatMapConcat {
                            if (it) getQuotesFlow.map { QuotesLoadedCommand(it) }
                            else flowOf(EmptyPlacesCommand)
                        }
                }
            }

            reduce {
                when (it) {
                    is EmptyPreferencesCommand, ToolbarItemClickCommand -> actions { listOf(ShowPreferencesScreenEffect) }
                    is FromPlacesReadyCommand -> state { copy(fromPlaces = it.places) }
                    is ToPlacesReadyCommand -> state { copy(toPlaces = it.places) }
                    is QuotesLoadedCommand -> actions { listOf(ShowQuotesEffect(it.data)) }
                    is SuggestClickCommand -> actions { listOf(HideKeyboardEffect) }
                    is EmptyPlacesCommand -> actions { listOf(ShowEmptyPlacesDialogEffect) }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun Long.formatDate() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(this))

    private fun Flow<CharSequence>.handleTextChange(): Flow<List<Place>> {
        return filter { it.isNotBlank() }
            .debounce(DEBOUNCE_TIME)
            .flatMapLatest { text ->
                if (text.isEmpty()) {
                    return@flatMapLatest emptyFlow()
                }

                flow { emit(apiService.getPlaces(countryCode, currencyCode, App.locale, text.toString())) }
                    .map { it.places }
            }
    }

    private fun getPreferences(dataStore: DataStore<Preferences>): Flow<Preferences> {
        return dataStore.data
            .catch {
                if (it is IOException) {
                    it.printStackTrace()
                    emit(emptyPreferences())
                } else throw it
            }
    }

    companion object {
        const val DEBOUNCE_TIME = 200L //ms
    }
}