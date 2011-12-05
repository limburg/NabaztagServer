package nl.topicus.konijn.data.dao.interfaces;

import nl.topicus.konijn.data.entity.Event;
import nl.topicus.konijn.data.entity.EventSetting;

/**
 * Entity interface implemented by eventSetting.
 * 
 * @author Joost Limburg
 */
public interface IEventSettingDao extends IBaseDao<EventSetting> {
	public EventSetting getEventSetting(String name, Event event);
}
