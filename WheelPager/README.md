# WheelPager

This demo shows how to create a wheel-like page transformation for [`ViewPager2`](https://developer.android.com/reference/androidx/viewpager2/widget/ViewPager2) (that supports both vertical & horizontal page transition). Wanna see how it works? Check out https://youtu.be/clr3feRaHtM. The key to make it work is to implement a `PageTransformer` which causes the page items rotating around a pivot point like below:

```kotlin
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
```
