package thuytrinh.hotelsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import thuytrinh.hotelsapp.hotelbooking.databinding.HotelBookingBinding

class HotelBookingFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelBookingBinding.inflate(inflater, container, false).run {
      root
    }
  }
}
