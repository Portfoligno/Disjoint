package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate
import java.math.BigDecimal

@JvmPackagePrivate
internal
object DoubleSerializer : JsonSerializer<Double>() {
  override
  fun serialize(value: Double, gen: JsonGenerator, serializers: SerializerProvider) =
      gen.writeNumber(BigDecimal(value))
}

@JvmPackagePrivate
internal
object FloatSerializer : JsonSerializer<Float>() {
  override
  fun serialize(value: Float, gen: JsonGenerator, serializers: SerializerProvider) =
      gen.writeNumber(BigDecimal(value.toDouble()))
}
