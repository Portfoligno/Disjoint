package io.github.portfoligno.json.disjoint.test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.portfoligno.jackson.scalar.StrictScalarModule;
import io.github.portfoligno.json.disjoint.Disjoint;
import io.github.portfoligno.json.disjoint.DisjointSource;
import io.github.portfoligno.json.disjoint.codec.DisjointCodec;
import io.github.portfoligno.json.disjoint.test.utility.StringSpec;
import io.github.portfoligno.json.disjoint.utility.TypeToken;

import java.util.List;

import static com.fasterxml.jackson.databind.MapperFeature.ALLOW_COERCION_OF_SCALARS;
import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DisjointJavaSpec extends StringSpec {{
  DisjointCodec c = DisjointCodec.from(new ObjectMapper().registerModule(new StrictScalarModule()));

  describe("`resolve` should work", () -> assertEquals(
      Disjoint.Right.class,
      c.resolve(DisjointSource.Unresolved.of(""), new TypeToken<List<String>>() { }).getClass()
  ));
  describe("`resolveSource` should work", () -> assertEquals(
      Disjoint.Right.class,
      c.resolveSource(Disjoint.unresolved(1), new TypeToken<String>() { }).getClass()
  ));
  describe("`mapRight` should work", () -> {
    DisjointSource<String, Integer> v =
        c.resolveSource(Disjoint.unresolved(1), new TypeToken<String>() { }).mapRight(i -> i + 1);

    assertEquals(Disjoint.unresolved(2), v);
  });
  describe("`swap` should work", () -> assertEquals(
    Disjoint.unresolved(5),
    Disjoint.swap(Disjoint.left(5))
  ));
  describe("README example should work", () -> {
    ObjectMapper m = new ObjectMapper().registerModule(new StrictScalarModule());
    out.println(m.readValue("\"hello\"", new TypeReference<Disjoint<Integer, String>>() { })); // right(hello)
    out.println(m.readValue("1.5", new TypeReference<Disjoint<Integer, Double>>() { })); // right(1.5)
    out.println(m.readValue("1.0", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)

    // Without `StrictScalarModule`
    ObjectMapper m1 = new ObjectMapper().configure(ALLOW_COERCION_OF_SCALARS, false);
    out.println(m1.readValue("1.5", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)
    out.println(m1.readValue("1.0", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)
  });
}}
