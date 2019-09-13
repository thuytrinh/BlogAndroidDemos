package thuytrinh.blogandroiddemos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import thuytrinh.blogandroiddemos.databinding.ActivityItemsBinding
import thuytrinh.blogandroiddemos.databinding.ItemBinding
import java.util.UUID

class ItemsActivity : AppCompatActivity() {
  private val viewModel by viewModels<ItemsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = DataBindingUtil.setContentView<ActivityItemsBinding>(
      this,
      R.layout.activity_items
    )
    binding.itemsView.layoutManager = LinearLayoutManager(this)

    // initWithOldWay(binding)
    initWithNewWay(binding)
  }

  private fun initWithOldWay(binding: ActivityItemsBinding) {
    val adapter = ItemsAdapter(items = viewModel.items.value!!, onItemClick = {
      viewModel.clickAt(position = it)
    })
    binding.itemsView.adapter = adapter
    viewModel.items.observe(this, Observer {
      adapter.setItems(it)
    })
  }

  private fun initWithNewWay(binding: ActivityItemsBinding) {
    val adapter = NewItemsAdapter(onItemClick = {
      viewModel.clickAt(position = it)
    })
    binding.itemsView.adapter = adapter
    viewModel.items.observe(this, Observer {
      adapter.submitList(it)
    })
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

  fun setItems(newItems: List<Item>) {
    items = newItems
    notifyDataSetChanged()
  }
}

object ItemCallback : DiffUtil.ItemCallback<Item>() {
  override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
    return oldItem == newItem
  }

  override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
    return oldItem == newItem
  }
}

class NewItemsAdapter(
  private val onItemClick: (Position) -> Unit
) : ListAdapter<Item, ItemViewHolder>(ItemCallback) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
    val binding = ItemBinding.inflate(LayoutInflater.from(parent.context))
    return ItemViewHolder(binding = binding, onItemClick = onItemClick)
  }

  override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
    holder.bind(getItem(position), position)
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

class ItemsViewModel : ViewModel() {
  private val _items = MutableLiveData(emptyList<Item>())
  val items: LiveData<List<Item>> get() = _items

  init {
    _items.value = listOf(Item())
  }

  fun clickAt(position: Position) {
    _items.value = _items.value!! + Item()
  }
}
