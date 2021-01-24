package com.github.f4b6a3.ulid.util.internal;

import static org.junit.Assert.assertEquals;

import java.util.Random;
import java.util.UUID;

import org.junit.Test;

import com.github.f4b6a3.ulid.Ulid;

public class UlidTest {

	private static final int DEFAULT_LOOP_MAX = 100_000;

	protected static final char[] ALPHABET_CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
	protected static final char[] ALPHABET_JAVA = "0123456789abcdefghijklmnopqrstuv".toCharArray(); // Long.parseUnsignedLong()

	@Test
	public void testOfAndToString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			String string0 = toString(uuid0);
			String string1 = Ulid.of(string0).toString();
			assertEquals(string0, string1);
		}
	}

	@Test
	public void testOfAndToUuid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			UUID uuid0 = UUID.randomUUID();
			UUID uuid1 = Ulid.of(uuid0).toUuid();
			assertEquals(uuid0.toString(), uuid1.toString());
		}
	}

	@Test
	public void testConstructorLongs() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			Ulid ulid0 = Ulid.of(msb, lsb); // <-- under test

			assertEquals(msb, ulid0.toUuid().getMostSignificantBits());
			assertEquals(lsb, ulid0.toUuid().getLeastSignificantBits());
		}
	}

	@Test
	public void testConstructorString() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			Ulid ulid0 = Ulid.of(random1, random2);

			String string1 = toString(ulid0);
			Ulid struct1 = Ulid.of(string1); // <-- under test
			assertEquals(ulid0, struct1);
		}
	}

	@Test
	public void testConstructorUuid() {
		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long msb = random.nextLong();
			final long lsb = random.nextLong();
			final UUID uuid0 = new UUID(msb, lsb);
			Ulid struct0 = Ulid.of(uuid0); // <-- under test

			UUID uuid1 = toUuid(struct0);
			assertEquals(uuid0, uuid1);
		}
	}

	@Test
	public void testToString() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			Ulid ulid0 = Ulid.of(random1, random2);

			String string1 = toString(ulid0);
			String string2 = ulid0.toString(); // <-- under test
			assertEquals(string1, string2);
		}
	}

	@Test
	public void testToUuid() {

		for (int i = 0; i < DEFAULT_LOOP_MAX; i++) {
			Random random = new Random();
			final long random1 = random.nextLong();
			final long random2 = random.nextLong();
			Ulid ulid0 = Ulid.of(random1, random2);

			UUID uuid1 = toUuid(ulid0);
			UUID uuid2 = ulid0.toUuid(); // <-- under test
			assertEquals(uuid1, uuid2);
		}
	}

	public static Ulid fromString(String string) {

		long time = 0;
		long random1 = 0;
		long random2 = 0;

		String tm = string.substring(0, 10);
		String r1 = string.substring(10, 18);
		String r2 = string.substring(18, 26);

		tm = transliterate(tm, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r1 = transliterate(r1, ALPHABET_CROCKFORD, ALPHABET_JAVA);
		r2 = transliterate(r2, ALPHABET_CROCKFORD, ALPHABET_JAVA);

		time = Long.parseUnsignedLong(tm, 32);
		random1 = Long.parseUnsignedLong(r1, 32);
		random2 = Long.parseUnsignedLong(r2, 32);

		long msb = (time << 16) | (random1 >>> 24);
		long lsb = (random1 << 40) | (random2 & 0xffffffffffL);

		return Ulid.of(msb, lsb);
	}

	public static UUID toUuid(Ulid struct) {

		long msb = struct.toUuid().getMostSignificantBits();
		long lsb = struct.toUuid().getLeastSignificantBits();

		return new UUID(msb, lsb);
	}

	public static UUID toUuid(final long time, final long random1, final long random2) {

		long tm = time & 0xffffffffffffL;
		long r1 = random1 & 0xffffffffffL;
		long r2 = random2 & 0xffffffffffL;

		final long msb = (tm << 16) | (r1 >>> 24);
		final long lsb = (r1 << 40) | r2;

		return new UUID(msb, lsb);
	}

	public static String toString(Ulid ulid) {
		return toString(ulid.toUuid().getMostSignificantBits(), ulid.toUuid().getLeastSignificantBits());
	}

	public static String toString(UUID uuid) {
		final long msb = uuid.getMostSignificantBits();
		final long lsb = uuid.getLeastSignificantBits();
		return toString(msb, lsb);
	}

	public static String toString(final long msb, final long lsb) {
		String timeComponent = toTimeComponent(msb >>> 16);
		String randomComponent = toRandomComponent(msb, lsb);
		return timeComponent + randomComponent;
	}

	public static String toTimeComponent(final long time) {
		final String tzero = "0000000000";
		String tm = Long.toUnsignedString(time, 32);
		tm = tzero.substring(0, tzero.length() - tm.length()) + tm;
		return transliterate(tm, ALPHABET_JAVA, ALPHABET_CROCKFORD);
	}

	public static String toRandomComponent(final long msb, final long lsb) {

		final String zeros = "00000000";

		final long random1 = ((msb & 0xffffL) << 24) | (lsb >>> 40);
		final long random2 = (lsb & 0xffffffffffL);

		String r1 = Long.toUnsignedString(random1, 32);
		String r2 = Long.toUnsignedString(random2, 32);

		r1 = zeros.substring(0, zeros.length() - r1.length()) + r1;
		r2 = zeros.substring(0, zeros.length() - r2.length()) + r2;

		r1 = transliterate(r1, ALPHABET_JAVA, ALPHABET_CROCKFORD);
		r2 = transliterate(r2, ALPHABET_JAVA, ALPHABET_CROCKFORD);

		return r1 + r2;
	}

	private static String transliterate(String string, char[] alphabet1, char[] alphabet2) {
		char[] output = string.toCharArray();
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < alphabet1.length; j++) {
				if (output[i] == alphabet1[j]) {
					output[i] = alphabet2[j];
					break;
				}
			}
		}
		return new String(output);
	}
}