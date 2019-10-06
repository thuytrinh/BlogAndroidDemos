package thuytrinh.hotelsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import thuytrinh.hotelsapp.hotelsappnav.R

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      val navHostFragment = NavHostFragment.create(R.navigation.nav_graph)
      supportFragmentManager
        .beginTransaction()
        .add(android.R.id.content, navHostFragment)
        // To intercept system Back button presses.
        .setPrimaryNavigationFragment(navHostFragment)
        .commit()
    }
  }
}
