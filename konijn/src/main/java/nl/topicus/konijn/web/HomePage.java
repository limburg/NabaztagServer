package nl.topicus.konijn.web;

import nl.topicus.konijn.security.AuthenticatedSession;
import nl.topicus.konijn.web.components.panels.security.CustomSignInPanel;
import nl.topicus.konijn.web.pages.home.RegisterPage;
import nl.topicus.konijn.web.pages.home.UserHomePage;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Main page
 * 
 * @author Joost Limburg
 */
public class HomePage extends WebPage {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor that is invoked when page is invoked without a em.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public HomePage(final PageParameters parameters) {
		checkIfLoggedIn();
		// Add the simplest type of label
		// add(new BookmarkablePageLink<Void>("login", UserHomePage.class));
		add(new Link<String>("registerLink"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				setResponsePage(RegisterPage.class);
			}
			
		});
		add(new CustomSignInPanel("signInPanel", false));
	}

	private void checkIfLoggedIn() {
		AuthenticatedSession session = (AuthenticatedSession) Session.get();
		if (session != null && session.getUser() != null) {
			if (session.isSignedIn() && !session.isSessionInvalidated())
				setResponsePage(UserHomePage.class);
		}
	}
}
