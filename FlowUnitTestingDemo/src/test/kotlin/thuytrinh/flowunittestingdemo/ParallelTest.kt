package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class ParallelTest {
  @Test
  fun `tasks run serially`() = runBlocking {
    suspend fun taskA() {
      repeat(times = 5) {
        delay(1000)
        println("A: done #$it")
      }
    }

    suspend fun taskB() {
      repeat(times = 5) {
        delay(500)
        println("B: done #$it")
      }
    }

    suspend fun tasks() {
      taskA()
      taskB()
    }
    tasks()
  }

  @Test
  fun `tasks run in parallel`() = runBlocking {
    suspend fun taskA() {
      repeat(times = 5) {
        delay(1000)
        println("A: done #$it")
      }
    }

    suspend fun taskB() {
      repeat(times = 5) {
        delay(500)
        println("B: done #$it")
      }
    }

    suspend fun tasks() {
      val a = async { taskA() }
      val b = async { taskB() }
      a.await()
      b.await()
    }
    tasks()
  }

  @Test
  fun `tasks still run concurrently`() = runBlocking {
    suspend fun taskA() {
      repeat(times = 5) {
        delay(1000)
        println("A: done #$it")
      }
    }

    suspend fun taskB() {
      repeat(times = 5) {
        delay(500)
        println("B: done #$it")
      }
    }

    val singleThreadContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    suspend fun tasks() {
      val a = async(singleThreadContext) { taskA() }
      val b = async(singleThreadContext) { taskB() }
      a.await()
      b.await()
    }
    // Tasks aren't executed in parallel but still concurrently.
    tasks()
  }

  @Test
  fun `mosaic should not work`() = runBlocking<Unit>(context = Dispatchers.IO) {
    val size = 100
    val matrix = (0..size).map {
      (0..size).toList()
    }

    suspend fun solve(row: Int, column: Int): String {
      delay(500)
      println("done: [$row, $column]")
      return "[$row, $column]"
    }

    // Solving will take place serially.
    matrix.mapIndexed { row, columns ->
      columns.mapIndexed { column, _ ->
        solve(row, column)
      }
    }
  }

  @Test
  fun `mosaic should work`() = runBlocking<Unit>(context = Dispatchers.IO) {
    val size = 100
    val matrix = (0..size).map {
      (0..size).toList()
    }

    suspend fun solve(row: Int, column: Int): String {
      delay(500)
      println("done: [$row, $column]")
      return "[$row, $column]"
    }

    // Solving will take place concurrently.
    matrix.mapIndexed { row, columns ->
      columns.mapIndexed { column, _ ->
        async { solve(row, column) }
      }
    }.map { it.awaitAll() }
  }

  @Test
  fun `should be able to collect async`() = runBlocking<Unit> {
    // Given
    val states = mutableListOf<Int>()
    val flowA = MutableStateFlow(0)
    val flowB = MutableStateFlow(1)
    val flows = listOf(flowA, flowB)

    // When
    val job = launch {
      flows
        .map {
          async { it.toList(states) }
        }
        .awaitAll()
    }

    delay(1000)
    flowA.value = 2
    flowB.value = 3

    delay(1000)
    job.cancel()

    // Then
    assertThat(states).containsAll(listOf(0, 1, 2, 3))
  }

  @Test
  fun `should cancel async tasks`() = runBlocking {
    // Given
    val items = CopyOnWriteArrayList<Int>()

    // When
    val job = launch {
      listOf(
        async {
          delay(2000)
          println("Done A")
          items.add(0) // Should never reach this due to job cancellation.
        },
        async {
          delay(2000)
          println("Done B")
          items.add(1) // Should never reach this due to job cancellation.
        }
      ).awaitAll()
    }
    delay(1000)
    job.cancel()
    delay(4000)

    // Then
    assertThat(items).isEmpty()
  }

  @Test
  fun `items should contain 0 and 1 even though the job was canceled`() = runBlocking<Unit> {
    // Given
    val items = CopyOnWriteArrayList<Int>()
    lateinit var jobA: Job
    lateinit var jobB: Job

    // When
    val syncScope = this
    val syncJob = launch {
      // This job won't be canceled when syncJob is canceled.
      jobA = syncScope.launch {
        delay(2000)
        println("Done A")
        items.add(0)
      }

      // This job won't be canceled when syncJob is canceled.
      jobB = syncScope.launch {
        delay(2000)
        println("Done B")
        items.add(1)
      }
    }
    delay(1000) // Wait for the launch.
    syncJob.cancel()

    // Then
    assertThat(jobA.isActive).isTrue()
    assertThat(jobB.isActive).isTrue()
    delay(4000) // Wait till both jobA and jobB complete.
    assertThat(items).containsAll(listOf(0, 1))
  }

  @Test
  fun `items should be empty due to job cancellation`() = runBlocking<Unit> {
    // Given
    val items = CopyOnWriteArrayList<Int>()
    lateinit var jobA: Job
    lateinit var jobB: Job

    // When
    val syncScope = this
    val syncJob = syncScope.launch {
      // The `this` is different from `syncScope`.
      jobA = this.launch {
        delay(2000)
        println("Done A")
        items.add(0)
      }

      // The `this` is different from `syncScope`.
      jobB = this.launch {
        delay(2000)
        println("Done B")
        items.add(1)
      }
    }
    delay(1000) // Wait for the launch.
    syncJob.cancel()

    // Then
    assertThat(jobA.isActive).isFalse()
    assertThat(jobB.isActive).isFalse()
    delay(4000) // Wait till both jobA and jobB complete.
    assertThat(items).isEmpty()
  }
}
