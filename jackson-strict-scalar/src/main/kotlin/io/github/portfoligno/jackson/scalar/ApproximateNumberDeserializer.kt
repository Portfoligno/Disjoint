package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate
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
      p.doubleValue.also { parsed ->
        if (BigDecimal(parsed.toString()).compareTo(p.decimalValue) != 0) {
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
      p.floatValue.also { parsed ->
        if (BigDecimal(parsed.toString()).compareTo(p.decimalValue) != 0) {
          val message = "FLOAT expected, but DOUBLE or BIG_DECIMAL (${p.decimalValue}) was found"
          throwInputMismatch(context, message)
        }
      }
}
