package nl.topicus.konijn.web.components.panels.common;

import nl.topicus.konijn.WicketApplication;
import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.data.dao.hibernate.UserDao;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.data.entity.User;
import nl.topicus.konijn.models.PersistenceModel;
import nl.topicus.konijn.security.AuthenticatedSession;
import nl.topicus.konijn.web.pages.home.UserHomePage;
import nl.topicus.konijn.xmpp.util.VysperDelegator;

import org.apache.wicket.Application;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.dialog.util.DialogUtilsBehavior;

import com.google.inject.Inject;

public class AddNabaztagPanel extends Panel {
	private static final long serialVersionUID = 1L;

	// Properties
	@Inject
	private NabaztagDao nabDao;

	@Inject
	private UserDao userDao;

	private DialogUtilsBehavior dialogUtilsBehavior;

	private TextField<String> textField;

	public AddNabaztagPanel(String id) {
		super(id);

		dialogUtilsBehavior = new DialogUtilsBehavior();
		add(dialogUtilsBehavior);

		Form<String> inputForm = new Form<String>("nabForm");

		textField = new TextField<String>("serial");
		textField.setOutputMarkupId(true);
		textField.setModel(new Model<String>(""));
		textField.setRequired(true);

		AjaxSubmitLink submitLink = new AjaxSubmitLink("add") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// Variables needed to process this submit:
				AuthenticatedSession session = (AuthenticatedSession) Session
						.get();
				WicketApplication wi = (WicketApplication) Application.get();
				String error = "";
				String result = textField.getModelObject().toLowerCase();
				Nabaztag nab = null;

				// Check input:
				if (result == null || result.length() != 12
						|| !result.matches(VysperDelegator.passwordRegex)) {
					error = "Invalid Nabaztag identifier used.";
				} else {
					nab = nabDao.getNabaztag(result);
				}

				// Check if bunny connected to server:
				if (error.length() == 0
						&& !wi.getDelegatorInstance().vysperExists(result)) {
					error = "The Nabaztag is not connected to the server.";
				}

				// Check if bunny has been added to the database:
				if (error.length() == 0 && nab == null) {
					error = "The Nabaztag has never been connected to the server.";
				}

				// Check if bunny has an owner:
				if (error.length() == 0 && nab != null
						&& nab.getOwner() != null) {
					error = "This Nabaztag has an owner already.";
				}

				// On Success:
				if (error.length() == 0) {
					User user = userDao.find(session.getUser().getId());
					nab.setOwner(user);
					nabDao.save(nab);
					setResponsePage(new UserHomePage(new PersistenceModel<Nabaztag>(nabDao.getNabaztag(result))));
				} else {
					// On Fail:
					target.appendJavaScript(dialogUtilsBehavior
							.warningDialog(error).render().toString());
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.appendJavaScript(dialogUtilsBehavior
						.warningDialog("You forgot to add your Nabaztag ID!")
						.render().toString());
			}
		};
		inputForm.add(textField);
		inputForm.add(submitLink);
		add(inputForm);
	}
}
