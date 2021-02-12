package test.skyscanner.common.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseViewHolder<V : Any>(parent: ViewGroup) : WrapperViewHolder<V>(parent) {
    abstract val content: Int
    override fun onCreate(parent: ViewGroup): View = LayoutInflater.from(context).inflate(content, parent, false)
    override fun onBind(item: V) {}
}