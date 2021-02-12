package test.skyscanner.quotes

import android.view.ViewGroup
import android.widget.TextView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import test.skyscanner.R
import test.skyscanner.common.recycler.BaseAdapter
import test.skyscanner.common.recycler.BaseViewHolder

@ExperimentalCoroutinesApi
class QuotesAdapter : BaseAdapter() {
    init {
        holder(::QuoteViewHolder)
        holder(::NoQuoteViewHolder)
    }
}

data class QuoteViewObject(
    val carrier: String = "",
    val price: String = "",
)

class QuoteViewHolder(parent: ViewGroup) : BaseViewHolder<QuoteViewObject>(parent) {
    override val content = R.layout.item_quote
    private val tvCarrier = findViewById<TextView>(R.id.tv_carrier)
    private val tvPrice = findViewById<TextView>(R.id.tv_price)

    override fun onBind(item: QuoteViewObject) {
        tvCarrier.text = item.carrier
        tvPrice.text = item.price
    }
}

object NoQuoteViewObject

class NoQuoteViewHolder(parent: ViewGroup) : BaseViewHolder<NoQuoteViewObject>(parent) {
    override val content = R.layout.item_no_qoute

    override fun onBind(item: NoQuoteViewObject) {
    }
}


