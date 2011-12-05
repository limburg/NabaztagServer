package nl.topicus.konijn.violet.factories;

import java.io.ByteArrayOutputStream;

import nl.topicus.konijn.violet.SOURCE;

public class AmbientPacketFactory extends AbstractVioletFactory {
	public static byte[] composeAmbientPacket(int lEar, int rEar,
			int blinkBelly, boolean sleep, boolean disable) {

		ByteArrayOutputStream dataArray = new ByteArrayOutputStream();
		ByteArrayOutputStream packArray = new ByteArrayOutputStream();

		dataArray.write(SOURCE.LEFT_EAR.getId());
		dataArray.write(lEar);
		dataArray.write(SOURCE.RIGHT_EAR.getId());
		dataArray.write(rEar);
		dataArray.write(SOURCE.BELLY.getId());
		dataArray.write(blinkBelly);
		dataArray.write(SOURCE.SLEEP.getId());
		dataArray.write(sleep ? 1 : 0);
		dataArray.write(SOURCE.DISABLE.getId());
		dataArray.write(disable ? 1 : 0);

		packArray.write(SOURCE.PACKET_START.getId());
		packArray.write(SOURCE.AMBIENT.getId());
		writeIntTo3Bytes(packArray, dataArray.size());
		packArray.write(SOURCE.PACKET_START.getId());
		packArray.write(SOURCE.DUMMY1.getId());
		packArray.write(SOURCE.DUMMY1.getId());
		packArray.write(SOURCE.DUMMY2.getId());
		packArray.write(dataArray.toByteArray(), 0, dataArray.size());
		packArray.write(0x0);
		packArray.write(SOURCE.EOF.getId());

		return packArray.toByteArray();
	}
}
