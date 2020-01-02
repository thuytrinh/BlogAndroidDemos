package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

class ConflatedBroadcastChannelTest {
  @Test
  fun test() {
    val latch = CountDownLatch(2)
    val valuePublisher = ConflatedBroadcastChannel<Int>()

    GlobalScope.launch {
      println("Consumer #0: Waiting...")
      println("Consumer #0: Received: ${valuePublisher.asFlow().first()}")
      latch.countDown()
    }

    GlobalScope.launch {
      println("Consumer #1: Waiting...")
      println("Consumer #1: Received: ${valuePublisher.asFlow().first()}")
      latch.countDown()
    }

    GlobalScope.launch {
      println("Delaying...")
      delay(2000)
      println("Sending...")
      valuePublisher.send(7)
    }

    latch.await()
  }
}
