package thuytrinh.weekpager2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

typealias Item = Int

class ItemsAdapter : ListAdapter<Item, ItemViewHolder>(
  object : DiffUtil.ItemCallback<Int>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
      return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
      return oldItem == newItem
    }
  }
) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val itemView = LayoutInflater.from(parent.context)
      .inflate(R.layout.item, parent, false)
    return ItemViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Item) {
    holder.bindTo(getItem(position))
  }
}
