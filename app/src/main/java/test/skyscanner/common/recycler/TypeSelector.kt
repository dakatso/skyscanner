package test.skyscanner.common.recycler

import android.os.Bundle
import android.view.ViewGroup
import androidx.collection.ArrayMap
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

class TypeSelector(private val listener: ((Any, Bundle) -> Unit)? = null) {

    private val store = ArrayMap<KClass<*>, Item>()

    fun addType(type: Pair<KClass<*>, (ViewGroup) -> WrapperViewHolder<*>>) {
        store[type.first] = Item(store.size, type.second)
    }

    fun getItemViewType(itemClass: Any) = store[itemClass::class]?.viewType
        ?: throw NoSuchElementException("No type added ${itemClass::class.java}")

    fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = store.values.first { it.viewType == viewType }
        .factory(parent).also { it.listener = listener }.holder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
        (holder as WrapperViewHolder.ViewHolder).onBindViewHolder(item)
    }

    private data class Item(val viewType: Int, val factory: (ViewGroup) -> WrapperViewHolder<*>)
}

