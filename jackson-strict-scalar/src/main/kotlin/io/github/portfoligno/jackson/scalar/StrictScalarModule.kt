@file:JvmPackagePrivate
@file:Suppress("UNCHECKED_CAST", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")
package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.portfoligno.jackson.scalar.DiscreteFloatingPointRoundingMode.*
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

private
fun SimpleModule.addDeserializers(
    doubles: BaseDeserializer<out Double>, floats: BaseDeserializer<out Float>
) {
  addDeserializer(java.lang.Double.TYPE, doubles)
  addDeserializer(java.lang.Double::class.java, doubles as JsonDeserializer<java.lang.Double>)
  addDeserializer(DoubleArray::class.java, ArrayDeserializer(doubles) { toDoubleArray() })

  addDeserializer(java.lang.Float.TYPE, floats)
  addDeserializer(java.lang.Float::class.java, floats as JsonDeserializer<java.lang.Float>)
  addDeserializer(FloatArray::class.java, ArrayDeserializer(floats) { toFloatArray() })
}

private
fun SimpleModule.addSerializers(
    doubles: JsonSerializer<in Double>, floats: JsonSerializer<in Float>
) {
  addSerializer(java.lang.Double.TYPE, doubles)
  addSerializer(java.lang.Double::class.java, doubles as JsonSerializer<java.lang.Double>)
  addSerializer(DoubleArray::class.java, DoubleArraySerializer(doubles))

  addSerializer(java.lang.Float.TYPE, floats)
  addSerializer(java.lang.Float::class.java, floats as JsonSerializer<java.lang.Float>)
  addSerializer(FloatArray::class.java, FloatArraySerializer(floats))
}

class StrictScalarModule(
    discreteFloatingPointRoundingMode: DiscreteFloatingPointRoundingMode = HALF_EVEN_SHORTEN_HYBRID
) : SimpleModule() {
  init {
    addDeserializer(String::class.java, StringDeserializer)
    addSerializer(String::class.java, direct())

    addDeserializer(Character.TYPE, CharDeserializer)
    addDeserializer(Character::class.java, CharDeserializer as JsonDeserializer<Character>)
    addDeserializer(CharArray::class.java, ArrayDeserializer(CharDeserializer) { toCharArray() })
    addSerializer(Character.TYPE, direct())
    addSerializer(Character::class.java, direct())
    addSerializer(CharArray::class.java, direct())

    addDeserializer(java.lang.Boolean.TYPE, BooleanDeserializer)
    addDeserializer(java.lang.Boolean::class.java, BooleanDeserializer as JsonDeserializer<java.lang.Boolean>)
    addDeserializer(BooleanArray::class.java, ArrayDeserializer(BooleanDeserializer) { toBooleanArray() })
    addDeserializer(AtomicBoolean::class.java, ForwardingDeserializer(BooleanDeserializer, ::AtomicBoolean))
    addSerializer(java.lang.Boolean.TYPE, direct())
    addSerializer(java.lang.Boolean::class.java, direct())
    addSerializer(BooleanArray::class.java, direct())
    addSerializer(AtomicBoolean::class.java, direct())

    addDeserializer(Number::class.java, NumberDeserializer)
    addDeserializer(BigDecimal::class.java, BigDecimalDeserializer)
    addDeserializer(BigInteger::class.java, BigIntegerDeserializer)
    addSerializer(BigDecimal::class.java, direct())
    addSerializer(BigInteger::class.java, direct())

    when (discreteFloatingPointRoundingMode) {
      UNNECESSARY -> addDeserializers(DoubleDeserializer, FloatDeserializer)
      SHORTEN_EXCLUSIVE -> addDeserializers(ShortenExclusiveDoubleDeserializer, ShortenExclusiveFloatDeserializer)
      HALF_EVEN,
      HALF_EVEN_SHORTEN_HYBRID -> addDeserializers(HalfEvenDoubleDeserializer, HalfEvenFloatDeserializer)
    }
    when (discreteFloatingPointRoundingMode) {
      UNNECESSARY,
      HALF_EVEN -> addSerializers(DoubleSerializer, FloatSerializer)
      SHORTEN_EXCLUSIVE,
      HALF_EVEN_SHORTEN_HYBRID -> addSerializers(direct(), direct())
    }

    addDeserializer(java.lang.Long.TYPE, LongDeserializer)
    addDeserializer(java.lang.Long::class.java, LongDeserializer as JsonDeserializer<java.lang.Long>)
    addDeserializer(LongArray::class.java, ArrayDeserializer(LongDeserializer) { toLongArray() })
    addDeserializer(AtomicLong::class.java, ForwardingDeserializer(LongDeserializer, ::AtomicLong))
    addSerializer(java.lang.Long.TYPE, direct())
    addSerializer(java.lang.Long::class.java, direct())
    addSerializer(LongArray::class.java, direct())
    addSerializer(AtomicLong::class.java, direct())

    addDeserializer(Integer.TYPE, IntDeserializer)
    addDeserializer(Integer::class.java, IntDeserializer as JsonDeserializer<Integer>)
    addDeserializer(IntArray::class.java, ArrayDeserializer(IntDeserializer) { toIntArray() })
    addDeserializer(AtomicInteger::class.java, ForwardingDeserializer(IntDeserializer, ::AtomicInteger))
    addSerializer(Integer.TYPE, direct())
    addSerializer(Integer::class.java, direct())
    addSerializer(IntArray::class.java, direct())
    addSerializer(AtomicInteger::class.java, direct())

    addDeserializer(java.lang.Short.TYPE, ShortDeserializer)
    addDeserializer(java.lang.Short::class.java, ShortDeserializer as JsonDeserializer<java.lang.Short>)
    addDeserializer(ShortArray::class.java, ArrayDeserializer(ShortDeserializer) { toShortArray() })
    addSerializer(java.lang.Short.TYPE, direct())
    addSerializer(java.lang.Short::class.java, direct())
    addSerializer(ShortArray::class.java, direct())

    addDeserializer(java.lang.Byte.TYPE, ByteDeserializer)
    addDeserializer(java.lang.Byte::class.java, ByteDeserializer as JsonDeserializer<java.lang.Byte>)
    addSerializer(java.lang.Byte.TYPE, direct())
    addSerializer(java.lang.Byte::class.java, direct())
  }
}
