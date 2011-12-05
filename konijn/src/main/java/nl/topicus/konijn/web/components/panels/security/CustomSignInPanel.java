package nl.topicus.konijn.web.components.panels.security;

import org.apache.wicket.authroles.authentication.panel.SignInPanel;

public class CustomSignInPanel extends SignInPanel {
	private static final long serialVersionUID = 1L;

	public CustomSignInPanel(String id) {
		super(id);
	}
	
	public CustomSignInPanel(String id, boolean remember) {
		super(id,remember);
	}
}
