package thuytrinh.flowunittestingdemo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
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
}
