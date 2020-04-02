package thuytrinh.flowunittestingdemo

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.containsExactly
import strikt.assertions.isEmpty
import java.util.regex.Pattern

class RegexTest {
  @Test
  fun `should extract JIRA ticket ids`() {
    fun findJiraTicketIds(input: String): List<String> {
      val pattern = Pattern.compile(
        "([A-Z0-9]{1,6}-[0-9]{1,6})"
      )
      return pattern.toRegex().findAll(input)
        .map { it.value }
        .toList()
    }

    val tests = listOf(
      Pair("BIT-1510 eMB docs", listOf("BIT-1510")),
      Pair("CAPP3-777 BIT-122 LOL", listOf("CAPP3-777", "BIT-122")),
      Pair("fix/CAPP3-777-resolve-types", listOf("CAPP3-777")),
      Pair("fix/CAPP3-777-BIT-777-resolve-types", listOf("CAPP3-777", "BIT-777")),
      Pair("feature/BIT-61-fix-npe", listOf("BIT-61"))
    )
    tests.forEach { (input, jiraTicketIds) ->
      expectThat(findJiraTicketIds(input)).containsExactly(jiraTicketIds)
    }
  }

  @Test
  fun `result should be empty`() {
    val pattern = Pattern.compile(
      "([A-Z0-9]{1,6}-[0-9]{1,6})"
    )
    expectThat(pattern.toRegex().findAll("Feature/bit 1510 e mb docs").toList()).isEmpty()
  }
}
