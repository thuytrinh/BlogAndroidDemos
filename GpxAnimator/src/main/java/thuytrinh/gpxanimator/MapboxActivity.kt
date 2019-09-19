package thuytrinh.gpxanimator

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Line
import com.mapbox.mapboxsdk.plugins.annotation.LineManager
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import thuytrinh.gpxanimator.databinding.ActivityMapboxBinding

const val START_PIN_ICON_NAME = "startPin"
const val END_PIN_ICON_NAME = "endPin"
const val MOVING_PIN_ICON_NAME = "movingPin"

class MapboxActivity : AppCompatActivity() {
  private val viewModel by viewModels<GpxViewModel>()
  private var lastLine: Line? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    Mapbox.getInstance(
      this,
      "pk.eyJ1IjoidHJpbmhuZ29jdGh1eSIsImEiOiJjazBxd2FoZzEwMDJjM2ZxbGhwaW56N2JwIn0.28TuWVsGG898tOWXuz3nmw"
    )
    val binding = DataBindingUtil.setContentView<ActivityMapboxBinding>(
      this,
      R.layout.activity_mapbox
    )

    binding.mapView.onCreate(savedInstanceState)
    binding.mapView.getMapAsync { map ->
      map.setStyle(Style.MAPBOX_STREETS) { style ->
        val lineManager = LineManager(binding.mapView, map, style)
        val symbolManager = SymbolManager(binding.mapView, map, style)
        val padding = resources.getDimensionPixelSize(R.dimen.mapPadding)

        style.addImage(START_PIN_ICON_NAME, createPinBitmap("#A3BE8C"))
        style.addImage(END_PIN_ICON_NAME, createPinBitmap("#BF616A"))
        style.addImage(MOVING_PIN_ICON_NAME, createPinBitmap("#EBCB8B"))

        viewModel.wayPoints.observe(this, Observer { wayPoints ->
          val allPoints = wayPoints
            .map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }

          val pointsToAnimate = mutableListOf(
            allPoints[0],
            allPoints[1]
          )

          fun animate(movingPin: Symbol, speedAccelerationRate: Long = 0) {
            val startPoint = pointsToAnimate[pointsToAnimate.lastIndex - 1]
            val endPoint = pointsToAnimate[pointsToAnimate.lastIndex]

            createMovingAnimator(
              speedAccelerationRate = speedAccelerationRate,
              onUpdate = { animatedValue ->
                val movingPoint = LatLng(
                  startPoint.latitude + (endPoint.latitude - startPoint.latitude) * animatedValue,
                  startPoint.longitude + (endPoint.longitude - startPoint.longitude) * animatedValue
                )

                lastLine?.let { lineManager.delete(it) }
                lastLine = lineManager.create(
                  LineOptions()
                    .withLatLngs(listOf(startPoint, movingPoint))
                    .withLineWidth(3f)
                    .withLineColor("#4C566A")
                )

                movingPin.latLng = movingPoint
                symbolManager.updateSource()

                val pointsToComputeBounds = pointsToAnimate.minus(endPoint) + movingPoint
                map.moveCamera(
                  CameraUpdateFactory.newLatLngBounds(
                    pointsToComputeBounds.fold(
                      LatLngBounds.Builder(),
                      { bounds, point -> bounds.include(point) }
                    ).build(),
                    padding
                  )
                )
              },
              onEnd = {
                lastLine?.let { lineManager.delete(it) }
                lastLine = null

                lineManager.create(
                  LineOptions()
                    .withLatLngs(listOf(startPoint, endPoint))
                    .withLineWidth(3f)
                    .withLineColor("#4C566A")
                )

                if (pointsToAnimate.size != allPoints.size) {
                  pointsToAnimate.add(allPoints[pointsToAnimate.lastIndex + 1])
                  animate(movingPin, speedAccelerationRate + 5)
                }
              }
            ).start()
          }

          animate(
            symbolManager.create(
              SymbolOptions()
                .withIconImage(MOVING_PIN_ICON_NAME)
                .withLatLng(pointsToAnimate.first())
            )
          )
        })

        viewModel.startPoint.observe(this, Observer {
          symbolManager.create(
            SymbolOptions()
              .withIconImage(START_PIN_ICON_NAME)
              .withLatLng(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
          )
        })

        viewModel.endPoint.observe(this, Observer {
          symbolManager.create(
            SymbolOptions()
              .withIconImage(END_PIN_ICON_NAME)
              .withLatLng(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
          )
        })
      }
    }
  }
}
