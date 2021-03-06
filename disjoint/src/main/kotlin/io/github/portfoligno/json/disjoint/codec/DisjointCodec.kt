package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.portfoligno.json.disjoint.Disjoint
import io.github.portfoligno.json.disjoint.Disjoint.Left
import io.github.portfoligno.json.disjoint.Disjoint.Right
import io.github.portfoligno.json.disjoint.DisjointSource
import io.github.portfoligno.json.disjoint.DisjointSource.Unresolved
import io.github.portfoligno.json.disjoint.utility.TypeToken

interface DisjointCodec {
  fun <T : Any> convertOrNull(value: Any?, type: TypeToken<T>): T?

  fun <A : Any, B> resolve(source: Unresolved<B>, leftType: TypeToken<A>) =
        convertOrNull(source.value, leftType)?.let(Left.Companion::invoke) ?: (Right)(source.value)

  fun <A : Any, B> resolveSource(source: DisjointSource<A, B>, leftType: TypeToken<A>) =
      when (source) {
        is Disjoint -> source
        is Unresolved -> resolve(source, leftType)
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
