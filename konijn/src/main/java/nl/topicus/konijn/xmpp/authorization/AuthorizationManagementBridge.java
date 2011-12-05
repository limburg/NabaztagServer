package nl.topicus.konijn.xmpp.authorization;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.xmpp.util.WicketInjector;

import org.apache.log4j.Logger;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.authentication.UserAuthentication;
import org.apache.vysper.xmpp.server.SessionContext;

/**
 * Bridge between authorization from wicket/database and XMppserver
 * 
 * @author Joost Limburg
 * 
 */
public class AuthorizationManagementBridge implements UserAuthentication {
	private static final Logger LOGGER = Logger
			.getLogger(AuthorizationManagementBridge.class);

	@Override
	public boolean verifyCredentials(Entity jid, String passwordCleartext,
			Object credentials) {
		LOGGER.info("Login for: " + jid.getBareJID() + " with "
				+ passwordCleartext);
		WicketApplication wi = WicketInjector
				.inject((SessionContext) credentials);

		boolean result = wi.getDelegatorInstance()
				.vysperAuthenticate(jid.getNode(), passwordCleartext);

		// Try register:
		if (result == false) {
			if (wi.getDelegatorInstance().vysperExists(jid.getNode()) == false) {
				LOGGER.info("Registering: " + jid.getBareJID() + " with "
						+ passwordCleartext);
				result = wi.getDelegatorInstance().vysperRegister(jid.getNode(), passwordCleartext);
			}
		}
		return result;
	}
}
