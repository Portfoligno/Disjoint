package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.*
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
class ArrayDeserializer<T, R>(
    private val elementDeserializer: BaseDeserializer<T>,
    private val transform: List<T>.() -> R
) : ExpectedTokenDeserializer<R>(START_ARRAY) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): R {
    val elements = arrayListOf<T>()

    loop@while (true) {
      elements.add(if (p.nextToken() == END_ARRAY) {
        break@loop
      } else {
        elementDeserializer(p, context)
      })
    }
    return elements.transform()
  }
}
