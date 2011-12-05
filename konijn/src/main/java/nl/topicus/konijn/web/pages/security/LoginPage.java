package nl.topicus.konijn.web.pages.security;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Login Page
 * 
 * @author Joost Limburg
 *
 */
public class LoginPage extends WebPage {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            Parameters to page
	 */
	public LoginPage(final PageParameters parameters) {
		add(new SignInPanel("signInPanel", false));
	}
}
