package test.skyscanner.preferenses

import test.skyscanner.common.entities.Country
import test.skyscanner.common.entities.Currency

object PreferencesContract {

    data class State(
        val countries: List<Country> = listOf(),
        val currencies: List<Currency> = listOf(),
        val selectedCountry: String = "",
        val selectedCurrency: String = "",
    )

    //intents
    object SaveClickIntent
    data class CountryItemClickIntent(val country: Country)
    data class CurrencyItemClickIntent(val currency: Currency)

    //commands
    data class DataLoadedCommand(
        val countries: List<Country>,
        val currencies: List<Currency>,
        val selectedCountry: String,
        val selectedCurrency: String,
    ) {
        override fun toString() = "DataLoadedCommand ${countries.count()}"
    }

    object DataSavedCommand

    //effects
    object ExitEffect
}