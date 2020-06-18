package thuytrinh.recyclerviewplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import thuytrinh.recyclerviewplayground.databinding.ActivityDividerDemoBinding
import thuytrinh.recyclerviewplayground.databinding.ItemBinding

class DividerDemoActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    with(ActivityDividerDemoBinding.inflate(LayoutInflater.from(this))) {
      setContentView(root)
      with(itemsView) {
        val linearLayoutManager = LinearLayoutManager(root.context)
        layoutManager = linearLayoutManager

        val dividerItemDecoration = DividerItemDecoration(
          root.context, linearLayoutManager.orientation
        )
        dividerItemDecoration.setDrawable(
          ContextCompat.getDrawable(
            root.context,
            R.drawable.divider
          )!!
        )
        addItemDecoration(dividerItemDecoration)

        val listAdapter = object : ListAdapter<String, StringViewHolder>(StringDiffCallback) {
          override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StringViewHolder {
            return StringViewHolder(ItemBinding.inflate(LayoutInflater.from(parent.context)))
          }

          override fun onBindViewHolder(holder: StringViewHolder, position: Int) {
            holder.bind(item = getItem(position))
          }
        }
        adapter = listAdapter
        listAdapter.submitList(
          "Frankness applauded by supported ye household. Collected favourite now for for and rapturous repulsive consulted. An seems green be wrote again. She add what own only like. Tolerably we as extremity exquisite do commanded. Doubtful offended do entrance of landlord moreover is mistress in. Nay was appear entire ladies. Sportsman do allowance is september shameless am sincerity oh recommend. Gate tell man day that who."
            .split(". ")
        )
      }
    }
  }
}

class StringViewHolder(
  private val binding: ItemBinding
) : RecyclerView.ViewHolder(binding.root) {
  fun bind(item: String) = with(binding) {
    textView.text = item
  }
}

object StringDiffCallback : DiffUtil.ItemCallback<String>() {
  override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
    return oldItem == newItem
  }

  override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
    return oldItem == newItem
  }
}
