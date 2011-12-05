package nl.topicus.konijn.data.dao.interfaces;

import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.Nabaztag;

/**
 * Entity interface implemented by event.
 * 
 * @author Joost Limburg
 */
public interface IEventDao extends IBaseDao<Event> {
	public Event getEvent(String eventClass, Nabaztag nabaztag);
}
