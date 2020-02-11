package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test

class CoroutinesTest {
  @Test
  fun test() {
    println("Start calling test() on ${Thread.currentThread().name}")
    val scope = CoroutineScope(Job() + Dispatchers.Default)
    scope.launch {
      println("Launching on ${Thread.currentThread().name}")
      withContext(Dispatchers.IO) {
        delay(2000)
        println("Done delaying on ${Thread.currentThread().name}")
      }
    }
    println("Calling test() is done")
  }
}
