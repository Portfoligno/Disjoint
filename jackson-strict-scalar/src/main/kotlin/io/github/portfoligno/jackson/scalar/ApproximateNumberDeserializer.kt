package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate
import java.math.BigDecimal

@JvmPackagePrivate
internal
object HalfEvenDoubleDeserializer : BaseDeserializer<Double>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.doubleValue
}

@JvmPackagePrivate
internal
object HalfEvenFloatDeserializer : BaseDeserializer<Float>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.floatValue
}


@JvmPackagePrivate
internal
object ShortenExclusiveDoubleDeserializer : BaseDeserializer<Double>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.doubleValue.let { parsed ->
        if (BigDecimal(parsed.toString()).compareTo(p.decimalValue) == 0) {
          parsed
        } else {
          val message = "DOUBLE expected, but BIG_DECIMAL (${p.decimalValue}) was found"
          throwInputMismatch(context, message)
        }
      }
}

@JvmPackagePrivate
internal
object ShortenExclusiveFloatDeserializer : BaseDeserializer<Float>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      p.floatValue.let { parsed ->
        if (BigDecimal(parsed.toString()).compareTo(p.decimalValue) == 0) {
          parsed
        } else {
          val message = "FLOAT expected, but DOUBLE or BIG_DECIMAL (${p.decimalValue}) was found"
          throwInputMismatch(context, message)
        }
      }
}
