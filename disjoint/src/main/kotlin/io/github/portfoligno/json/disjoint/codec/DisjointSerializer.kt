package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.disjoint.Disjoint.Companion.value
import io.github.portfoligno.json.disjoint.DisjointSource
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
class DisjointSerializer : JsonSerializer<DisjointSource<*, *>>() {
  override
  fun serialize(value: DisjointSource<*, *>, gen: JsonGenerator, serializers: SerializerProvider) =
      gen.writeObject(value.value)
}
