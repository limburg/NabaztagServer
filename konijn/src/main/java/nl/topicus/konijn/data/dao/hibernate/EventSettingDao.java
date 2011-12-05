package nl.topicus.konijn.data.dao.hibernate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import nl.topicus.konijn.data.dao.interfaces.IEventSettingDao;
import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.EventSetting;

/**
 * EventSetting Data access helper
 * 
 * @author Joost Limburg
 * 
 */
public class EventSettingDao extends BaseDao<EventSetting> implements
		IEventSettingDao {
	public EventSetting getEventSetting(String name, Event event) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<EventSetting> c = cb.createQuery(EventSetting.class);
		Root<EventSetting> eve = c.from(EventSetting.class);
		Path<String> eveQ = eve.get("event");
		Path<String> typeQ = eve.get("name");
		c.where(cb.and(cb.equal(eveQ, event), cb.equal(typeQ, name)));

		EventSetting eventS = null;
		try {
			eventS = emp.get().createQuery(c).getSingleResult();
		} catch (javax.persistence.NoResultException ex) {
		}

		return eventS;
	}
}
