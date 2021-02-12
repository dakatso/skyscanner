package test.skyscanner.common.recycler

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
open class BaseAdapter : Adapter<ViewHolder>() {

    val flow = MutableSharedFlow<Event>()
    val selector = TypeSelector { item, payload -> GlobalScope.launch { flow.emit(Event(item, payload)) } }
    val items: MutableList<Any> = mutableListOf()

    fun itemClicks() = flow
    fun setItems(items: List<Any>) {
        this.items.clear()
        this.items.addAll(items)
    }

    override fun getItemCount() = items.size
    override fun getItemViewType(position: Int) = selector.getItemViewType(items[position])
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = selector.onCreateViewHolder(parent, viewType)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = selector.onBindViewHolder(holder, items[position])

    protected inline fun <reified T : Any> holder(noinline types: (ViewGroup) -> WrapperViewHolder<T>) =
        selector.addType(T::class to types)

    data class Event(val item: Any, val payload: Bundle)
}