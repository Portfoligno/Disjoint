package io.github.portfoligno.jackson.scalar

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate

@JvmPackagePrivate
internal
class ForwardingDeserializer<T, R>(
    private val delegate: BaseDeserializer<T>,
    private val transform: T.() -> R
) : BaseDeserializer<R>() {
  override
  fun invoke(p: JsonParser, context: DeserializationContext) =
      delegate(p, context).transform()
}
