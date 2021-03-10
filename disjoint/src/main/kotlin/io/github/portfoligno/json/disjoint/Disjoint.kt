package io.github.portfoligno.json.disjoint

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import io.github.portfoligno.json.disjoint.Disjoint.Left
import io.github.portfoligno.json.disjoint.Disjoint.Right
import io.github.portfoligno.json.disjoint.codec.DisjointCodec
import io.github.portfoligno.json.disjoint.codec.UnionDeserializer
import io.github.portfoligno.json.disjoint.codec.UnionSerializer
import io.github.portfoligno.json.disjoint.utility.typeTokenOf

@JsonDeserialize(using = UnionDeserializer::class)
@JsonSerialize(using = UnionSerializer::class)
sealed class Union<out A : Any, out B : Any> {
  companion object {
    @JvmStatic
    fun <A : Any, B : Any> unresolvedRight(value: B): Union<A, B> = UnresolvedRight(value)

    @JvmStatic
    fun <A : Any, B : Any> left(value: A): Union<A, B> = Left(value)

    inline fun <reified A : Any, B : Any> Union<A, B>.resolveWith(codec: DisjointCodec) =
        codec.resolveSource(this, typeTokenOf())

    @JvmStatic
    val <A : Any> Union<A, A>.value
      get() = when (this) {
        is Left -> value
        is Right -> value
        is UnresolvedRight -> value
      }
  }
}

class UnresolvedRight<out B : Any> private constructor (val value: B) : Union<Nothing, B>() {
  inline fun <reified A : Any> resolveWith(codec: DisjointCodec) =
      codec.resolve(this, typeTokenOf<A>())

  override fun hashCode() = 0x19085cbf + value.hashCode()
  override fun equals(other: Any?) = other is UnresolvedRight<*> && value == other.value
  override fun toString() = "UnresolvedRight($value)"

  companion object {
    @JvmStatic
    @JvmName("of")
    operator fun <B : Any> invoke(value: B) = UnresolvedRight(value)
  }
}

sealed class Disjoint<A : Any, out B : Any> : Union<A, B>() {
  abstract val left: A?
  abstract val right: B?

  abstract fun <R> fold(leftTransform: (A) -> R, rightTransform: (B) -> R): R
  abstract fun <R : Any> mapRight(transform: (B) -> R): Union<A, R>
  abstract fun <R : Any> mapLeft(transform: (A) -> R): Union<R, B>
  abstract fun <R : Any, S : Any> bimap(leftTransform: (A) -> R, rightTransform: (B) -> S): Union<R, S>

  // While serialization in Jackson regards only run-time type, i.e. contravariant serializers only,
  // this definition intends to address invariant serializers too.
  // (Effectively, `A` has the same variance as the serializer.)
  class Right<A : Any, out B : Any> private constructor (val value: B) : Disjoint<A, B>() {
    override val left: Nothing? get() = null
    override val right get() = value

    override fun <R> fold(leftTransform: (A) -> R, rightTransform: (B) -> R) =
        rightTransform(value)
    override fun <R : Any> mapRight(transform: (B) -> R) =
        UnresolvedRight(transform(value))
    override fun <R : Any> mapLeft(transform: (A) -> R) =
        UnresolvedRight(value)
    override fun <R : Any, S : Any> bimap(leftTransform: (A) -> R, rightTransform: (B) -> S) =
        UnresolvedRight(rightTransform(value))

    override fun hashCode() = 0x6c49acc3 + value.hashCode()
    override fun equals(other: Any?) = other is Right<*, *> && value == other.value
    override fun toString() = "Disjoint.right($value)"

    companion object {
      @JvmSynthetic
      internal
      operator fun <A : Any, B : Any> invoke(value: B) = Right<A, B>(value)
    }
  }

  class Left<out A : Any> private constructor (val value: A) : Disjoint<@UnsafeVariance A, Nothing>() {
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
    fun <A : Any, B : Any> left(value: A): Disjoint<A, B> = Left(value)

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
        UnresolvedRight(value)
  }
}
