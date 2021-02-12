package test.skyscanner.common.recycler

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class WrapperViewHolder<V : Any>(parent: ViewGroup) {
    val context: Context = parent.context
    val resources: Resources = parent.resources
    var listener: ((V, Bundle) -> Unit)? = null

    val itemView get() = holder.itemView

    val holder by lazy {
        object : ViewHolder(this.onCreate(parent)) {
            @Suppress("UNCHECKED_CAST")
            override fun onBindViewHolder(any: Any) = onBind(any as V)
        }
    }

    protected fun <V : View> findViewById(id: Int): V = itemView.findViewById(id)

    abstract fun onCreate(parent: ViewGroup): View
    abstract fun onBind(item: V)

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun onBindViewHolder(any: Any)
    }
}
