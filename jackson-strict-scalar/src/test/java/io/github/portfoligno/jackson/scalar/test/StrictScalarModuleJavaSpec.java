package io.github.portfoligno.jackson.scalar.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.portfoligno.jackson.scalar.StrictScalarModule;
import io.github.portfoligno.json.disjoint.test.utility.StringSpec;

import static io.github.portfoligno.jackson.scalar.DiscreteFloatingPointRoundingMode.SHORTEN_EXCLUSIVE;
import static io.github.portfoligno.jackson.scalar.DiscreteFloatingPointRoundingMode.UNNECESSARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StrictScalarModuleJavaSpec extends StringSpec {{
  ObjectMapper m = new ObjectMapper().registerModule(new StrictScalarModule());
  ObjectMapper unnecessary = new ObjectMapper().registerModule(new StrictScalarModule(UNNECESSARY));
  ObjectMapper shortedExclusive = new ObjectMapper().registerModule(new StrictScalarModule(SHORTEN_EXCLUSIVE));

  describe("Coercion as `int` should not work", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("\"3\"", int.class)
  ));
  describe("Coercion as `Integer` should not work", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("\"3\"", Integer.class)
  ));
  describe("JSON number without decimal place should deserialize into `int`", () -> assertEquals(
      3,
      m.readValue("3", int.class)
  ));
  describe("JSON number with decimal place should deserialize into `int`", () -> assertEquals(
      3,
      m.readValue("3.0", int.class)
  ));
  describe("JSON number with exponent part should deserialize into `int`", () -> assertEquals(
      3,
      m.readValue("3e0", int.class)
  ));
  describe("JSON number with non-zero fractional part should not deserialize into `int`", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("3.1", int.class)
  ));
  describe("Overflow during deserialization into `short` should be detected", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("32768", short.class)
  ));
  describe("Underflow during deserialization into `short` should be detected", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("-32769", short.class)
  ));
  describe("Overflow during deserialization into `byte` should be detected", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("128", byte.class)
  ));
  describe("Underflow during deserialization into `byte` should be detected", () -> assertThrows(
      MismatchedInputException.class,
      () -> m.readValue("-129", byte.class)
  ));
  describe("Serialize `Float` as shortened form should work", () -> assertEquals(
      "0.1",
      m.writeValueAsString(0.1f)
  ));
  describe("Serialize `Float` as precise form should work", () -> assertEquals(
      "0.100000001490116119384765625",
      unnecessary.writeValueAsString(0.1f)
  ));
  describe("Deserialization as `Float` should detect precision loss if requested", () -> assertThrows(
      MismatchedInputException.class,
      () -> unnecessary.readValue("0.100000001", float.class)
  ));
  describe("Deserialization as `Float` should detect value shift if requested", () -> assertThrows(
      MismatchedInputException.class,
      () -> shortedExclusive.readValue("0.100000001490116119384765625", float.class)
  ));
}}
