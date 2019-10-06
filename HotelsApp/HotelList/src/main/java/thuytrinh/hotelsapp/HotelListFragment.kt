package thuytrinh.hotelsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import thuytrinh.hotelsapp.hotellist.databinding.HotelListBinding

class HotelListFragment : Fragment() {
  private val hotelListNavigator by inject<HotelListNavigator>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelListBinding.inflate(inflater, container, false).run {
      showHotelDetailsButton.setOnClickListener {
        hotelListNavigator.navigateToHotelDetails(findNavController())
      }
      root
    }
  }
}
