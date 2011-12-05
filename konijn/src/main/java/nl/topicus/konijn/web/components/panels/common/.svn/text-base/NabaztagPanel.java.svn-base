package nl.topicus.konijn.web.components.panels.common;

import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.data.entity.User;
import nl.topicus.konijn.models.PersistenceListModel;
import nl.topicus.konijn.models.PersistenceModel;
import nl.topicus.konijn.security.AuthenticatedSession;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.odlabs.wiquery.ui.tabs.Tabs;

@AuthorizeInstantiation(Roles.USER)
public class NabaztagPanel extends Panel {
	private static final long serialVersionUID = 1L;

	private PersistenceModel<User> user;

	private Tabs tabs;

	public NabaztagPanel(String name, final PersistenceModel<Nabaztag> currentNabaztag) {
		super(name);
		user = new PersistenceModel<User>(
				((AuthenticatedSession) Session.get()).getUser());

		PersistenceListModel<Nabaztag> bunnieList = new PersistenceListModel<Nabaztag>(
				user.getObject().getNabaztags());

		add(tabs = new Tabs("nabaztags"));

		tabs.add(new ListView<Nabaztag>("nabaztagList", bunnieList) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Nabaztag> item) {
				item.add(new ExternalLink("name", "#name" + item.getIndex(),
						item.getModelObject().getName()));
			}
		}.setRenderBodyOnly(true).setOutputMarkupId(true));
		tabs.add(new ListView<Nabaztag>("nabaztagContainer", bunnieList) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<Nabaztag> item) {
				Long id = null;
				if (currentNabaztag != null)
					id = currentNabaztag.getId();
				else if (currentNabaztag == null)
					this.setStartIndex(0);
				
				if (item.getModelObject().getId().equals(id))
					this.setStartIndex(item.getIndex());
				
				item.setMarkupId("name" + item.getIndex());
				item.add(new NabaztagContainerPanel("container",
						new PersistenceModel<Nabaztag>(item.getModelObject())));
			}
		}.setRenderBodyOnly(true).setOutputMarkupId(true));
		tabs.add(new AddNabaztagPanel("addNabaztagContainer"));
	}
}
