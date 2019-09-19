package thuytrinh.gpxanimator

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.core.animation.addListener
import androidx.core.graphics.applyCanvas
import kotlin.math.max

fun Context.createPinBitmap(colorInHex: String): Bitmap {
  val pinSize = resources.getDimensionPixelSize(R.dimen.pinSize)
  val bitmap = Bitmap.createBitmap(
    pinSize,
    pinSize,
    Bitmap.Config.ARGB_8888
  )
  val paint = Paint().apply {
    color = Color.BLACK
    style = Paint.Style.FILL
    isAntiAlias = true
  }
  return bitmap.applyCanvas {
    translate(pinSize / 2f, pinSize / 2f)
    drawCircle(0f, 0f, pinSize / 2f, paint)
    paint.apply {
      color = Color.parseColor(colorInHex)
      style = Paint.Style.FILL
    }
    drawCircle(
      0f,
      0f,
      pinSize / 2f - resources.getDimensionPixelSize(R.dimen.pinStrokeWidth),
      paint
    )
  }
}

fun createMovingAnimator(
  speedAccelerationRate: Long,
  onUpdate: (Float) -> Unit,
  onEnd: (Animator) -> Unit
): ValueAnimator {
  return ValueAnimator.ofFloat(0f, 1f).apply {
    duration = max(200 - speedAccelerationRate, 5)
    addUpdateListener { onUpdate(it.animatedValue as Float) }
    addListener(onEnd = onEnd)
  }
}
