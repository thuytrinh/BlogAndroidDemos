package thuytrinh.dialogplayground

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import kotlinx.coroutines.delay
import thuytrinh.dialogplayground.databinding.FragmentHomeBinding

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navHostFragment = supportFragmentManager
      .findFragmentById(R.id.navHost) as NavHostFragment
    navHostFragment.navController.apply {
      graph = createGraph(NavGraph.Id, NavGraph.Dest.Home) {
        fragment<HomeFragment>(NavGraph.Dest.Home)
      }
    }
  }
}

object NavGraph {
  const val Id = 0

  object Dest {
    const val Home = 1
  }
}

class HomeFragment : Fragment() {
  private var dialog: AlertDialog? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return FragmentHomeBinding.inflate(inflater).apply {
      clickMe.setOnClickListener {
        lifecycleScope.launchWhenResumed {
          repeat(6) {
            delay(1500)
            if (it % 2 == 0) {
              if (dialog == null) {
                dialog = AlertDialog.Builder(requireContext())
                  .create()
              }
              dialog?.apply {
                setMessage("Time: $it")
                show()
              }
            } else {
              dialog?.dismiss()
            }
          }
        }
      }
    }.root
  }
}
