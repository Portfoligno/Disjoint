package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
class DoubleArraySerializer(
    private val elementSerializer: JsonSerializer<in Double>
) : JsonSerializer<DoubleArray>() {
  override
  fun serialize(value: DoubleArray, gen: JsonGenerator, serializers: SerializerProvider) {
    gen.writeStartArray(value, value.size)
    value.forEach {
      elementSerializer.serialize(it, gen, serializers)
    }
    gen.writeEndArray()
  }
}

@JvmPackagePrivate
internal
class FloatArraySerializer(
    private val elementSerializer: JsonSerializer<in Float>
) : JsonSerializer<FloatArray>() {
  override
  fun serialize(value: FloatArray, gen: JsonGenerator, serializers: SerializerProvider) {
    gen.writeStartArray(value, value.size)
    value.forEach {
      elementSerializer.serialize(it, gen, serializers)
    }
    gen.writeEndArray()
  }
}
