package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Test
import java.util.concurrent.Executors

class NewSingleThreadExecutorTest {
  @Test
  fun `will it print all lines of taskA first?`() = runBlocking {
    val scope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher())
    suspend fun taskA() {
      repeat(times = 10) {
        delay(500)
        println("A: $it")
      }
    }

    suspend fun taskB() {
      repeat(times = 10) {
        delay(500)
        println("B: $it")
      }
    }

    val i = scope.launch { taskA() }
    val j = scope.launch { taskB() }
    listOf(i, j).joinAll()
  }

  class ServiceA(private val mutex: Mutex) {
    suspend fun taskA() = mutex.withLock {
      repeat(times = 10) {
        delay(500)
        println("A: $it")
      }
    }
  }

  class ServiceB(private val mutex: Mutex) {
    suspend fun taskB() = mutex.withLock {
      repeat(times = 10) {
        delay(500)
        println("B: $it")
      }
    }
  }

  @Test
  fun `should execute serially with Mutex`() = runBlocking<Unit> {
    val sharedMutex = Mutex()
    val serviceA = ServiceA(sharedMutex)
    val serviceB = ServiceB(sharedMutex)

    listOf(
      async(Dispatchers.IO) { serviceA.taskA() },
      async(Dispatchers.IO) { serviceB.taskB() }
    ).awaitAll()
  }
}
