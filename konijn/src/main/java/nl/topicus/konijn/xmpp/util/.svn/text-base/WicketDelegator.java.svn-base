package nl.topicus.konijn.xmpp.util;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.xmpp.MyXMPPServer;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.addressing.EntityImpl;
import org.apache.wicket.Application;

/**
 * Used in wicket to communicate with Vysper.
 * 
 * @author Joost Limburg
 * 
 */
public class WicketDelegator {
	/**
	 * Checks weather the nabaztag is online in Vysper.
	 * 
	 * @param uid
	 * @return true nabaztag is online, false nabaztag is offline.
	 */
	public static boolean isNabaztagOnline(String uid) {
		MyXMPPServer server = ((WicketApplication) Application.get())
				.getXmppFactory().getServer();

		Entity bunny = new EntityImpl(uid, server.getServerRuntimeContext()
				.getServerEnitity().getDomain(), null);

		return !server.getServerRuntimeContext().getResourceRegistry()
				.getSessions(bunny).isEmpty();
	}

}
