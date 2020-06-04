package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class DelayTest {
  private val dispatcher = TestCoroutineDispatcher()

  @Test
  fun test() = runBlockingTest(context = dispatcher) {
    pauseDispatcher()
    log()
  }
}

private suspend fun log() {
  delay(2000)
  println("Hello!")
}
