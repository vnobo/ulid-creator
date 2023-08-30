
ULID Creator
======================================================

This is a Java implementation of [Universally Unique Lexicographically Sortable Identifier](https://github.com/ulid/spec).

In summary:

* Sorted by generation time;
* Can be stored as a UUID/GUID;
* Can be stored as a string of 26 chars;
* Can be stored as an array of 16 bytes;
* String format is encoded to [Crockford's base32](https://www.crockford.com/base32.html);
* String format is URL safe, is case insensitive, and has no hyphens.

This project contains a [micro benchmark](https://github.com/f4b6a3/ulid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/ulid-creator/tree/master/src/test/java/com/github/f4b6a3/ulid).

The jar file can be downloaded directly from [maven.org](https://repo1.maven.org/maven2/com/github/f4b6a3/ulid-creator/).

Read the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/ulid-creator).

Usage
------------------------------------------------------

Create a ULID:

```java
Ulid ulid = UlidCreator.getUlid();
```

Create a Monotonic ULID:

```java
Ulid ulid = UlidCreator.getMonotonicUlid();
```

### Dependency

Add these lines to your `pom.xml`.

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/ulid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>ulid-creator</artifactId>
  <version>5.2.1</version>
</dependency>
```
See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/ulid-creator).

### Modularity

Module and bundle names are the same as the root package name.

- JPMS module name: `com.github.f4b6a3.ulid`
- OSGi symbolic name: `com.github.f4b6a3.ulid`

### ULID

ULID is a 128-bit identifier. The first 48 bits represent the number of milliseconds since Unix Epoch, 1970-01-01. The remaining 80 bits are generated by a secure random number generator. Its canonical string representation is 26 characters long.


```java
// Generate a ULID
Ulid ulid = UlidCreator.getUlid();
```

Structure of a series of ULIDs:

```text
01EX8Y21KBH49ZZCA7KSKH6X1C
01EX8Y21KBJTFK0JV5J20QPQNR
01EX8Y21KBG2CS1V6WQCTVM7K6
01EX8Y21KB8HPZNBP3PTW7HVEY
01EX8Y21KB3HZV38VAPTPAG1TY
01EX8Y21KB9FTEJHPAGAKYG9Z8
01EX8Y21KBQGKGH2SVPQAYEFFC
01EX8Y21KBY17J9WR9KQR8SE7H
01EX8Y21KCVHYSJGVK4HBXDMR9 < millisecond changed
01EX8Y21KC668W3PEDEAGDHMVG
01EX8Y21KC53D2S5ADQ2EST327
01EX8Y21KCPQ3TENMTY1S7HV56
01EX8Y21KC3755QF9STQEV05EB
01EX8Y21KC5ZSHK908GMDK69WE
01EX8Y21KCSGJS8S1FVS06B3SX
01EX8Y21KC6ZBWQ0JBV337R1CN
         ^ look

|---------|--------------|
    time      random
```

### Monotonic ULID

Monotonic ULID is a variant of ULID. The random component is incremented by 1 whenever the current millisecond equals the previous millisecond. Its main advantage is *speed*.

```java
// Generate a Monotonic ULID
Ulid ulid = UlidCreator.getMonotonicUlid();
```

Structure of a series of Monotonic ULIDs:

```text
01EX8Y7M8MDVX3M3EQG69EEMJW
01EX8Y7M8MDVX3M3EQG69EEMJX
01EX8Y7M8MDVX3M3EQG69EEMJY
01EX8Y7M8MDVX3M3EQG69EEMJZ
01EX8Y7M8MDVX3M3EQG69EEMK0
01EX8Y7M8MDVX3M3EQG69EEMK1
01EX8Y7M8MDVX3M3EQG69EEMK2
01EX8Y7M8MDVX3M3EQG69EEMK3
01EX8Y7M8N1G30CYF2PJR23J2J < millisecond changed
01EX8Y7M8N1G30CYF2PJR23J2K
01EX8Y7M8N1G30CYF2PJR23J2M
01EX8Y7M8N1G30CYF2PJR23J2N
01EX8Y7M8N1G30CYF2PJR23J2P
01EX8Y7M8N1G30CYF2PJR23J2Q
01EX8Y7M8N1G30CYF2PJR23J2R
01EX8Y7M8N1G30CYF2PJR23J2S
         ^ look          ^ look

|---------|--------------|
    time      random
```

### Hash ULID

Hash ULID is a "non-standard" variant of ULID. It always returns the same ULID for a specific pair of arguments. It has the same basic structure as a ULID, except that the random component is replaced with the first 10 bytes of an SHA-256 hash.

```java
// Generate a Hash ULID of a string
long time = file.getCreatedAt();
String name = file.getFileName();
Ulid ulid = UlidCreator.getHashUlid(time, name);
```

```java
// Generate a Hash ULID of a byte array
long time = file.getCreatedAt();
byte[] bytes = file.getFileBinary();
Ulid ulid = UlidCreator.getHashUlid(time, bytes);
```

Structure of Hash ULIDs:

```text
01GZ8AR53SE5XCA1MN1PGCSDJ0

|---------|--------------|

    time        hash
```

### More Examples

Create a quick ULID:

```java
Ulid ulid = Ulid.fast();
```

---

Create a ULID from a canonical string (26 chars):

```java
Ulid ulid = Ulid.from("0123456789ABCDEFGHJKMNPQRS");
```

---

Convert a ULID into a canonical string in lower case:

```java
String string = ulid.toLowerCase(); // 0123456789abcdefghjkmnpqrs
```

---

Convert a ULID into a UUID:

```java
UUID uuid = ulid.toUuid(); // 0110c853-1d09-52d8-d73e-1194e95b5f19
```

---

Convert a ULID into a [RFC-4122](https://tools.ietf.org/html/rfc4122) UUID v4:

```java
UUID uuid = ulid.toRfc4122().toUuid(); // 0110c853-1d09-42d8-973e-1194e95b5f19
                                       //               ^ UUID v4
```

---

Get the creation instant of a ULID:

```java
Instant instant = ulid.getInstant(); // 2007-02-16T02:13:14.633Z
```

---

```java
// static method
Instant instant = Ulid.getInstant("0123456789ABCDEFGHJKMNPQRS"); // 2007-02-16T02:13:14.633Z
```

---

Get only the time component substring:

```java
String ulidTimePart = ulid.toString()
                          .substring(0, Ulid.TIME_CHARS); // 0123456789
```

---

Get only the random component substring:

```java
String ulidRandPart = ulid.toString()
                          .substring(Ulid.TIME_CHARS, Ulid.RANDOM_CHARS); // ABCDEFGHJKMNPQRS
```

---

Insert a string between the time and random components efficiently (avoiding concatenation) ([#29](https://github.com/f4b6a3/ulid-creator/pull/29)):

```java
String ulidExpanded = new StringBuilder(ulid.toString())
                          .insert(Ulid.TIME_CHARS, "-INSERTED-")
                          .toString(); // 0123456789-INSERTED-ABCDEFGHJKMNPQRS
```

---

A key generator that makes substitution easy if necessary:

```java
package com.example;

import com.github.f4b6a3.ulid.UlidCreator;

public class KeyGenerator {
    public static String next() {
        return UlidCreator.getUlid().toString();
    }
}
```
```java
// use the generator
String key = KeyGenerator.next();
```

---

A `UlidFactory` with `java.util.Random`:

```java
// use a `java.util.Random` instance for fast generation
UlidFactory factory = UlidFactory.newInstance(new Random());
```
```java
// use the factory
Ulid ulid = factory.create();
```

---

A `UlidFactory` with `RandomGenerator` (JDK 17+):

```java
// use a random function that returns a long value
RandomGenerator random = RandomGenerator.getDefault();
UlidFactory factory = UlidFactory.newInstance(() -> random.nextLong());
```
```java
// use the factory
Ulid ulid = factory.create();
```

---

Benchmark
------------------------------------------------------

This section shows benchmarks comparing `UlidCreator` to `UUID.randomUUID()`.

```
--------------------------------------------------------------------------------
THROUGHPUT (operations/msec)            Mode  Cnt      Score      Error   Units
--------------------------------------------------------------------------------
UUID_randomUUID                        thrpt    5   1787,601 ±  489,482  ops/ms ( 1.0)
UUID_randomUUID_toString               thrpt    5   1598,467 ±   93,067  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
Ulid_fast                              thrpt    5 100521,737 ±27132,748  ops/ms (56.2)
Ulid_fast_toString                     thrpt    5  50529,743 ± 8970,574  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
UlidCreator_getUlid                    thrpt    5   2443,810 ±  379,149  ops/ms ( 1.3)
UlidCreator_getUlid_toString           thrpt    5   2200,047 ±  539,767  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
UlidCreator_getMonotonicUlid           thrpt    5   9845,810 ± 1239,148  ops/ms ( 5.5)
UlidCreator_getMonotonicUlid_toString  thrpt    5   5748,568 ±  549,121  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
UlidCreator_getHashUlid                thrpt    5   6651,239 ± 2541,774  ops/ms
UlidCreator_getHashUlidString          thrpt    5   6043,582 ± 1879,042  ops/ms
--------------------------------------------------------------------------------
Total time: 00:03:22
--------------------------------------------------------------------------------
```

System: CPU i7-8565U, 16G RAM, Ubuntu 22.04, JVM 11, rng-tools installed.

To execute the benchmark, run `./benchmark/run.sh`.

Other identifier generators
-------------------------------------------

Check out the other ID generators from the same family:

* [UUID Creator](https://github.com/f4b6a3/uuid-creator): Universally Unique Identifiers
* [TSID Creator](https://github.com/f4b6a3/tsid-creator): Time Sortable Identifiers
* [KSUID Creator](https://github.com/f4b6a3/ksuid-creator): K-Sortable Globally Unique Identifiers

License
------------------------------------------------------

This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).

