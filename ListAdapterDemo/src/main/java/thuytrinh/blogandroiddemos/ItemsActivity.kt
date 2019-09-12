package thuytrinh.blogandroiddemos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import thuytrinh.blogandroiddemos.databinding.ActivityItemsBinding
import thuytrinh.blogandroiddemos.databinding.ItemBinding
import java.util.UUID

class ItemsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityItemsBinding>(
      this,
      R.layout.activity_items
    )
  }
}

data class Item(val id: String = UUID.randomUUID().toString())
typealias Position = Int

class ItemsAdapter(
  private var items: List<Item> = emptyList(),
  private val onItemClick: (Position) -> Unit
) : RecyclerView.Adapter<ItemViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding = ItemBinding.inflate(LayoutInflater.from(parent.context))
    return ItemViewHolder(binding = binding, onItemClick = onItemClick)
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.bind(items[position], position)
  }
}

class ItemViewHolder(
  private val binding: ItemBinding,
  private val onItemClick: (Position) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(item: Item, position: Position) {
    binding.textView.text = item.id
    binding.root.setOnClickListener { onItemClick(position) }
  }
}
