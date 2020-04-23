package thuytrinh.flowunittestingdemo

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RxJavaCoroutinesTest {
  @Test
  fun `will we get UndeliverableException?`() = runBlocking {
    val stream = Observable.create<Int> {
      Thread.sleep(2000)
      throw RuntimeException("ಠ_ಠ")
    }
    val flow = stream
      .subscribeOn(Schedulers.io())
      .asFlow()
    val job = GlobalScope.launch {
      flow.collect()
    }

    delay(1000)
    job.cancel()
    job.join()
  }

  @Test
  fun `should get UndeliverableException`() = runBlocking {
    val stream = Observable.create<Int> {
      Thread.sleep(1000)
      throw RuntimeException("ಠ_ಠ")
    }
    val subscription = stream
      .subscribeOn(Schedulers.from(Dispatchers.IO.asExecutor()))
      .subscribe()

    delay(1000)
    subscription.dispose()
  }

  @Test
  fun `should convert Single to suspend function`() = runBlocking<Unit> {
    val value = Single.just(2).await()
    assertThat(value).isEqualTo(2)
  }
}
