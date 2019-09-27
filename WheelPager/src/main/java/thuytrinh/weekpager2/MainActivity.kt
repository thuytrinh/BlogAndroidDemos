package thuytrinh.weekpager2

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
  private val logTag = MainActivity::class.java.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    populateItems()

    viewPager.setPageTransformer { page: View, position: Float ->
      page.apply {
        // To allow only rotation around the pivot point.
        x = 0f
        y = 0f

        // To adjust pivot point correctly based on ViewPager orientation.
        when {
          viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL -> {
            pivotX = width.toFloat() / 2f
            pivotY = height.toFloat()
          }
          viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL -> {
            pivotX = width.toFloat()
            pivotY = height.toFloat() / 2f
          }
        }

        val direction = when {
          viewPager.orientation == ViewPager2.ORIENTATION_HORIZONTAL -> 1f
          viewPager.orientation == ViewPager2.ORIENTATION_VERTICAL -> -1f
          else -> error("Oops! Sorry, this only works for vertical & horizontal.")
        }

        when {
          position == 0f -> {
            // When the page is front-and-center.
            Log.i(logTag, "${page.tag} is now a front-and-center page")

            rotation = direction * position * 180
          }
          position == 1f -> {
            // When the page is a full page to the right (or bottom in vertical orientation).
            Log.i(logTag, "${page.tag} is now a full page to the right")

            rotation = direction * position * 180
          }
          position == -1f -> {
            // When the page is a full page to the left (or up in vertical orientation).
            Log.i(logTag, "${page.tag} is now a full page to the left")

            rotation = direction * position * 180
          }
          position < 0 && position > -1f -> {
            // [-1, 0]
            // This is when moving a page in the LHS.
            Log.i(logTag, "${page.tag} is moving w/ position $position")

            rotation = direction * position * 180
          }
          position < 1 && position > 0f -> {
            // (0, 1]
            // This is when moving a page in the RHS.
            Log.i(logTag, "${page.tag} is moving w/ position $position")

            rotation = direction * position * 180
          }
          else -> {
            // This is when `position` is out of the bounds of `[-1, 1]`.
            // In other words, when the page is way off-screen.
            Log.i(logTag, "${page.tag} is way off-screen")
          }
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main, menu)
    val item = menu?.findItem(R.id.orientation)
    item?.title = viewPager.orientationText()
    return super.onCreateOptionsMenu(menu)
  }

  private fun ViewPager2.orientationText(): CharSequence? {
    return when (orientation) {
      ViewPager2.ORIENTATION_HORIZONTAL -> "Horizontal"
      ViewPager2.ORIENTATION_VERTICAL -> "Vertical"
      else -> error("Oops! Sorry, this only works for vertical & horizontal.")
    }
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    return when {
      item?.itemId == R.id.orientation -> {
        toggleOrientation()
        populateItems()
        item.title = viewPager.orientationText()
        true
      }
      else -> false
    }
  }

  private fun toggleOrientation() {
    viewPager.orientation = when (viewPager.orientation) {
      ViewPager2.ORIENTATION_HORIZONTAL -> ViewPager2.ORIENTATION_VERTICAL
      ViewPager2.ORIENTATION_VERTICAL -> ViewPager2.ORIENTATION_HORIZONTAL
      else -> error("Oops! Sorry, this only works for vertical & horizontal.")
    }
  }

  private fun populateItems() {
    viewPager.adapter = ItemsAdapter().apply {
      val items = (0..10).toList()
      submitList(items)
    }
  }
}
