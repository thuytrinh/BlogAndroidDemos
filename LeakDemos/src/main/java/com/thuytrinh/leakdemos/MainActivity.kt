package com.thuytrinh.leakdemos

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.thuytrinh.leakdemos.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class MainActivity : AppCompatActivity() {
  @ExperimentalTime
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.doWorkButton.setOnClickListener {
      lifecycleScope.launchWhenCreated {
        // doWork0()
        // doWork1()
        // doWork2()
        doWork3 {
          Toast.makeText(this@MainActivity, "Received: $it", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }
}

@ExperimentalTime
suspend fun doWork0() {
  val delayTimes = listOf(
    2.seconds,
    2.seconds,
    2.seconds
  )
  withContext(Dispatchers.IO) {
    delayTimes.forEachIndexed { index, duration ->
      Log.i("doWork", "delaying #$index")
      delay(duration.toLongMilliseconds())
      Log.i("doWork", "done #$index")
    }
  }
}

@ExperimentalTime
suspend fun doWork1() {
  return GlobalScope.launch {
    val delayTimes = listOf(
      2.seconds,
      2.seconds,
      2.seconds
    )
    withContext(Dispatchers.IO) {
      delayTimes.forEachIndexed { index, duration ->
        Log.i("doWork", "delaying #$index")
        delay(duration.toLongMilliseconds())
        Log.i("doWork", "done #$index")
      }
    }
  }.join()
}

@ExperimentalTime
suspend fun doWork2() {
  return GlobalScope.async {
    val delayTimes = listOf(
      2.seconds,
      2.seconds,
      2.seconds
    )
    withContext(Dispatchers.IO) {
      delayTimes.forEachIndexed { index, duration ->
        Log.i("doWork", "delaying #$index")
        delay(duration.toLongMilliseconds())
        Log.i("doWork", "done #$index")
      }
    }
  }.await()
}

@ExperimentalTime
suspend fun doWork3(callback: (String) -> Unit) {
  return GlobalScope.launch {
    val delayTimes = listOf(
      2.seconds,
      2.seconds,
      2.seconds
    )
    withContext(Dispatchers.IO) {
      delayTimes.forEachIndexed { index, duration ->
        Log.i("doWork", "delaying #$index")
        delay(duration.toLongMilliseconds())
        Log.i("doWork", "done #$index")
      }
    }
    withContext(Dispatchers.Main) {
      callback("✓")
    }
  }.join()
}
