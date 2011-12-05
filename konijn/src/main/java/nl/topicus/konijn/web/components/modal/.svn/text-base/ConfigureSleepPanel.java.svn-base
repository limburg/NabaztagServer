package nl.topicus.konijn.web.components.modal;

import nl.topicus.konijn.data.dao.hibernate.EventDao;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.models.PersistenceModel;

import org.apache.wicket.markup.html.panel.Panel;

import com.google.inject.Inject;

public class ConfigureSleepPanel extends Panel{
	private static final long serialVersionUID = 1L;
	
	@Inject
	private EventDao eventDao;
	
	private PersistenceModel<Nabaztag> nabaztag;
	
	public ConfigureSleepPanel(String id, PersistenceModel<Nabaztag> nabaztag) {
		super(id);
		this.nabaztag = nabaztag;
		
		//eventDao.getEvent()
	}

	/*
	 * List<Message> messages = new ArrayList<Message>();
	 * 
	 * messages.add(new Message("ID", "12626205")); messages.add(new
	 * Message("CL", "16711680"));
	 * 
	 * switch (new Random().nextInt(3)) { case 0: messages.add(new
	 * Message("MU", "broadcast/broad/test.mp3")); break; case 1:
	 * messages.add(new Message("MU",
	 * "broadcast/broad/killallhumans.mp3")); break; case 2:
	 * messages.add(new Message("MU",
	 * "broadcast/broad/agressie.mp3")); break; }
	 * 
	 * messages.add(new Message("MW", null));
	 * 
	 * MessagePacketFactory.sendMessage(nabaztag.getObject().getUid()
	 * , messages);
	 */
}
