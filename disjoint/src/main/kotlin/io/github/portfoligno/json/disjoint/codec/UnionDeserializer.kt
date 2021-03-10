@file:JvmPackagePrivate
package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.util.TokenBuffer
import io.github.portfoligno.json.disjoint.Disjoint
import io.github.portfoligno.json.disjoint.Disjoint.Left
import io.github.portfoligno.json.disjoint.Disjoint.Right
import io.github.portfoligno.json.disjoint.Union
import io.github.portfoligno.json.disjoint.UnresolvedRight
import io.github.portfoligno.json.disjoint.utility.jvm.JvmPackagePrivate

private
fun JsonDeserializer<*>.throwInputMismatch(context: DeserializationContext, message: String): Nothing =
    context.reportInputMismatch(this, message)

private
fun Throwable.throwIfCritical(): Unit =
    when (this) {
      is ThreadDeath, is VirtualMachineError -> throw this
      else -> Unit
    }


@JvmPackagePrivate
internal
class UnionDeserializer : JsonDeserializer<Union<Any, Any>>(), ContextualDeserializer {
  override
  fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
      when (val t = ctxt.contextualType) {
        null -> this
        else -> object : JsonDeserializer<Union<Any, Any>>() {
          override
          fun deserialize(p: JsonParser, ctxt: DeserializationContext): Union<Any, Any> =
              typedDeserialize(p, t, ctxt)!!

          override
          fun getNullValue(ctxt: DeserializationContext): Union<Any, Any>? =
              typedDeserialize(ctxt.parser, t, ctxt)
        }
      }

  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): Union<Any, Any> =
      ctxt.typeFactory.constructType(Any::class.java).let {
        createDisjoint(p, ctxt, it, it)!!
      }

  fun typedDeserialize(
      p: JsonParser, contextualType: JavaType, context: DeserializationContext
  ): Union<Any, Any>? =
      contextualType.run {
        when (rawClass.kotlin) {
          UnresolvedRight::class ->
            p.codec.readValue<Any?>(p, bindings.typeParameters[0])?.let { UnresolvedRight(it) }
          Left::class ->
            p.codec.readValue<Any?>(p, bindings.typeParameters[0])?.let { Left(it) }
          Right::class ->
            createDisjointRight(p, context, bindings.typeParameters[0], bindings.typeParameters[1])
          else ->
            createDisjoint(p, context, bindings.typeParameters[0], bindings.typeParameters[1])
        }
      }

  private
  fun <A : Any, B : Any> createDisjoint(
      p: JsonParser, context: DeserializationContext, leftType: JavaType, rightType: JavaType
  ): Disjoint<A, B>? =
      TokenBuffer(p).deserialize(p, context).let { tokens ->
        val leftValue = try {
          p.codec.readValue<A?>(tokens.asParser(), leftType)
        } catch (t: Throwable) {
          t.throwIfCritical()
          null
        }
        if (leftValue != null) {
          Left(leftValue)
        } else {
          p.codec.readValue<B?>(tokens.asParser(), rightType)?.let { Right(it) }
        }
      }

  private
  fun <A : Any, B : Any> createDisjointRight(
      p: JsonParser, context: DeserializationContext, leftType: JavaType, rightType: JavaType
  ): Right<A, B>? =
      TokenBuffer(p).deserialize(p, context).let { tokens ->
        val isLeft = try {
          p.codec.readValue<Any?>(tokens.asParser(), leftType) != null
        } catch (t: Throwable) {
          t.throwIfCritical()
          false
        }
        if (isLeft) {
          throwInputMismatch(
              context,
              "$rightType expected, but $leftType (${tokens.asParser().readValueAsTree<TreeNode>()}) was found"
          )
        } else {
          p.codec.readValue<B?>(tokens.asParser(), rightType)?.let { Right(it) }
        }
      }
}
