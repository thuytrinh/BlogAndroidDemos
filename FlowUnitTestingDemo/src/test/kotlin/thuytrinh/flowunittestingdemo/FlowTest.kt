package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains

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

  @Test
  fun `should work with callbackFlow`() {
    fun toFlow(api: CallbackApi): Flow<String> = callbackFlow {
      api.callback = {
        offer(it)
      }

      awaitClose { api.callback = {} }
    }

    repeat(1_000_000) {
      val api = CallbackApi()
      val testCollector = toFlow(api).test()

      api(0)
      testCollector.assertValues("0")

      api(1)
      testCollector.assertValues("0", "1")

      api(2)
      testCollector.assertValues("0", "1", "2")
    }
  }
}

internal class CallbackApi {
  var callback: (String) -> Unit = {}

  operator fun invoke(value: Int) {
    callback(value.toString())
  }
}

internal class TestCollector<T> {
  private val values = mutableListOf<T>()

  fun collect(value: T) {
    values.add(value)
  }

  fun assertValues(vararg _values: T) {
    expectThat(values).contains(_values.toList())
  }
}

internal fun <T> Flow<T>.test(): TestCollector<T> {
  val flow = this
  val testCollector = TestCollector<T>()
  TestCoroutineScope().launch {
    flow.collect { testCollector.collect(it) }
  }
  return testCollector
}
