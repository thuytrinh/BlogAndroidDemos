package thuytrinh.flowunittestingdemo

import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
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
  fun `should work with TestCollector`() = runBlockingTest {
    val broadcastManager = LocalBroadcastManager()
    val collector = TestCollector<Intent>()
    val job = collector.test(this, broadcastManager.asFlow())

    broadcastManager.sendBroadcast(0)
    collector.assertValues(0)

    broadcastManager.sendBroadcast(1)
    collector.assertValues(0, 1)

    broadcastManager.sendBroadcast(2)
    collector.assertValues(0, 1, 2)

    job.cancel()
  }

  @Test
  fun `should work with Tester`() = runBlockingTest {
    val broadcastManager = LocalBroadcastManager()
    val (tester, job) = broadcastManager.asFlow().testWithin(this)

    broadcastManager.sendBroadcast(0)
    tester.assertValues(0)

    broadcastManager.sendBroadcast(1)
    tester.assertValues(0, 1)

    broadcastManager.sendBroadcast(2)
    tester.assertValues(0, 1, 2)

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

fun LocalBroadcastManager.asFlow(): Flow<Intent> = callbackFlow {
  val receiver: BroadcastReceiver = { offer(it) }
  registerReceiver(receiver)
  awaitClose { unregisterReceiver(receiver) }
}

/** Approach #1 */
class TestCollector<T> {
  private val values = mutableListOf<T>()

  fun test(scope: CoroutineScope, flow: Flow<T>): Job {
    return scope.launch { flow.collect { values.add(it) } }
  }

  fun assertValues(vararg _values: T) {
    expectThat(values).contains(_values.toList())
  }
}

/** Approach #2 */
class Tester<T>(private val values: MutableList<T>) {
  fun assertValues(vararg _values: T) {
    expectThat(values).contains(_values.toList())
  }
}

fun <T> Flow<T>.testWithin(scope: CoroutineScope): Pair<Tester<T>, Job> {
  val values = mutableListOf<T>()
  val tester = Tester(values)
  val flow = this
  val job = scope.launch {
    flow.toList(values)
  }
  return Pair(tester, job)
}
