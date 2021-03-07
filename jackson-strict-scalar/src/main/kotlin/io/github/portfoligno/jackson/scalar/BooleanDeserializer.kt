package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken.VALUE_FALSE
import com.fasterxml.jackson.core.JsonToken.VALUE_TRUE
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
object BooleanDeserializer : BaseDeserializer<Boolean>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      when (p.currentToken()) {
        VALUE_TRUE -> true
        VALUE_FALSE -> false
        else -> throwWrongTokenException(context, VALUE_FALSE)
      }
}
