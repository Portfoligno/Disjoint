@file:JvmPackagePrivate
package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.util.TokenBuffer
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

internal
typealias Deserializer<A> = com.fasterxml.jackson.databind.JsonDeserializer<A>

internal
fun Deserializer<*>.throwWrongTokenException(context: DeserializationContext, expectedToken: JsonToken): Nothing {
  context.reportWrongTokenException(this, expectedToken, null)
  throw AssertionError("Unexpected invocation")
}

internal
fun Deserializer<*>.throwInputMismatch(context: DeserializationContext, message: String): Nothing =
    context.reportInputMismatch(this, message)


internal
fun JsonParser.currentValueTokens(context: DeserializationContext) =
    TokenBuffer(this).deserialize(this, context).asParser()
