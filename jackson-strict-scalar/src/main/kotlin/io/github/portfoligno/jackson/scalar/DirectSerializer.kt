@file:JvmPackagePrivate
package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass
import com.fasterxml.jackson.core.JsonGenerator as G

private
class DirectSerializer<T>(private val write: JsonGenerator.(T) -> Unit) : JsonSerializer<T>() {
  override
  fun serialize(value: T, gen: JsonGenerator, serializers: SerializerProvider) =
      write(gen, value)
}

private
inline fun <reified T : Any> bind(noinline write: JsonGenerator.(T) -> Unit) =
    T::class to DirectSerializer(write)

private
fun JsonGenerator.writeCharacter(value: Char) =
    writeString(value.toString())


@Suppress("UNCHECKED_CAST")
internal
inline fun <reified T : Any> direct() =
    directSerializers.getValue(T::class) as JsonSerializer<T>

internal
val directSerializers: Map<KClass<out Any>, JsonSerializer<out Any>> = mapOf(
  bind<String>(G::writeString),

  bind(G::writeCharacter),
  bind<CharArray> {
    writeStartArray(it, it.size)
    it.forEach(::writeCharacter)
    writeEndArray()
  },

  bind(G::writeBoolean),
  bind<BooleanArray> {
    writeStartArray(it, it.size)
    it.forEach(::writeBoolean)
    writeEndArray()
  },
  bind<AtomicBoolean> { writeBoolean(it.get()) },

  bind<BigDecimal>(G::writeNumber),
  bind<BigInteger>(G::writeNumber),

  bind<Double>(G::writeNumber),
  bind<Float>(G::writeNumber),

  bind<Long>(G::writeNumber),
  bind<LongArray> {
    writeStartArray(it, it.size)
    it.forEach(::writeNumber)
    writeEndArray()
  },
  bind<AtomicLong> { writeNumber(it.get()) },

  bind<Int>(G::writeNumber),
  bind<IntArray> {
    writeStartArray(it, it.size)
    it.forEach(::writeNumber)
    writeEndArray()
  },
  bind<AtomicInteger> { writeNumber(it.get()) },

  bind<Short>(G::writeNumber),
  bind<ShortArray> {
    writeStartArray(it, it.size)
    it.forEach(::writeNumber)
    writeEndArray()
  },

  bind<Byte> { writeNumber(it.toInt()) }
)
