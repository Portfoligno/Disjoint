[![Release](https://jitpack.io/v/io.github.portfoligno/disjoint.svg)](
  https://jitpack.io/#io.github.portfoligno/disjoint
)

Disjoint
===

Encodes the concept of disjoint union concerning JSON conversion as a finite-state machine.

Consider a set union `A ∪ B`. There are three states we are interested in:

* `Left` - the set `A` that encompasses all possibilities of the left-hand side type.

* `UnresolvedRight` - the set `B` that encompasses all possibilities of the right-hand side type.

* `Right` -  the set difference `B \ A` that encompasses possibilities of the right-hand side type
while excludes overlaps with the left-hand side.

A `Disjoint<A, B>` of this library can either be `Left<A>` or `Right<A, B>`.
It describes the union of type `A` and `B \ A`,
and is the desired form that can be accessed through a side resolution.

A `Union<A, B>`, which can either be `Disjoint<A, B>` or `UnresolvedRight<B>`, is the free form.
`Left<A>` and `UnresolvedRight<B>` can be constructed through provided factory methods.
An `UnresolvedRight<B>` can be then resolved to `Left<A>` or `Right<A, B>`, depends on its serialized value.

Only Jackson is supported currently.

## Installation

It is recommended to apply `StrictScalarModule` provided together by this repository.

```kts
repositories {
  mavenCentral()
  maven("https://jitpack.io")
}
dependencies {
  // `Disjoint` data types with codecs
  implementation("io.github.portfoligno.disjoint", "disjoint", VERSION)

  // `StrictScalarModule`
  implementation("io.github.portfoligno.disjoint", "jackson-strict-scalar", VERSION)
}
```

## Basic Usage

```java
ObjectMapper m = new ObjectMapper().registerModule(new StrictScalarModule());
out.println(m.readValue("\"hello\"", new TypeReference<Disjoint<Integer, String>>() { })); // right(hello)
out.println(m.readValue("1.5", new TypeReference<Disjoint<Integer, Double>>() { })); // right(1.5)
out.println(m.readValue("1.0", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)

// Without `StrictScalarModule`
ObjectMapper m1 = new ObjectMapper().configure(ALLOW_COERCION_OF_SCALARS, false);
out.println(m1.readValue("1.5", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)
out.println(m1.readValue("1.0", new TypeReference<Disjoint<Integer, Double>>() { })); // left(1)
```
