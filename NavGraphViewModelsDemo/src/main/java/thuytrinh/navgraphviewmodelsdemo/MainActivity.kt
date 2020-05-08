package thuytrinh.navgraphviewmodelsdemo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NavHostFragment.create(R.navigation.main).apply {
      supportFragmentManager
        .beginTransaction()
        .replace(android.R.id.content, this)
        .setPrimaryNavigationFragment(this)
        .commit()
    }
  }
}

private const val KEY_RESULT = "result"

class ItemsFragment : Fragment(R.layout.fragment_items) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<TextView>(R.id.itemsTextView).setOnClickListener {
      findNavController().navigate(R.id.toItemDetails)
    }
    findNavController().currentBackStackEntry?.apply {
      savedStateHandle.getLiveData<Int>(KEY_RESULT).observe(viewLifecycleOwner, Observer {
        Toast.makeText(requireContext(), "Result: $it", Toast.LENGTH_SHORT).show()
      })
    }
  }
}

class ItemDetailsFragment : Fragment(R.layout.fragment_item_details) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.findViewById<TextView>(R.id.itemDetailsTextView).setOnClickListener {
      findNavController().previousBackStackEntry?.savedStateHandle?.set(KEY_RESULT, 1)
      findNavController().popBackStack()
    }
  }
}
