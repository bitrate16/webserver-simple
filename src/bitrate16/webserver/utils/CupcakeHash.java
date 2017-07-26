package bitrate16.webserver.utils;

/**
 * CupcakeHash class is special util for generating 64bit hash for input
 * strings;
 * 
 * @author bitrate16
 *
 */
public class CupcakeHash {
	public static final String	HASH_BASE_STRING	= "CUPCAKEHASHCODE0CUPCAKEHASHCODE0CUPCAKEHASHCODE0CUPCAKEHASHCODE0";
	public static final byte[]	HASH_BASE			= genBytes(HASH_BASE_STRING);
	public static final String	HASH_TABLE_STRING	= "0ABCDE1FGHIJ2KLMNO3PQRST4UVWXY5Zabcd6efghi7jklmn8opqrs9tuvwx0yzA";
	public static final byte[]	HASH_TABLE			= genBytes(HASH_TABLE_STRING);
	public static final int		HASH_LENGTH			= 64;

	/**
	 * Generates hash for input array of bytes. Returns 64 bit hash.
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] genHash(byte[] in) {
		byte[] hash = new byte[HASH_LENGTH];

		for (int i = 0; i < HASH_LENGTH; i++) {
			hash[i] += in.length * i % 255;
			hash[i] += in.length << HASH_BASE[i];
			for (int j = 0; j < in.length; j++) {
				hash[i] += in[j] * in[j];
				hash[i] *= (j + i + 1);
			}

			// hash[i] = (byte) (hash[i] < 0 ? -hash[i] : hash[i]);
			// hash[i] = HASH_TABLE[hash[i] % HASH_TABLE.length];
		}

		return hash;
	}

	/**
	 * Transforms input string into byte array.
	 * 
	 * @param in
	 * @return
	 */
	public static byte[] genBytes(String in) {
		if (in == null)
			return new byte[0];

		byte[] raw = in.getBytes();
		byte[] bytes = new byte[raw.length];

		for (int i = 0; i < raw.length; i++)
			bytes[i] = raw[i];

		return bytes;
	}

	/**
	 * Transforms 64bit hash into readable String
	 * 
	 * @param in
	 * @return
	 */
	public static String genString(byte[] in) {
		String ret = "";
		for (int i = 0; i < in.length; i++)
			ret += String.format("%02X", in[i]);
		return ret;
	}

	/**
	 * Transforms 64bit hash into readable String using provided chartable
	 * 
	 * @param in
	 * @param table
	 * @return
	 */
	public static String genTabledString(byte[] in, byte[] table) {
		in = applyTable(in, table);
		String ret = "";
		for (int i = 0; i < in.length; i++)
			ret += (char) table[in[i] < 0 ? -in[i] : in[i]];
		return ret;
	}

	/**
	 * Transforms 64bit hash into readable String using default chartable
	 * 
	 * @param in
	 * @return
	 */
	public static String genDefaultTabledString(byte[] in) {
		return genTabledString(in, HASH_TABLE);
	}

	/**
	 * Apply character table to given bytes. for i -> length: bytes[i] %
	 * table.length
	 */
	public static byte[] applyTable(byte[] in, byte[] table) {
		byte[] buf = new byte[in.length];
		for (int i = 0; i < in.length; i++)
			buf[i] = (byte) ((in[i] < 0 ? -in[i] : in[i]) % table.length);
		return buf;
	}

	/**
	 * Apply default chartable
	 */
	public static byte[] applyDefaultTable(byte[] in) {
		return applyTable(in, HASH_TABLE);
	}

	/**
	 * Prints first line with indexes, second line bytes in hex, third line
	 * character, that is equal to this bit in ASCII.
	 * 
	 * @param bytes
	 */
	public static void printBytes(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++)
			System.err.print(String.format("%02X", i) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(String.format("%02X", bytes[i]) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(" " + ((char) bytes[i]) + " ");
		System.out.println();
	}

	/**
	 * Prints first line with indexes, second line bytes in hex, third line
	 * character, that is equal to this bit in ASCII, using table.
	 * 
	 * @param bytes
	 * @param table
	 */
	public static void printBytes(byte[] bytes, byte[] table) {
		bytes = applyTable(bytes, table);
		for (int i = 0; i < bytes.length; i++)
			System.err.print(String.format("%02X", i) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(String.format("%02X", table[bytes[i]]) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(" " + ((char) table[bytes[i]]) + " ");
		System.out.println();
	}

	/**
	 * Prints first line with indexes, second line bytes in hex, third line
	 * character, that is equal to this bit in ASCII, using default table.
	 * 
	 * @param bytes
	 */
	public static void printDefaultBytes(byte[] bytes) {
		bytes = applyTable(bytes, HASH_TABLE);
		for (int i = 0; i < bytes.length; i++)
			System.err.print(String.format("%02X", i) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(String.format("%02X", HASH_TABLE[bytes[i]]) + " ");
		System.out.println();
		for (int i = 0; i < bytes.length; i++)
			System.out.print(" " + ((char) HASH_TABLE[bytes[i]]) + " ");
		System.out.println();
	}
}
