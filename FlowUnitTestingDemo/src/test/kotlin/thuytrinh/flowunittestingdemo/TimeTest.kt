package thuytrinh.flowunittestingdemo

import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class TimeTest {
  @Test
  fun `should give the local date in next 7 weeks`() {
    val next7Weeks = Instant.now()
      .atZone(ZoneId.systemDefault())
      .plusWeeks(7)
    val info = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(next7Weeks)
    println(info)
  }
}
