package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonToken.VALUE_EMBEDDED_OBJECT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate

@JvmPackagePrivate
internal
abstract class BaseDeserializer<A> : Deserializer<A>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
      when (p.currentToken) {
        VALUE_EMBEDDED_OBJECT -> invoke(p.currentValueTokens(ctxt), ctxt)
        else -> invoke(p, ctxt)
      }

  abstract operator fun invoke(p: JsonParser, context: DeserializationContext): A
}

@JvmPackagePrivate
internal
abstract class ExpectedTokenDeserializer<A>(private val expectedToken: JsonToken) : BaseDeserializer<A>() {
  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
      when (p.currentToken) {
        VALUE_EMBEDDED_OBJECT ->
          p.currentValueTokens(ctxt).let {
            if (it.nextToken() !== expectedToken) {
              throwWrongTokenException(ctxt, expectedToken)
            }
            invoke(it, ctxt)
          }
        expectedToken -> invoke(p, ctxt)
        else -> throwWrongTokenException(ctxt, expectedToken)
      }
}
