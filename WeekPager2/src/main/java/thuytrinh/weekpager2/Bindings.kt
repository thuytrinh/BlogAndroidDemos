package thuytrinh.weekpager2

import android.animation.ValueAnimator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("isSelected")
fun ImageView.setIsSelected(isSelected: IsSelected) {
  setBackgroundResource(R.drawable.circle)
  if (isSelected.value) {
    if (isSelected.hasAnimation) {
      animate().alpha(1f)
    } else {
      alpha = 1f
    }
  } else {
    if (isSelected.hasAnimation) {
      animate().alpha(0f)
    } else {
      alpha = 0f
    }
  }
}

@BindingAdapter("isSelected")
fun TextView.setIsSelected(isSelected: IsSelected) {
  if (isSelected.value) {
    if (isSelected.hasAnimation) {
      ValueAnimator.ofArgb(
        ContextCompat.getColor(context, android.R.color.black),
        ContextCompat.getColor(context, android.R.color.white)
      ).apply {
        addUpdateListener {
          setTextColor(it.animatedValue as Int)
        }
        start()
      }
    } else {
      setTextColor(ContextCompat.getColor(context, android.R.color.white))
    }
  } else {
    if (isSelected.hasAnimation) {
      ValueAnimator.ofArgb(
        ContextCompat.getColor(context, android.R.color.white),
        ContextCompat.getColor(context, android.R.color.black)
      ).apply {
        addUpdateListener {
          setTextColor(it.animatedValue as Int)
        }
        start()
      }
    } else {
      setTextColor(ContextCompat.getColor(context, android.R.color.black))
    }
  }
}
