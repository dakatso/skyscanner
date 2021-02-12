package test.skyscanner.quotes

import test.skyscanner.common.entities.QuotesData

object QuotesContract {

    data class State(
        val quotes: List<Any> = listOf(),
    )

    //commands
    data class DataLoadedCommand(val data: QuotesData)
}
