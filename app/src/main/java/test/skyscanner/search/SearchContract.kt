package test.skyscanner.search

import android.view.MenuItem
import test.skyscanner.common.entities.Place
import test.skyscanner.common.entities.QuotesData

object SearchContract {

    data class State(
        val fromPlaces: List<Place> = listOf(),
        val toPlaces: List<Place> = listOf(),
    )

    //intents
    data class ToolbarItemClickIntent(val item: MenuItem)
    data class FromTextChangesIntent(val text: CharSequence)
    data class ToTextChangesIntent(val text: CharSequence)
    data class FromItemClickIntent(val place: Place)
    data class ToItemClickIntent(val place: Place)
    data class CalendarDateClickIntent(val date: Long)
    object SearchClickIntent

    //commands
    object EmptyPreferencesCommand
    object ToolbarItemClickCommand
    object SuggestClickCommand
    data class FromPlacesReadyCommand(val places: List<Place>)
    data class ToPlacesReadyCommand(val places: List<Place>)
    data class QuotesLoadedCommand(val data: QuotesData)
    object EmptyPlacesCommand

    //effects
    object ShowPreferencesScreenEffect
    object HideKeyboardEffect
    data class ShowQuotesEffect(val data: QuotesData)
    object ShowEmptyPlacesDialogEffect
}