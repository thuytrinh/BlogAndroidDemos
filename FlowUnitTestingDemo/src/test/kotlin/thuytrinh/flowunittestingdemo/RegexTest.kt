package thuytrinh.flowunittestingdemo

import org.assertj.core.api.Assertions.assertThat
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

  /**
   * - https://www.vogella.com/tutorials/JavaRegularExpressions/article.html
   * - https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
   */
  @Test
  fun `should follow CamelCase pattern`() {
    val regex = Regex("(([A-Z]([a-z0-9]+))+)|(buildSrc)")

    // Valid cases.
    listOf(
      "Foo", "Bar", "FooBar", "Foobar", "Fo", "FoBa",
      "buildSrc"
    ).forEach {
      assertThat(it).matches(regex.toPattern())
    }

    // Invalid cases.
    listOf(
      "fooBar", "foobar", "FB", "F",
      "foo_bar", "Foo_Bar", "foo_Bar",
      "foo-bar", "Foo-Bar"
    ).forEach {
      assertThat(it).doesNotMatch(regex.toPattern())
    }
  }
}
