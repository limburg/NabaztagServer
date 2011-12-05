package nl.topicus.konijn.web.components.panels.events;

import java.util.ArrayList;
import java.util.List;

import nl.topicus.konijn.violet.Message;
import nl.topicus.konijn.violet.factories.MessagePacketFactory;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

public class RadioContainerPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private Label listenLinkText;
	private String nabUid;
	private TextField<String> textField;

	public RadioContainerPanel(String id, String nabUid) {
		super(id);
		this.nabUid = nabUid;

		Form<String> inputForm = new Form<String>("radioForm");

		textField = new TextField<String>("listenUrl");
		textField.setOutputMarkupId(true);
		textField.setModel(new Model<String>(""));

		inputForm.add(textField);
		listenLinkText = new Label("listenLinkText","Start listening");
		
		AjaxSubmitLink listenLink = new AjaxSubmitLink("listenLink") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				if (textField.isEnabled())
				{
					listenLinkText.setDefaultModelObject("Stop listening");
					textField.setEnabled(false);
					target.add(listenLinkText);
					target.add(textField);
					String url = textField.getModelObject();
					List<Message> messages = new ArrayList<Message>();
	
					messages.add(new Message("ID", "12626205"));
					messages.add(new Message("CL", "16711680"));
					messages.add(new Message("ST", url));
					messages.add(new Message("MW", null));
					MessagePacketFactory.sendMessage(getNabUid(), messages);
				} else {
					listenLinkText.setDefaultModelObject("Start listening");
					textField.setEnabled(true);
					target.add(listenLinkText);
					target.add(textField);
					
					List<Message> messages = new ArrayList<Message>();
					messages.add(new Message("ID", "12626205"));
					messages.add(new Message("CL", "16711680"));
					messages.add(new Message("MW", null));
					MessagePacketFactory.sendMessage(getNabUid(), messages);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				// TODO Auto-generated method stub
			}
		};
		listenLinkText.setOutputMarkupId(true);
		listenLink.add(listenLinkText);
		
		inputForm.add(listenLink);
		add(inputForm.setOutputMarkupId(true));
	}

	public String getNabUid() {
		return nabUid;
	}
}
