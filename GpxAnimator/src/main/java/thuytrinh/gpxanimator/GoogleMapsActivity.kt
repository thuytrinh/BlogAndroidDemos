package thuytrinh.gpxanimator

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class GoogleMapsActivity : AppCompatActivity() {
  private val viewModel by viewModels<GpxViewModel>()
  private var lastLine: Polyline? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_google_maps)

    val mapFragment = supportFragmentManager
      .findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync { map ->
      val padding = resources.getDimensionPixelSize(R.dimen.mapPadding)
      viewModel.wayPoints.observe(this, Observer { wayPoints ->
        val allPoints = wayPoints
          .map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }

        val pointsToAnimate = mutableListOf(
          allPoints[0],
          allPoints[1]
        )

        fun animate(movingPin: Marker, speedAccelerationRate: Long = 0) {
          val startPoint = pointsToAnimate[pointsToAnimate.lastIndex - 1]
          val endPoint = pointsToAnimate[pointsToAnimate.lastIndex]

          createMovingAnimator(
            speedAccelerationRate = speedAccelerationRate,
            onUpdate = { animatedValue ->
              val movingPoint = LatLng(
                startPoint.latitude + (endPoint.latitude - startPoint.latitude) * animatedValue,
                startPoint.longitude + (endPoint.longitude - startPoint.longitude) * animatedValue
              )

              lastLine?.remove()
              lastLine = map.addPolyline(
                PolylineOptions().apply {
                  add(startPoint, movingPoint)
                  color(Color.parseColor("#4C566A"))
                }
              )

              movingPin.position = movingPoint

              val pointsToComputeBounds = pointsToAnimate.minus(endPoint) + movingPoint
              map.moveCamera(
                CameraUpdateFactory.newLatLngBounds(
                  pointsToComputeBounds.fold(
                    LatLngBounds.Builder(),
                    { bounds, point -> bounds.include(point) }
                  ).build(),
                  padding
                ))
            },
            onEnd = {
              lastLine?.remove()
              lastLine = null

              map.addPolyline(
                PolylineOptions().apply {
                  add(startPoint, endPoint)
                  color(Color.parseColor("#4C566A"))
                }
              )

              if (pointsToAnimate.size != allPoints.size) {
                pointsToAnimate.add(allPoints[pointsToAnimate.lastIndex + 1])
                animate(movingPin, speedAccelerationRate + 5)
              }
            }
          ).start()
        }

        animate(
          map.addMarker(
            MarkerOptions()
              .icon(BitmapDescriptorFactory.fromBitmap(createPinBitmap("#EBCB8B")))
              .position(pointsToAnimate.first())
          )
        )
      })

      viewModel.startPoint.observe(this, Observer {
        map.addMarker(
          MarkerOptions()
            .icon(BitmapDescriptorFactory.fromBitmap(createPinBitmap("#A3BE8C")))
            .position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
        )
      })

      viewModel.endPoint.observe(this, Observer {
        map.addMarker(
          MarkerOptions()
            .icon(BitmapDescriptorFactory.fromBitmap(createPinBitmap("#BF616A")))
            .position(LatLng(it.latitude.toDouble(), it.longitude.toDouble()))
        )
      })
    }
  }
}
