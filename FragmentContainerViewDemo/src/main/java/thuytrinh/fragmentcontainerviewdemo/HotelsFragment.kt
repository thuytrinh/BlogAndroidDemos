package thuytrinh.fragmentcontainerviewdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import thuytrinh.fragmentcontainerviewdemo.databinding.HotelsBinding

class HotelsFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelsBinding.inflate(inflater, container, false).root
  }
}
