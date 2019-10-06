package thuytrinh.hotelsapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import thuytrinh.hotelsapp.hotellist.databinding.HotelListBinding

class HotelListFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelListBinding.inflate(inflater, container, false).run {
      root
    }
  }
}
