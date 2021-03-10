package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.portfoligno.json.disjoint.Disjoint
import io.github.portfoligno.json.disjoint.Disjoint.Left
import io.github.portfoligno.json.disjoint.Disjoint.Right
import io.github.portfoligno.json.disjoint.Union
import io.github.portfoligno.json.disjoint.UnresolvedRight
import io.github.portfoligno.json.disjoint.utility.TypeToken

interface DisjointCodec {
  fun <T : Any> convertOrNull(value: Any?, type: TypeToken<T>): T?

  fun <A : Any, B : Any> resolve(source: UnresolvedRight<B>, leftType: TypeToken<A>) =
        convertOrNull(source.value, leftType)?.let { Left(it) } ?: Right(source.value)

  fun <A : Any, B : Any> resolveSource(source: Union<A, B>, leftType: TypeToken<A>) =
      when (source) {
        is Disjoint -> source
        is UnresolvedRight -> resolve(source, leftType)
      }

  companion object {
    @JvmStatic
    @JvmName("from")
    fun ObjectMapper.toDisjointCodec() = object : DisjointCodec {
      override fun <T : Any> convertOrNull(value: Any?, type: TypeToken<T>): T? =
          try {
            convertValue(value, typeFactory.constructType(type.type))
          } catch (_: IllegalArgumentException) {
            null
          }
    }
  }
}
