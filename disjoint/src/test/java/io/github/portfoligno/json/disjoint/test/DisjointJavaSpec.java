package io.github.portfoligno.json.disjoint.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeToken;
import io.github.portfoligno.jackson.scalar.StrictScalarModule;
import io.github.portfoligno.json.disjoint.Disjoint;
import io.github.portfoligno.json.disjoint.DisjointSource;
import io.github.portfoligno.json.disjoint.codec.DisjointCodec;
import io.github.portfoligno.json.disjoint.test.utility.StringSpec;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("UnstableApiUsage")
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
}}
