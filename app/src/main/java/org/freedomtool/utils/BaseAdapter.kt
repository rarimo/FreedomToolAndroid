package org.freedomtool.utils

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<M, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {

    var onClick: ((M) -> Unit)? = null
    var onLongClick: ((M) -> Unit)? = null

    private var items = mutableListOf<M>()

    override fun getItemCount(): Int = this.items.size

    fun add(item: M) {
        this.items.add(item)
        notifyItemInserted(itemCount - 1)
    }

    fun addFirst(item: M) {
        this.items.add(0, item)
        notifyItemRangeInserted(0, itemCount - 1)
    }

    fun addAll(items: List<M>) {
        val insertPosition = this.itemCount
        this.items.addAll(items)
        notifyItemRangeInserted(insertPosition, items.size)
    }

    fun add(position: Int, item: M) {
        this.items.add(position - 1, item)
        notifyItemRangeInserted(position, itemCount - 1)
    }

    fun addAll(position: Int, items: List<M>) {
        this.items.addAll(position, items)
        notifyDataSetChanged()
    }

    fun replace(position: Int, item: M) {
        if (position != RecyclerView.NO_POSITION) {
            this.items[position] = item
            notifyItemChanged(position)
        }
    }

    fun replace(items: List<M>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        items.clear()
        notifyDataSetChanged()
    }

    fun remove(positionFrom: Int, positionTo: Int) {
        this.items.removeAll(this.items.subList(positionFrom, positionTo))
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        this.items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun remove(item: M) {
        val position = items.indexOf(item)
        if (position != RecyclerView.NO_POSITION) this.items.remove(item)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): M = this.items[position]

    fun getItems(): List<M> = this.items

    /**
     * @returns true if adapter has data, false otherwise
     */
    open val hasData: Boolean
        get() = items.isNotEmpty()

    protected fun inflate(parent: ViewGroup, @LayoutRes layoutRes: Int) = parent.inflate(layoutRes)
}