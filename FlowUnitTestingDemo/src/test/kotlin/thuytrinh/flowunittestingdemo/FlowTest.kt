package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class FlowTest {
  @Test
  fun `launch with GlobalScope`() {
    GlobalScope.launch {
      flowOf(0, 1, 2, 3).collect { println(it) }
    }
    println("Done.")
  }

  @Test
  fun `launch with runBlockingTest`() {
    runBlockingTest {
      flowOf(0, 1, 2, 3).collect { println(it) }
    }
    println("Done.")
  }
}
