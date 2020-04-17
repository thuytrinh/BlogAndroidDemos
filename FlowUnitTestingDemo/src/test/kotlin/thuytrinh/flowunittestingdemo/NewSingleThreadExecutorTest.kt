/** Copyright © 2019 Robert Bosch GmbH. All rights reserved. */
package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
}
