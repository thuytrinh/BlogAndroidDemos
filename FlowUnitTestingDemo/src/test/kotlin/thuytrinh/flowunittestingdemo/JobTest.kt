package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JobTest {
  @Test
  fun `why?`() = runBlocking<Unit> {
    // Given
    val channel = BroadcastChannel<Int>(capacity = Channel.BUFFERED)
    var onCompletion = false
    var onStart = false
    val events = mutableListOf<Int>()
    val computationContext = this.coroutineContext
    // val computationContext = Dispatchers.Unconfined
    // val computationContext = Dispatchers.IO

    suspend fun doWork() {
      withContext(computationContext) {
        channel.asFlow()
          .onStart { onStart = true }
          .onCompletion { onCompletion = true }
          .onCompletion { println("onCompletion") }
          .onEach { println("onEach: $it") }
          .toList(events)
      }
    }

    // When
    val job = launch { doWork() }
    delay(1000)
    channel.send(0)
    delay(1000)
    channel.send(1)
    delay(2000)
    job.cancel()

    // Then
    delay(2000)
    assertThat(onStart)
      .describedAs("onStart was $onStart")
      .isTrue()
    assertThat(events).containsExactly(0, 1)
    assertThat(onCompletion)
      .describedAs("onCompletion was $onCompletion")
      .isTrue()
  }
}
