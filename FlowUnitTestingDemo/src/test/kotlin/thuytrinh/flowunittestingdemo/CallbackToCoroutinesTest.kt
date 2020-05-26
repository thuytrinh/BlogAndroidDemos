package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume

class CallbackToCoroutinesTest {
  @Test
  fun `how invokeOnCancellation works?`() = runBlocking {
    fun doWithCallback(callback: (String) -> Unit) {
      callback("Hi!")
    }

    suspend fun doWithSuspend(): String = suspendCancellableCoroutine { continuation ->
      continuation.invokeOnCancellation {
        println("Done")
      }

      doWithCallback { continuation.resume(it) }
    }

    println(doWithSuspend())
  }
}
