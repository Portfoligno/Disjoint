package io.github.portfoligno.json.decode.codec

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import io.github.portfoligno.json.decode.DecodingResult
import io.github.portfoligno.json.decode.DecodingResult.Failure
import io.github.portfoligno.json.decode.DecodingResult.Success
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

@JvmPackagePrivate
internal
class DecodingResultSerializer : JsonSerializer<DecodingResult<*>>() {
  override
  fun serialize(value: DecodingResult<*>, gen: JsonGenerator, serializers: SerializerProvider) =
      gen.writeObject(when (value) {
        is Failure -> value.rawValue
        is Success -> value.value
      })
}
