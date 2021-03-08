@file:Suppress("MemberVisibilityCanBePrivate")
package io.github.portfoligno.json.decode

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.portfoligno.json.ast.Json
import io.github.portfoligno.json.decode.codec.DecodingResultDeserializer
import io.github.portfoligno.json.decode.codec.DecodingResultSerializer
import java.lang.reflect.Type

@JsonDeserialize(using = DecodingResultDeserializer::class)
@JsonSerialize(using = DecodingResultSerializer::class)
sealed class DecodingResult<out A : Any> {
  abstract fun <R> fold(transformFailure: (Json, Type, Throwable) -> R, transformSuccess: (A) -> R): R

  class Failure(val rawValue: Json, val targetType: Type, val throwable: Throwable) : DecodingResult<Nothing>() {
    override fun <R> fold(transformFailure: (Json, Type, Throwable) -> R, transformSuccess: (Nothing) -> R): R =
        transformFailure(rawValue, targetType, throwable)

    override fun hashCode() = targetType.hashCode() + 0x3e8639d4 * rawValue.hashCode()
    override fun equals(other: Any?) = other is Failure && targetType == other.targetType && rawValue == other.rawValue
    override fun toString() = "Failure($rawValue, $targetType, $throwable)"
  }

  class Success<out A : Any>(val value: A) : DecodingResult<A>() {
    override fun <R> fold(transformFailure: (Json, Type, Throwable) -> R, transformSuccess: (A) -> R): R =
        transformSuccess(value)

    override fun hashCode() = 0x4357decb + value.hashCode()
    override fun equals(other: Any?) = other is Success<*> && value == other.value
    override fun toString() = "Success($value)"
  }
}
