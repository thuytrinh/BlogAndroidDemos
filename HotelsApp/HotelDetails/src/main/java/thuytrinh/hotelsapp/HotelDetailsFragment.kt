package thuytrinh.hotelsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.android.ext.android.inject
import thuytrinh.hotelsapp.hoteldetails.databinding.HotelDetailsBinding

class HotelDetailsFragment : Fragment() {
  private val hotelDetailsNavigator by inject<HotelDetailsNavigator>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelDetailsBinding.inflate(inflater, container, false).run {
      showHotelBookingButton.setOnClickListener {
        hotelDetailsNavigator.navigateToBooking(findNavController())
      }
      root
    }
  }
}
