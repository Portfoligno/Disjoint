package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.disjoint.Union
import io.github.portfoligno.json.disjoint.Union.Companion.value
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
class UnionSerializer : JsonSerializer<Union<*, *>>() {
  override
  fun serialize(value: Union<*, *>, gen: JsonGenerator, serializers: SerializerProvider) =
      gen.writeObject(value.value)
}
