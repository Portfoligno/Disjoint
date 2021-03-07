package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.VALUE_STRING
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
object StringDeserializer : ExpectedTokenDeserializer<String>(VALUE_STRING) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): String =
      p.valueAsString
}

@JvmPackagePrivate
internal
object CharDeserializer : ExpectedTokenDeserializer<Char>(VALUE_STRING) {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.valueAsString.let { stringValue ->
        if (stringValue.length == 1) {
          stringValue[0]
        } else {
          val message = "CHAR expected, but STRING ($stringValue) was found"
          throwInputMismatch(context, message)
        }
      }
}
