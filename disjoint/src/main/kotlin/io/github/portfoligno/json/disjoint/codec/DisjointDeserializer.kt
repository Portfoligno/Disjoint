@file:JvmPackagePrivate
package io.github.portfoligno.json.disjoint.codec

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.util.TokenBuffer
import io.github.portfoligno.json.disjoint.Disjoint
import io.github.portfoligno.json.disjoint.DisjointSource
import io.github.portfoligno.json.disjoint.utility.JvmPackagePrivate

private
fun JsonDeserializer<*>.throwInputMismatch(context: DeserializationContext, message: String): Nothing =
    context.reportInputMismatch(this, message)


@JvmPackagePrivate
internal
class DisjointDeserializer : JsonDeserializer<DisjointSource<Any, Any>>(), ContextualDeserializer {
  override
  fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> =
      when (val t = ctxt.contextualType) {
        null -> this
        else -> object : JsonDeserializer<DisjointSource<Any, Any>>() {
          override
          fun deserialize(p: JsonParser, ctxt: DeserializationContext): DisjointSource<Any, Any> =
              typedDeserialize(p, t, ctxt)
        }
      }

  override
  fun deserialize(p: JsonParser, ctxt: DeserializationContext): DisjointSource<Any, Any> =
      ctxt.typeFactory.constructType(Any::class.java).let {
        createDisjoint(p, ctxt, it, it)
      }

  fun typedDeserialize(
      p: JsonParser, contextualType: JavaType, context: DeserializationContext
  ): DisjointSource<Any, Any> =
      contextualType.run {
        when (rawClass.kotlin) {
          DisjointSource.Unresolved::class ->
            (DisjointSource.Unresolved)(p.codec.readValue(p, bindings.typeParameters[0]))
          Disjoint.Left::class ->
            (Disjoint.Left)(p.codec.readValue(p, bindings.typeParameters[0]))
          Disjoint.Right::class ->
            createDisjointRight(p, context, bindings.typeParameters[0], bindings.typeParameters[1])
          else ->
            createDisjoint(p, context, bindings.typeParameters[0], bindings.typeParameters[1])
        }
      }

  private
  fun <A : Any, B : Any> createDisjoint(
      p: JsonParser, context: DeserializationContext, leftType: JavaType, rightType: JavaType
  ): Disjoint<A, B> =
      TokenBuffer(p).deserialize(p, context).let { tokens ->
        try {
          (Disjoint.Left)(p.codec.readValue(tokens.asParser(), leftType))
        } catch (_: JsonProcessingException) {
          (Disjoint.Right)(p.codec.readValue(tokens.asParser(), rightType))
        }
      }

  private
  fun <A : Any, B : Any> createDisjointRight(
      p: JsonParser, context: DeserializationContext, leftType: JavaType, rightType: JavaType
  ): Disjoint.Right<A, B> =
      TokenBuffer(p).deserialize(p, context).let { tokens ->
        try {
          p.codec.readValue<Any>(tokens.asParser(), leftType)
        } catch (_: JsonProcessingException) {
          return (Disjoint.Right)(p.codec.readValue(tokens.asParser(), rightType))
        }
        val message = "$rightType expected, but $leftType (${tokens.asParser().readValueAsTree<TreeNode>()}) was found"
        throwInputMismatch(context, message)
      }
}
