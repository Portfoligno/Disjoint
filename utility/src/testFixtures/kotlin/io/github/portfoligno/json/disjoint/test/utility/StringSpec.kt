package io.github.portfoligno.json.disjoint.test.utility

import io.kotest.core.spec.style.StringSpec
import org.junit.jupiter.api.function.Executable

abstract class StringSpec(body: StringSpec.() -> Unit = {}) : StringSpec(body) {
  fun describe(name: String, test: Executable) =
      name {
        test.execute()
      }
}
