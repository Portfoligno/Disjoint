@file:Suppress("MemberVisibilityCanBePrivate")
package io.github.portfoligno.json.disjoint

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.portfoligno.json.disjoint.codec.DisjointCodec
import io.github.portfoligno.json.disjoint.codec.DisjointDeserializer
import io.github.portfoligno.json.disjoint.codec.DisjointSerializer
import io.github.portfoligno.json.disjoint.utility.typeTokenOf

@JsonDeserialize(using = DisjointDeserializer::class)
@JsonSerialize(using = DisjointSerializer::class)
sealed class DisjointSource<out A : Any, out B : Any> {
  class Unresolved<B : Any> private constructor (val value: B) : DisjointSource<Nothing, B>() {
    inline fun <reified A : Any> resolveWith(codec: DisjointCodec) =
        codec.resolve(this, typeTokenOf<A>())

    override fun hashCode() = 0x19085cbf + value.hashCode()
    override fun equals(other: Any?) = other is Unresolved<*> && value == other.value
    override fun toString() = "Disjoint.unresolved($value)"

    companion object {
      @JvmStatic
      @JvmName("of")
      operator fun <B : Any> invoke(value: B) = Unresolved(value)
    }
  }
}

sealed class Disjoint<A : Any, out B : Any> : DisjointSource<A, B>() {
  abstract val left: A?
  abstract val right: B?

  abstract fun <R> fold(leftTransform: (A) -> R, rightTransform: (B) -> R): R
  abstract fun <R : Any> mapRight(transform: (B) -> R): DisjointSource<A, R>
  abstract fun <R : Any> mapLeft(transform: (A) -> R): DisjointSource<R, B>
  abstract fun <R : Any, S : Any> bimap(leftTransform: (A) -> R, rightTransform: (B) -> S): DisjointSource<R, S>

  class Right<A : Any, B : Any> private constructor (val value: B) : Disjoint<A, B>() {
    override val left: Nothing? get() = null
    override val right get() = value

    override fun <R> fold(leftTransform: (A) -> R, rightTransform: (B) -> R) =
        rightTransform(value)
    override fun <R : Any> mapRight(transform: (B) -> R) =
        Unresolved(transform(value))
    override fun <R : Any> mapLeft(transform: (A) -> R) =
        Unresolved(value)
    override fun <R : Any, S : Any> bimap(leftTransform: (A) -> R, rightTransform: (B) -> S) =
        Unresolved(rightTransform(value))

    override fun hashCode() = 0x6c49acc3 + value.hashCode()
    override fun equals(other: Any?) = other is Right<*, *> && value == other.value
    override fun toString() = "Disjoint.right($value)"

    companion object {
      @JvmSynthetic
      internal
      operator fun <A : Any, B : Any> invoke(value: B) = Right<A, B>(value)
    }
  }

  class Left<A : Any> private constructor (val value: A) : Disjoint<A, Nothing>() {
    override val left get() = value
    override val right get() = null

    override fun <R> fold(leftTransform: (A) -> R, rightTransform: (Nothing) -> R) =
        leftTransform(value)
    override fun <R : Any> mapRight(transform: (Nothing) -> R) =
        this
    override fun <R : Any> mapLeft(transform: (A) -> R) =
        Left(transform(value))
    override fun <R : Any, S : Any> bimap(leftTransform: (A) -> R, rightTransform: (Nothing) -> S) =
        Left(leftTransform(value))

    override fun hashCode() = 0x60fc3c14 + value.hashCode()
    override fun equals(other: Any?) = other is Left<*> && value == other.value
    override fun toString() = "Disjoint.left($value)"

    companion object {
      @JvmStatic
      @JvmName("of")
      operator fun <A : Any> invoke(value: A) = Left(value)
    }
  }

  companion object {
    @JvmStatic
    fun <A : Any, B : Any> unresolved(value: B): DisjointSource<A, B> = Unresolved(value)

    @JvmStatic
    fun <A : Any, B : Any> left(value: A): Disjoint<A, B> = Left(value)

    inline fun <reified A : Any, B : Any> DisjointSource<A, B>.resolveWith(codec: DisjointCodec) =
        codec.resolveSource(this, typeTokenOf())

    @JvmStatic
    val <A : Any> DisjointSource<A, A>.value
      get() = when (this) {
        is Left -> value
        is Right -> value
        is Unresolved -> value
      }

    @JvmStatic
    fun <A : Any, B : Any> Disjoint<A, B>.swap() =
        when (this) {
          is Left -> swap()
          is Right -> swap()
        }

    @JvmStatic
    @JvmName("swapRight")
    fun <B : Any> Right<*, B>.swap() =
        Left(value)

    @JvmStatic
    @JvmName("swapLeft")
    fun <A : Any> Left<A>.swap() =
        Unresolved(value)
  }
}
