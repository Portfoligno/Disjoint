package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonParser.NumberType.*
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_FLOAT
import com.fasterxml.jackson.core.JsonToken.VALUE_NUMBER_INT
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate
import java.math.BigDecimal
import java.math.BigInteger

@JvmPackagePrivate
internal
object NumberDeserializer : BaseDeserializer<Number>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): Number =
      when (p.numberType) {
          null -> throwWrongTokenException(context, VALUE_NUMBER_FLOAT)
          INT -> p.intValue
          LONG -> p.longValue
          BIG_INTEGER -> p.bigIntegerValue
          else -> p.decimalValue
      }
}


@JvmPackagePrivate
internal
object BigDecimalDeserializer : BaseDeserializer<BigDecimal>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): BigDecimal =
      p.decimalValue
}

@JvmPackagePrivate
internal
object DoubleDeserializer : BaseDeserializer<Double>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.doubleValue.also { parsed ->
        if (BigDecimal(parsed).compareTo(p.decimalValue) != 0) {
          val message = "DOUBLE expected, but BIG_DECIMAL (${p.decimalValue}) was found"
          throwInputMismatch(context, message)
        }
      }
}

@JvmPackagePrivate
internal
object FloatDeserializer : BaseDeserializer<Float>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.floatValue.also { parsed ->
        if (BigDecimal(parsed.toDouble()).compareTo(p.decimalValue) != 0) {
          val message = "FLOAT expected, but DOUBLE or BIG_DECIMAL (${p.decimalValue}) was found"
          throwInputMismatch(context, message)
        }
      }
}


@JvmPackagePrivate
internal
object BigIntegerDeserializer : BaseDeserializer<BigInteger>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext): BigInteger =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> p.bigIntegerValue
        VALUE_NUMBER_FLOAT -> try {
          p.decimalValue.toBigIntegerExact()
        } catch (_: ArithmeticException) {
          throwWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throwWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

@JvmPackagePrivate
internal
object LongDeserializer : BaseDeserializer<Long>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> p.longValue
        VALUE_NUMBER_FLOAT -> try {
          p.decimalValue.longValueExact()
        } catch (_: ArithmeticException) {
          throwWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throwWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

@JvmPackagePrivate
internal
object IntDeserializer : BaseDeserializer<Int>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> p.intValue
        VALUE_NUMBER_FLOAT -> try {
          p.decimalValue.intValueExact()
        } catch (_: ArithmeticException) {
          throwWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throwWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

@JvmPackagePrivate
internal
object ShortDeserializer : BaseDeserializer<Short>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> p.intValue.toShort().also { parsed ->
          if (parsed.toInt() != p.intValue) {
              val message = "SHORT expected, but INT (${p.intValue}) was found"
              throwInputMismatch(context, message)
          }
        }
        VALUE_NUMBER_FLOAT -> try {
          p.decimalValue.shortValueExact()
        } catch (_: ArithmeticException) {
          throwWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throwWrongTokenException(context, VALUE_NUMBER_INT)
      }
}

@JvmPackagePrivate
internal
object ByteDeserializer : BaseDeserializer<Byte>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      when (p.currentToken()) {
        VALUE_NUMBER_INT -> p.intValue.toByte().also { parsed ->
          if (parsed.toInt() != p.intValue) {
              val message = "BYTE expected, but SHORT or INT (${p.intValue}) was found"
              throwInputMismatch(context, message)
          }
        }
        VALUE_NUMBER_FLOAT -> try {
          p.decimalValue.byteValueExact()
        } catch (_: ArithmeticException) {
          throwWrongTokenException(context, VALUE_NUMBER_INT)
        }
        else -> throwWrongTokenException(context, VALUE_NUMBER_INT)
      }
}
