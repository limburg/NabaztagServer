package nl.topicus.konijn.violet.factories;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

public abstract class AbstractVioletFactory {
	static final Logger LOGGER = Logger.getLogger(MessagePacketFactory.class);

	private static int[] inv8 = { 1, 171, 205, 183, 57, 163, 197, 239, 241, 27,
			61, 167, 41, 19, 53, 223, 225, 139, 173, 151, 25, 131, 165, 207,
			209, 251, 29, 135, 9, 243, 21, 191, 193, 107, 141, 119, 249, 99,
			133, 175, 177, 219, 253, 103, 233, 211, 245, 159, 161, 75, 109, 87,
			217, 67, 101, 143, 145, 187, 221, 71, 201, 179, 213, 127, 129, 43,
			77, 55, 185, 35, 69, 111, 113, 155, 189, 39, 169, 147, 181, 95, 97,
			11, 45, 23, 153, 3, 37, 79, 81, 123, 157, 7, 137, 115, 149, 63, 65,
			235, 13, 247, 121, 227, 5, 47, 49, 91, 125, 231, 105, 83, 117, 31,
			33, 203, 237, 215, 89, 195, 229, 15, 17, 59, 93, 199, 73, 51, 85,
			255 };

	protected static byte[] crypt8(String src, int key, int alpha) {
		try {
			final byte[] buf = src.getBytes("ISO-8859-1");
			int theKey = key;
			for (int i = 0; i < buf.length; i++) {
				final byte v = buf[i];
				final int x = alpha + v * inv8[theKey >> 1];
				buf[i] = (byte) x;
				theKey = (v + v + 1) & 255;
			}
			return buf;
		} catch (final UnsupportedEncodingException t) {
			LOGGER.debug("!exception in crypt8");
			LOGGER.fatal(t, t);
		}
		return null;
	}

	protected static void writeIntTo3Bytes(ByteArrayOutputStream inStream, int v) {
		inStream.write((byte) ((v >> 16) & 255));
		inStream.write((byte) ((v >> 8) & 255));
		inStream.write((byte) (v & 255));
	}
}
