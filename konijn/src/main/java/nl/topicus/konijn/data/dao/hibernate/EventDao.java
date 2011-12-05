package nl.topicus.konijn.data.dao.hibernate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import nl.topicus.konijn.data.dao.interfaces.IEventDao;
import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.Nabaztag;

/**
 * Event Data access helper
 * 
 * @author Joost Limburg
 * 
 */
public class EventDao extends BaseDao<Event> implements IEventDao {
	public Event getEvent(String eventClass, Nabaztag nabaztag) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<Event> c = cb.createQuery(Event.class);
		Root<Event> nab = c.from(Event.class);
		Path<String> nabQ = nab.get("nabaztag");
		Path<String> typeQ = nab.get("eventClass");
		c.where(cb.and(cb.equal(nabQ, nabaztag), cb.equal(typeQ, eventClass)));

		Event event = null;
		try {
			event = emp.get().createQuery(c).getSingleResult();
		} catch (javax.persistence.NoResultException ex) {
		}

		return event;
	}
}
