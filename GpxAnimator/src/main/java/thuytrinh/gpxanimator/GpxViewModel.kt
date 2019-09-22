package thuytrinh.gpxanimator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.jenetics.jpx.GPX
import io.jenetics.jpx.WayPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GpxViewModel(
  private val app: Application
) : AndroidViewModel(app) {
  val wayPoints = MutableLiveData<List<WayPoint>>()
  val startPoint = MutableLiveData<WayPoint>()
  val endPoint = MutableLiveData<WayPoint>()

  init {
    viewModelScope.launch(context = Dispatchers.IO) {
      val stream = app.assets.open("2019-09-18_94596525_nidda.gpx")
      // val stream = app.assets.open("2019-08-08_84768262_nidda-heddernheim-mainufer-frankfurt.gpx")
      val gpx = GPX.read(stream)
      val points = gpx.tracks
        .flatMap { it.segments }
        .flatMap { it.points }
      wayPoints.postValue(points)
      startPoint.postValue(points.first())
      endPoint.postValue(points.last())
    }
  }
}
