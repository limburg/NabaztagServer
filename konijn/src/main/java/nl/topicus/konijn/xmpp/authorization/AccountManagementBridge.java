package nl.topicus.konijn.xmpp.authorization;

import nl.topicus.konijn.WicketApplication;

import org.apache.log4j.Logger;
import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.authentication.AccountCreationException;
import org.apache.vysper.xmpp.authentication.AccountManagement;
import org.apache.wicket.Application;

/**
 * Account bridge between XMPPServer and shared wicket/database space.
 * 
 * @author Joost Limburg
 * 
 */
public class AccountManagementBridge implements AccountManagement {

	private static final Logger LOGGER = Logger
			.getLogger(AccountManagementBridge.class);

	@Override
	public void addUser(Entity user, String password)
			throws AccountCreationException {
		WicketApplication app = (WicketApplication) Application.get();

		if (!app.getDelegatorInstance().vysperRegister(user.getNode(), password)) {
			LOGGER.warn("XMPP Auth failed to addUser: " + user.getNode());
			throw new AccountCreationException();
		}

		LOGGER.info("XMPP Auth addUser: " + user.getNode());
	}

	@Override
	public void changePassword(Entity user, String password)
			throws AccountCreationException {
		LOGGER.info("XMPP Auth ignore: changePwd");
	}

	@Override
	public boolean verifyAccountExists(Entity arg0) {
		WicketApplication app = (WicketApplication) Application.get();
		return app.getDelegatorInstance().vysperExists(arg0.getNode());
	}

}
