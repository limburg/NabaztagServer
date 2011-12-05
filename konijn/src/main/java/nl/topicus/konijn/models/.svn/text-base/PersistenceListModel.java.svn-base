package nl.topicus.konijn.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import nl.topicus.konijn.data.entity.BaseEntity;

import org.apache.wicket.model.LoadableDetachableModel;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Persistent listmodel
 * 
 * @author Joost Limburg
 * @author Jeroen Steenbeke
 * 
 * @param <T>
 */
public class PersistenceListModel<T extends BaseEntity> extends
		LoadableDetachableModel<List<T>> {
	private static final long serialVersionUID = 1L;

	@Inject
	private transient Provider<EntityManager> em;

	private List<Long> idList = null;

	private Class<T> persistentClass = null;

	@SuppressWarnings("unchecked")
	public PersistenceListModel(List<T> myListObject) {
		org.apache.wicket.injection.Injector.get().inject(this);

		idList = new ArrayList<Long>();
		if (myListObject != null && myListObject.size() > 0) {
			for (T myObject : myListObject) {
				if (myObject.getId() != null) {
					if (persistentClass == null) {
						persistentClass = (Class<T>) myObject.getClass();
					}
					idList.add(myObject.getId());
				}
			}
		}
	}

	@Override
	protected List<T> load() {
		org.apache.wicket.injection.Injector.get().inject(this);
		List<T> entityList = new ArrayList<T>();
		if (idList != null && idList.size() > 0 && persistentClass != null) {
			for (Long id : idList) {
				T entity = em.get().find(persistentClass, id);
				if (entity != null)
					entityList.add(entity);
			}

		}
		return entityList;
	}
}