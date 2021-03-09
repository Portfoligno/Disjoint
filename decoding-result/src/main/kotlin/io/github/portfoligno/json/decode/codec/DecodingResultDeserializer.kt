@file:JvmPackagePrivate
package io.github.portfoligno.json.decode.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.util.TokenBuffer
import io.github.portfoligno.json.ast.JsonNonNull
import io.github.portfoligno.json.ast.JsonNull
import io.github.portfoligno.json.decode.DecodingResult
import io.github.portfoligno.json.decode.DecodingResult.Failure
import io.github.portfoligno.json.decode.DecodingResult.Success
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

private
fun JsonDeserializer<*>.throwInputMismatch(context: DeserializationContext, message: String): Nothing =
    context.reportInputMismatch(this, message)

private
fun Throwable.throwIfCritical(): Unit =
    when (this) {
      is ThreadDeath, is VirtualMachineError -> throw this
      else -> Unit
    }


@JvmPackagePrivate
internal
class DecodingResultDeserializer : JsonDeserializer<DecodingResult<Any>>(), ContextualDeserializer {
  override
  fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
      when (val t = ctxt.contextualType) {
        null -> this
        else -> object : JsonDeserializer<DecodingResult<Any>>() {
          override
          fun deserialize(p: JsonParser, ctxt: DeserializationContext): DecodingResult<Any> =
              typedDeserialize(p, t, ctxt)

          override
          fun getNullValue(ctxt: DeserializationContext): DecodingResult<Any> =
              typedDeserialize(ctxt.parser, t, ctxt)
        }
      }

  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): DecodingResult<Any> =
      createDecodingResult(p, ctxt, ctxt.typeFactory.constructType(Any::class.java))

  fun typedDeserialize(
      p: JsonParser, contextualType: JavaType, context: DeserializationContext
  ): DecodingResult<Any> =
      contextualType.run {
        when (rawClass.kotlin) {
          Failure::class ->
            try {
              throwInputMismatch(context, "Failure requested")
            } catch (t: Throwable) {
              t.throwIfCritical()
              Failure(p.codec.readValue(p, JsonNonNull::class.java) ?: JsonNull, Nothing::class.java, t)
            }
          Success::class ->
            createSuccess(p, context, bindings.typeParameters[0])
          else ->
            createDecodingResult(p, context, bindings.typeParameters[0])
        }
      }

  private
  fun <A : Any> createDecodingResult(
      p: JsonParser, context: DeserializationContext, type: JavaType
  ): DecodingResult<A> =
      TokenBuffer(p).deserialize(p, context).let { tokens ->
        try {
          createSuccess(tokens.asParser(), context, type)
        } catch (t: Throwable) {
          t.throwIfCritical()
          Failure(p.codec.readValue(p, JsonNonNull::class.java) ?: JsonNull, type, t)
        }
      }

  private
  fun <A : Any> createSuccess(
      p: JsonParser, context: DeserializationContext, type: JavaType
  ): Success<A> =
      p.codec.readValue<A?>(p, type)
          ?.let(::Success)
          ?: throwInputMismatch(context, "$type expected, but null was found")
}
