package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StateFlowTest {
  @Test
  fun `should return initial value`() {
    val i = MutableStateFlow(69)
    assertThat(i.value).isEqualTo(69)
  }

  /**
   * Publishing states
   */
  @Test
  fun `a state flow may skip some states if emitting too fast`() = runBlocking {
    // Given
    val publisher = MutableStateFlow(0)
    val states = mutableListOf<Int>()
    val job = launch { publisher.toList(states) }
    val times = 100

    // When
    delay(1000)
    repeat(times) {
      publisher.value = it
    }
    delay(1000)

    // Then
    val expectedStates = listOf(0, times - 1)
    assertThat(states).isEqualTo(expectedStates)
    job.cancel()
  }

  /**
   * Publishing events
   */
  @Test
  fun `a buffered channel should emit all events`() = runBlocking {
    // Given
    val publisher = BroadcastChannel<Int>(capacity = Channel.BUFFERED)
    val events = mutableListOf<Int>()
    val job = launch { publisher.asFlow().toList(events) }
    val times = 100

    // When
    delay(1000)
    repeat(times) {
      publisher.send(it)
    }
    delay(1000)

    // Then
    val expectedEvents = (0 until times).toList()
    assertThat(events).isEqualTo(expectedEvents)
    job.cancel()
  }

  @Test
  fun `MutableStateFlow only emits distinct states to collectors`() = runBlocking {
    // Given
    val stateFlow = MutableStateFlow(0)
    val states = mutableListOf<Int>()

    // When
    val job = launch { stateFlow.toList(states) }
    delay(200)
    stateFlow.value = 1
    delay(200)
    stateFlow.value = 1
    delay(200)
    stateFlow.value = 1

    // Then
    // We'll receive only 2 states in spite of dispatching 3 times.
    assertThat(states).containsExactly(0, 1)
    job.cancel()
  }
}
