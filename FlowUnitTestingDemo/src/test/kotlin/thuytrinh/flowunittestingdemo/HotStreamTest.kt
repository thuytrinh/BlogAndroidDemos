package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.contains

class HotStreamTest {
  @Test
  fun `should work with callbackFlow`() = runBlockingTest {
    repeat(10_000) {
      val broadcastManager = LocalBroadcastManager()
      val values = mutableListOf<Int>()
      val job = launch {
        asFlow(broadcastManager).collect { values.add(it) }
      }

      broadcastManager.sendBroadcast(0)
      expectThat(values).contains(0)

      broadcastManager.sendBroadcast(1)
      expectThat(values).contains(0, 1)

      broadcastManager.sendBroadcast(2)
      expectThat(values).contains(0, 1, 2)

      job.cancel()
    }
  }

  @Test
  fun `should work with ConflatedBroadcastChannel`() = runBlockingTest {
    repeat(10_000) {
      val publisher = ConflatedBroadcastChannel(0)
      val values = mutableListOf<Int>()
      launch {
        publisher.asFlow().collect { values.add(it) }
      }

      publisher.offer(1)
      expectThat(values).contains(0, 1)

      publisher.offer(2)
      expectThat(values).contains(0, 1, 2)

      publisher.close()
    }
  }
}

typealias BroadcastReceiver = (Int) -> Unit
typealias Intent = Int

class LocalBroadcastManager {
  private val receivers = mutableSetOf<BroadcastReceiver>()

  fun sendBroadcast(intent: Intent) {
    receivers.forEach { it(intent) }
  }

  fun registerReceiver(receiver: BroadcastReceiver) {
    receivers.add(receiver)
  }

  fun unregisterReceiver(receiver: BroadcastReceiver) {
    receivers.remove(receiver)
  }
}

fun asFlow(broadcastManager: LocalBroadcastManager): Flow<Int> = callbackFlow {
  val receiver: BroadcastReceiver = { offer(it) }
  broadcastManager.registerReceiver(receiver)
  awaitClose { broadcastManager.unregisterReceiver(receiver) }
}
