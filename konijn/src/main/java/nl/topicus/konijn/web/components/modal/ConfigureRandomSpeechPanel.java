package nl.topicus.konijn.web.components.modal;

import java.util.Date;

import nl.topicus.konijn.data.dao.hibernate.EventDao;
import nl.topicus.konijn.data.dao.hibernate.EventSettingDao;
import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.EventSetting;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.models.PersistenceModel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.button.ButtonBehavior;
import org.odlabs.wiquery.ui.core.DefaultJsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.slider.Slider;

import com.google.inject.Inject;

public class ConfigureRandomSpeechPanel extends Panel {
	private static String EVENT_CLASS = "RandomSpeech";

	private static String EVENT_SETTING = "frequency";

	private static final long serialVersionUID = 1L;

	@Inject
	NabaztagDao nabDao;

	@Inject
	private EventDao eventDao;

	@Inject
	private EventSettingDao eventSettingDao;

	private PersistenceModel<Event> event;

	private Slider slider;

	private HiddenField<String> hiddenField;

	private PersistenceModel<EventSetting> eSetting;
	
	private Form<String> form;
	
	public ConfigureRandomSpeechPanel(String id,
			PersistenceModel<Nabaztag> nabaztag) {
		super(id);
		
		setEventClass(nabaztag);

		form = new Form<String>("form");

		slider = new Slider("slider", 0, 3);
		slider.setOutputMarkupId(true);
		slider.setMarkupId("slider");
		slider.setChangeEvent(new DefaultJsScopeUiEvent(
				"$('#sliderval').val($( \"#slider\" ).slider( \"option\", \"value\" ));"));

		eSetting = new PersistenceModel<EventSetting>(
				eventSettingDao.getEventSetting(EVENT_SETTING,
						event.getObject()));

		if (eSetting.getId() != null) {
			slider.setValue(Integer.parseInt(eSetting.getObject().getValue()));
			hiddenField = new HiddenField<String>("sliderval",
					new Model<String>(eSetting.getObject().getValue()));
		} else {
			hiddenField = new HiddenField<String>("sliderval",
					new Model<String>("0"));
		}
		hiddenField.setOutputMarkupId(true);
		hiddenField.setMarkupId("sliderval");

		form.add(hiddenField);
		form.add(new AjaxButton("submit") {
			private static final long serialVersionUID = 1L;

			@Inject
			private EventSettingDao esDao;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				String value = hiddenField.getModelObject();
				if (value != null && Integer.valueOf(value) >= 0
						&& Integer.valueOf(value) <= 3) {
					EventSetting tmpE = esDao.find(eSetting.getId());
					tmpE.setValue(value);
					esDao.save(tmpE);
				}
				
				((Dialog)getParent().getParent().getParent()).close(target);
				//setResponsePage(UserHomePage.class);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				((Dialog)getParent().getParent().getParent()).close(target);
			}

		}.add(new ButtonBehavior()));
		form.add(slider);
		form.setOutputMarkupId(true);

		add(form);

		// eventDao.getEvent()
	}

	private void setEventClass(PersistenceModel<Nabaztag> nabaztag) {
		Event tmpEvent = eventDao.getEvent(EVENT_CLASS, nabaztag.getObject());
		if (tmpEvent == null) {
			tmpEvent = new Event();
			tmpEvent.setChangedAt(new Date());
			tmpEvent.setCreatedAt(new Date());
			tmpEvent.setEventClass(EVENT_CLASS);
			tmpEvent.setNabaztag(nabDao.find(nabaztag.getId()));
			eventDao.save(tmpEvent);

			EventSetting eventSetting = new EventSetting();
			eventSetting.setValue("0");
			eventSetting.setEvent(tmpEvent);
			eventSetting.setName(EVENT_SETTING);
			eventSettingDao.save(eventSetting);
		}
		event = new PersistenceModel<Event>(tmpEvent);
	}

	/*
	 * List<Message> messages = new ArrayList<Message>();
	 * 
	 * messages.add(new Message("ID", "12626205")); messages.add(new
	 * Message("CL", "16711680"));
	 * 
	 * switch (new Random().nextInt(3)) { case 0: messages.add(new Message("MU",
	 * "broadcast/broad/test.mp3")); break; case 1: messages.add(new
	 * Message("MU", "broadcast/broad/killallhumans.mp3")); break; case 2:
	 * messages.add(new Message("MU", "broadcast/broad/agressie.mp3")); break; }
	 * 
	 * messages.add(new Message("MW", null));
	 * 
	 * MessagePacketFactory.sendMessage(nabaztag.getObject().getUid() ,
	 * messages);
	 */
}
