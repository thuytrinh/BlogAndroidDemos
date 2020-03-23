package thuytrinh.fragmentcontainerviewdemo

import android.Manifest.permission
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import thuytrinh.fragmentcontainerviewdemo.databinding.HotelsBinding

class HotelsFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return HotelsBinding.inflate(inflater, container, false).apply {
      requestButton.setOnClickListener {
        val launcher = prepareCall(ActivityResultContracts.RequestPermissions()) { results ->
          if (results.values.all { it }) {
            Toast.makeText(requireContext(), "Thanks!", Toast.LENGTH_SHORT).show()
          } else {
            Toast.makeText(requireContext(), "Oops! (ಥ﹏ಥ)", Toast.LENGTH_SHORT).show()
          }
        }
        launcher.launch(
          arrayOf(
            permission.ACCESS_COARSE_LOCATION
          )
        )
      }
    }.root
  }
}
