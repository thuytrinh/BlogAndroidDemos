package thuytrinh.weekpager2

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
  private val textView: TextView = itemView.findViewById(R.id.textView)

  fun bindTo(item: Int) {
    textView.text = item.toString()
    val backgroundColor = when {
      item % 2 == 0 -> Color.parseColor("#909090")
      else -> Color.parseColor("#D0D0D0")
    }
    itemView.setBackgroundColor(backgroundColor)

    // Just for debugging purpose.
    itemView.tag = "Page #$item"
  }
}
