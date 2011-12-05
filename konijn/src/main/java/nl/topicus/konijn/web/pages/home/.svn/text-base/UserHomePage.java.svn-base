package nl.topicus.konijn.web.pages.home;

import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.models.PersistenceModel;
import nl.topicus.konijn.security.AuthenticatedSession;
import nl.topicus.konijn.web.components.panels.common.NabaztagPanel;
import nl.topicus.konijn.web.pages.security.LogoutPage;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

@AuthorizeInstantiation(Roles.USER)
/**
 * User homepage.
 * Secured by role.
 * 
 * @author Joost Limburg
 */
public class UserHomePage extends WebPage {
	private static final long serialVersionUID = 1L;

	public UserHomePage() {
		this(null);
	}
	
	public UserHomePage(PersistenceModel<Nabaztag> nabaztag)
	{
		AuthenticatedSession sess = (AuthenticatedSession)Session.get();
		
		add(new Label("username", sess.getUser().getUsername() ));
		add(new NabaztagPanel("nabaztag", nabaztag));
		add(new BookmarkablePageLink<Object>("logout", LogoutPage.class));
	}
}
