package test.skyscanner.quotes

import android.os.Bundle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi
import test.skyscanner.common.entities.Currency
import test.skyscanner.common.entities.QuotesData
import test.skyscanner.common.mvi.MviViewModel
import test.skyscanner.quotes.QuotesContract.DataLoadedCommand
import test.skyscanner.quotes.QuotesContract.State
import test.skyscanner.search.SearchActivity.Companion.ARGUMENTS_QUOTES


@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalSerializationApi
class QuotesViewModel(
    arguments: Bundle,
) : MviViewModel<State>() {

    private val quotesData = arguments.getParcelable<QuotesData>(ARGUMENTS_QUOTES)
        ?: throw IllegalArgumentException()

    init {

        setup {
            initialState = State()

            intent<FirstBindIntent> {
                flatMapConcat {
                    flowOf(DataLoadedCommand(quotesData))
                }
            }

            reduce {
                when (it) {
                    is DataLoadedCommand -> state { copy(quotes = it.data.getViewObjects()) }
                    else -> throw IllegalArgumentException()
                }
            }
        }
    }

    private fun QuotesData.getViewObjects(): List<Any> {

        fun Int.toPrice(currency: Currency) = (if (currency.symbolOnLeft) "${currency.symbol}$this"
        else "$this${currency.symbol}")

        if (this.carriers.isEmpty() || quotes.isEmpty() || currencies.isEmpty()) {
            return listOf(NoQuoteViewObject)
        }

        return quotes.map { quote ->
            val carrier = carriers.filter { it.carrierId in quote.outboundLeg.carrierIds }[0]
            QuoteViewObject(carrier.name, quote.minPrice.toPrice(currencies[0]))
        }
    }
}