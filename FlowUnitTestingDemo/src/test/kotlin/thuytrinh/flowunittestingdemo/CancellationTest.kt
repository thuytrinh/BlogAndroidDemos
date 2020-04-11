package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Test
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad
 */
class CancellationTest {
  @Test
  fun `cancel with withContext()`() = runBlocking {
    val externalScope = GlobalScope

    suspend fun veryImportantOperation() {
      println("veryImportantOperation: delaying")
      delay(2000)
      println("veryImportantOperation: done")
    }

    suspend fun doWork() {
      println("doWork: entering")
      withContext(externalScope.coroutineContext) {
        veryImportantOperation()
      }
      println("doWork: done")
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val job = scope.launch {
      doWork()
    }

    // Wait till veryImportantOperation reaches somewhere in the middle of it.
    delay(1000)
    job.cancel()

    // Delay to see if veryImportantOperation continues to execute.
    delay(5000)
  }

  @Test
  fun `veryImportantOperation should continue`() = runBlocking {
    val externalScope = GlobalScope

    suspend fun veryImportantOperation() = externalScope.launch {
      println("veryImportantOperation: delaying")
      delay(2000)
      println("veryImportantOperation: done")
    }.join()

    suspend fun doWork() {
      println("doWork: entering")
      withContext(externalScope.coroutineContext) {
        veryImportantOperation()
      }
      println("doWork: done")
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val job = scope.launch {
      doWork()
    }

    // Wait till veryImportantOperation reaches somewhere in the middle of it.
    delay(1000)
    job.cancel()

    // Delay to see if veryImportantOperation continues to execute.
    delay(5000)
  }

  @ExperimentalTime
  @Test
  fun `veryImportantOperation with callback should continue`() = runBlocking {
    val externalScope = GlobalScope

    suspend fun veryImportantOperation(callback: (String) -> Unit) = externalScope.launch {
      val secs = 3.seconds
      println("veryImportantOperation: delaying $secs")
      delay(secs.toLongMilliseconds())
      callback("✓")
      println("veryImportantOperation: done")
    }.join()

    suspend fun doWork(callback: (String) -> Unit) {
      println("doWork: entering")
      withContext(externalScope.coroutineContext) {
        veryImportantOperation(callback)
      }
      println("doWork: done")
    }

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val job = scope.launch {
      doWork(::println)
    }

    // Wait till veryImportantOperation reaches somewhere in the middle of it.
    delay(1000)
    job.cancel()

    // Delay to see if veryImportantOperation continues to execute.
    delay(5000)
  }
}
