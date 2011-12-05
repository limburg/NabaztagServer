package nl.topicus.konijn.violet;

public enum SOURCE {
	PACKET_START(0x7f), 
	AMBIENT(0x4), 
	MESSAGE(0xa), 
	LEFT_EAR(0x4), 
	RIGHT_EAR(0x5), 
	BELLY(0x8), 
	EOF(0xff), 
	DUMMY1(0xffffffff), 
	DUMMY2(0xfffffffe), 
	SLEEP(0xb), 
	DISABLE(0x0);

	private final int id;

	SOURCE(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public static SOURCE findById(Integer id) {
		for (final SOURCE aMode : SOURCE.values()) {
			if (aMode.getId() == id) {
				return aMode;
			}
		}
		return null;
	}
};