package nl.topicus.konijn.xmpp.util;

import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.data.entity.Nabaztag;

/**
 * Used in Vysper to communicate with Wicket and the database.
 * 
 * @author Joost Limburg
 * 
 */
public class VysperDelegator {
	private NabaztagDao nabDao;

	public static String passwordRegex = "^00[0-9a-zA-Z]{10,10}$";

	public VysperDelegator(NabaztagDao nabDao) {
		this.nabDao = nabDao;
	}

	/**
	 * Register a nabaztag to the database.
	 * 
	 * @param uid
	 * @param password
	 * @return
	 */
	public boolean vysperRegister(String uid, String password) {
		boolean reg = false;

		if (uid.length() == 12 && password.length() > 5
				&& uid.matches(passwordRegex)) {
			if (nabDao.getNabaztag(uid) == null) {

				Nabaztag nab = new Nabaztag();
				nab.setLeftEar(8);
				nab.setRightEar(1);
				nab.setBlink(1);
				nab.setUid(uid);
				nabDao.save(nab);
				reg = true;
			}
		}
		return reg;
	}

	/**
	 * Return a nabaztag registration from the database.
	 * 
	 * @param uid
	 * @return
	 */
	public Nabaztag vysperGetNabaztag(String uid) {
		return nabDao.getNabaztag(uid);
	}

	/**
	 * Check if a nabaztag exists in the database.
	 * 
	 * @param uid
	 * @return
	 */
	public boolean vysperExists(String uid) {
		if (nabDao.getNabaztag(uid) != null)
			return true;
		else
			return false;
	}

	/**
	 * Authenticate a nabaztag
	 * 
	 * @param uid
	 * @param password
	 * @return
	 */
	public boolean vysperAuthenticate(String uid, String password) {
		return nabDao.authenticate(uid);
	}
}
