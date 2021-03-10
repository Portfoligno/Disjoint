@file:Suppress("BlockingMethodInNonBlockingContext")
package io.github.portfoligno.json.disjoint.test

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.portfoligno.jackson.scalar.StrictScalarModule
import io.github.portfoligno.json.disjoint.Disjoint
import io.github.portfoligno.json.disjoint.Union
import io.github.portfoligno.json.disjoint.UnresolvedRight
import io.github.portfoligno.json.disjoint.codec.DisjointCodec.Companion.toDisjointCodec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DisjointKotlinSpec : StringSpec({
  val m = ObjectMapper().registerModule(StrictScalarModule())
  val codec = m.toDisjointCodec()

  "Resolution as `left` should work" {
    UnresolvedRight(10.0).resolveWith<Int>(codec).left shouldBe 10
  }
  "Resolution as `right` should work" {
    UnresolvedRight("10.0").resolveWith<Int>(codec).right shouldBe "10.0"
  }
  "Deserialization as `right` should work" {
    m.readValue<Disjoint<List<String>, List<Int>>>("[2, 2]").right shouldBe listOf(2, 2)
  }
  "Deserialization as `left` should work" {
    m.readValue<Disjoint<List<Int>, List<String>>>("[2, 2]").left shouldBe listOf(2, 2)
  }
  "Deserialization targeted as `unresolvedRight` should work" {
    m.readValue<UnresolvedRight<List<Int>>>("[2, 2]").value shouldBe listOf(2, 2)
  }
  "Deserialization of nested structure should work" {
    m.readValue<List<Disjoint<List<Int>, String>>>("""["2", [2]]""")[1] shouldBe Disjoint.left(listOf(2))
  }
  "`Union` can be used in deserialization" {
    m.readValue<Union<List<Int>, List<String>>>("[2, 2]") shouldBe Disjoint.left(listOf(2, 2))
  }
  "`Disjoint.Left` can be used in deserialization" {
    m.readValue<Disjoint.Left<List<Int>>>("[2, 2]") shouldBe Disjoint.left(listOf(2, 2))
  }
  "Deserialization as `right` should not accept `left` value" {
    shouldThrow<MismatchedInputException> {
      m.readValue<Disjoint.Right<List<Int>, List<String>>>("[2, 2]")
    }
  }
  "Serialization from `unresolvedRight` should work" {
    m.writeValueAsString(UnresolvedRight(10.0)) shouldBe "10.0"
  }
  "Serialization from `right` should work" {
    m.writeValueAsString(UnresolvedRight(10.0).resolveWith<String>(codec)) shouldBe "10.0"
  }
  "Serialization from `left` should work" {
    m.writeValueAsString(Disjoint.Left(10.0)) shouldBe "10.0"
  }

  "Deserialization as `left` of `NullNode` should work" {
    m.readValue<Disjoint<JsonNode, Int>>("null").left shouldBe NullNode.instance
  }
  "Deserialization as `right` of `NullNode` should work" {
    m.readValue<Disjoint<Int, JsonNode>>("null").right shouldBe NullNode.instance
  }
  "Deserialization as `null` should work" {
    m.readValue<Disjoint<String, Int>>("null") shouldBe null
  }
})
