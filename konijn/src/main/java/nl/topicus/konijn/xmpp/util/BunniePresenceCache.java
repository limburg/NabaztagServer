package nl.topicus.konijn.xmpp.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.vysper.xmpp.addressing.Entity;
import org.apache.vysper.xmpp.stanza.PresenceStanza;
import org.apache.vysper.xmpp.state.presence.AbstractBaseCache;
import org.apache.vysper.xmpp.state.presence.PresenceCachingException;

/**
 * 
 * Allow multiple presences, defined by the namespace given by the user.
 * 
 * unbounded in-memory-only cache, but entries are timestamped and oldest entry
 * is the first in list
 * 
 * @author Joost Limburg
 */
public class BunniePresenceCache extends AbstractBaseCache {

	private final Map<Entity, Entry> presenceMap = new LinkedHashMap<Entity, Entry>();

	public boolean isNodePresent(String node) {
		return getNode(node) == null ? false : true;
	}

	public Entity getNode(String node) {
		Set<Entity> keys = presenceMap.keySet();
		for (Entity e : keys) {
			if (e.getNode().equals(node)) {
				return e;
			}
		}
		return null;
	}

	public List<String> getEntities() {
		List<String> entities = new ArrayList<String>();
		Set<Entity> keys = presenceMap.keySet();
		for (Entity e : keys) {
			entities.add(e.getFullQualifiedName());
		}

		return entities;
	}

	@Override
	protected void put0(Entity entity, PresenceStanza presenceStanza) {
		checkEntry(entity);
		// force adding at the end, this guarantees that the entry is the latest
		// in getForBareJID()
		presenceMap.remove(entity);
		presenceMap.put(entity, new Entry(presenceStanza));
	}

	@Override
	protected PresenceStanza get0(Entity entity)
			throws PresenceCachingException {
		checkEntry(entity);
		Entry entry = presenceMap.get(entity);
		if (entry == null)
			return null;
		return entry.getPresenceStanza();
	}

	public PresenceStanza getForBareJID(Entity entity)
			throws PresenceCachingException {
		// TODO this is naive and not optimized. the whole key set is traversed
		// every time
		PresenceStanza latest = null;
		for (Entity key : presenceMap.keySet()) {
			if (key.getBareJID().equals(entity)) {
				latest = presenceMap.get(key).getPresenceStanza(); // this is
																	// the
																	// latest
																	// until we
																	// find a
																	// newer one
			}
		}
		return latest;
	}

	public void removeAll(Entity entity)
	{
		List<Entity> remEnt = new ArrayList<Entity>();
		for(Entity ent : presenceMap.keySet())
		{
			if (ent.getNode().equals(entity.getNode()))
				remEnt.add(ent);
		}
		for(Entity ent : remEnt)
			presenceMap.remove(ent);
		
	}
	public void remove(Entity entity) {
		presenceMap.remove(entity);
	}

	static class Entry {
		protected long timestamp = System.currentTimeMillis();

		protected PresenceStanza presenceStanza;

		Entry(PresenceStanza presenceStanza) {
			this.presenceStanza = presenceStanza;
		}

		public long getTimestamp() {
			return timestamp;
		}

		public PresenceStanza getPresenceStanza() {
			return presenceStanza;
		}
	}
}
