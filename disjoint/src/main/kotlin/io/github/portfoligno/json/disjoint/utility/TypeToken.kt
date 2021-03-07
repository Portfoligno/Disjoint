@file:JvmPackagePrivate
@file:JvmName("TypeTokens")
package io.github.portfoligno.json.disjoint.utility

import com.fasterxml.jackson.core.type.TypeReference
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable

inline fun <reified T : Any> typeTokenOf() =
    object : TypeToken<T>() { }


abstract class TypeToken<T : Any> private constructor (explicitType: Type? = null) {
  constructor() : this(null)

  val type: Type = explicitType ?: (javaClass.genericSuperclass as ParameterizedType)
      .actualTypeArguments[0]
      .also { require(it !is TypeVariable<*>) }

  override fun hashCode() = type.hashCode()
  override fun equals(other: Any?) = other is TypeToken<*> && type == other.type
  override fun toString(): String = type.typeName


  private class SimpleTypeToken<T : Any>(type: Type) : TypeToken<T>(type)

  companion object {
    @JvmSynthetic
    @PublishedApi
    internal
    fun <T : Any> Type.toTypeToken(): TypeToken<T> =
        SimpleTypeToken(this)

    @JvmStatic
    @JvmName("of")
    fun <T : Any> Class<T>.toTypeToken(): TypeToken<T> =
        SimpleTypeToken(this)

    @JvmStatic
    @JvmName("of")
    fun <T : Any> TypeReference<T>.toTypeToken(): TypeToken<T> =
        SimpleTypeToken(type)
  }
}
