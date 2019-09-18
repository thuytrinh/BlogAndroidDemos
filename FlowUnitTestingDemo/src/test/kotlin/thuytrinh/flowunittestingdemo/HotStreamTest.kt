package thuytrinh.flowunittestingdemo

import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
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
import strikt.assertions.isEqualTo

class HotStreamTest {
  @Test
  fun `should receive values from BehaviorSubject`() {
    val publisher = BehaviorSubject.createDefault(0)
    val observer: TestObserver<Int> = publisher.test()

    publisher.onNext(1)
    publisher.onNext(2)

    observer.assertValues(0, 1, 2)
  }

  @Test
  fun `should work with callbackFlow`() = runBlockingTest {
    val broadcastManager = LocalBroadcastManager()
    val values = mutableListOf<Int>()
    val job = launch {
      broadcastManager.asFlow().collect { values.add(it) }
    }

    broadcastManager.sendBroadcast(0)
    expectThat(values).contains(0)

    broadcastManager.sendBroadcast(1)
    expectThat(values).contains(0, 1)

    broadcastManager.sendBroadcast(2)
    expectThat(values).contains(0, 1, 2)

    job.cancel()
  }

  @Test
  fun `should work with ConflatedBroadcastChannel`() = runBlockingTest {
    val publisher = ConflatedBroadcastChannel(0)
    val values = mutableListOf<Int>()

    val job = launch {
      publisher.asFlow().collect { values.add(it) }
    }
    expectThat(publisher.value).isEqualTo(0)

    publisher.offer(1)
    expectThat(values).contains(0, 1)
    expectThat(publisher.value).isEqualTo(1)

    publisher.offer(2)
    expectThat(values).contains(0, 1, 2)
    expectThat(publisher.value).isEqualTo(2)

    job.cancel()
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

fun LocalBroadcastManager.asFlow(): Flow<Int> = callbackFlow {
  val receiver: BroadcastReceiver = { offer(it) }
  registerReceiver(receiver)
  awaitClose { unregisterReceiver(receiver) }
}
