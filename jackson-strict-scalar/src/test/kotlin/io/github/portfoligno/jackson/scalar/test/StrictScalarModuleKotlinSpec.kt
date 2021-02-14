@file:Suppress("BlockingMethodInNonBlockingContext")
package io.github.portfoligno.jackson.scalar.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.github.portfoligno.jackson.scalar.DiscreteFloatingPointRoundingMode
import io.github.portfoligno.jackson.scalar.StrictScalarModule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class StrictScalarModuleKotlinSpec : StringSpec({
  val mappers = Arb.element(DiscreteFloatingPointRoundingMode
      .values()
      .map { ObjectMapper().registerModule(StrictScalarModule(it)) })

  "Round-trip for `String` should work" {
    checkAll(mappers, Arb.string()) { m, v ->
      m.readValue<String>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `Char` should work" {
    checkAll(mappers, Arb.char()) { m, v ->
      m.readValue<Char>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `CharArray` should work" {
    checkAll(mappers, Arb.list(Arb.char()).map { it.toCharArray() }) { m, v ->
      m.readValue<CharArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `Boolean` should work" {
    checkAll(mappers, Arb.bool()) { m, v ->
      m.readValue<Boolean>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `BooleanArray` should work" {
    checkAll(mappers, Arb.list(Arb.bool()).map { it.toBooleanArray() }) { m, v ->
      m.readValue<BooleanArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `AtomicBoolean` should work" {
    checkAll(mappers, Arb.bool().map(::AtomicBoolean)) { m, v ->
      m.readValue<AtomicBoolean>(m.writeValueAsString(v)).get() shouldBe v.get()
    }
  }
  "Round-trip for `BigDecimal` should work" {
    checkAll(mappers, Arb.double().filter(Double::isFinite).map(::BigDecimal)) { m, v ->
      m.readValue<BigDecimal>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `BigInteger` should work" {
    checkAll(mappers, Arb.bigInt(256)) { m, v ->
      m.readValue<BigInteger>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `Double` should work" {
    checkAll(mappers, Arb.double().filter(Double::isFinite)) { m, v ->
      val result = m.readValue<Double>(m.writeValueAsString(v))
      (result == v) shouldBe true // `shouldBe` have issue with -0.0
    }
  }
  "Round-trip for `DoubleArray` should work" {
    checkAll(mappers, Arb.list(Arb.double().filter(Double::isFinite)).map { it.toDoubleArray() }) { m, v ->
      val result = m.readValue<DoubleArray>(m.writeValueAsString(v))
      (result.zip(v).all { (a, b) -> a == b }) shouldBe true // `shouldBe` have issue with -0.0
    }
  }
  "Round-trip for `Float` should work" {
    checkAll(mappers, Arb.float().filter(Float::isFinite)) { m, v ->
      m.readValue<Float>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `FloatArray` should work" {
    checkAll(mappers, Arb.list(Arb.float().filter(Float::isFinite)).map { it.toFloatArray() }) { m, v ->
      m.readValue<FloatArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `Long` should work" {
    checkAll(mappers, Arb.long()) { m, v ->
      m.readValue<Long>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `LongArray` should work" {
    checkAll(mappers, Arb.list(Arb.long()).map { it.toLongArray() }) { m, v ->
      m.readValue<LongArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `AtomicLong` should work" {
    checkAll(mappers, Arb.long().map(::AtomicLong)) { m, v ->
      m.readValue<AtomicLong>(m.writeValueAsString(v)).get() shouldBe v.get()
    }
  }
  "Round-trip for `Int` should work" {
    checkAll(mappers, Arb.int()) { m, v ->
      m.readValue<Int>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `IntArray` should work" {
    checkAll(mappers, Arb.list(Arb.int()).map { it.toIntArray() }) { m, v ->
      m.readValue<IntArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `AtomicInteger` should work" {
    checkAll(mappers, Arb.int().map(::AtomicInteger)) { m, v ->
      m.readValue<AtomicInteger>(m.writeValueAsString(v)).get() shouldBe v.get()
    }
  }
  "Round-trip for `Short` should work" {
    checkAll(mappers, Arb.short()) { m, v ->
      m.readValue<Short>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `ShortArray` should work" {
    checkAll(mappers, Arb.list(Arb.short()).map { it.toShortArray() }) { m, v ->
      m.readValue<ShortArray>(m.writeValueAsString(v)) shouldBe v
    }
  }
  "Round-trip for `Byte` should work" {
    checkAll(mappers, Arb.byte()) { m, v ->
      m.readValue<Byte>(m.writeValueAsString(v)) shouldBe v
    }
  }
})
