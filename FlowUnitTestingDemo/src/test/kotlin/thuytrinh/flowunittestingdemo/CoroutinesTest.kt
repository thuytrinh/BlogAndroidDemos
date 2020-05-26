package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly

class CoroutinesTest {
  @Test
  fun `print thread names`() {
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

  @Test
  fun `can use toList instead of collect`() = runBlockingTest {
    // Given
    val publisher = ConflatedBroadcastChannel(0)
    val events = mutableListOf<Int>()

    // When
    // Not recommended:
    // val job = launch { publisher.asFlow().collect { events.add(it) } }
    // Recommended:
    val job = launch { publisher.asFlow().toList(events) }

    // Then
    expectThat(events).containsExactly(0)
    job.cancel()
  }
}
