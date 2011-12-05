package nl.topicus.konijn.web.components.panels.events;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class AddNabaztagContainer extends Panel {

	private static final long serialVersionUID = 1L;

	public AddNabaztagContainer(String id) {
		super(id);
		Form<String> form = new Form<String>("form");
		add(form);
	}
}
