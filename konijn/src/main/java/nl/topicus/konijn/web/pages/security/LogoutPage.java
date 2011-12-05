package nl.topicus.konijn.web.pages.security;

import nl.topicus.konijn.security.AuthenticatedSession;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.pages.SignOutPage;

/**
 * Logout Page
 * 
 * @author Joost Limburg
 * 
 */
public class LogoutPage extends SignOutPage {
	private static final long serialVersionUID = 1L;

	public LogoutPage() {
		super();
		AuthenticatedSession sess = (AuthenticatedSession) Session.get();

		sess.removeUser();
		sess.invalidate();
	}
}
