package nl.topicus.konijn.models;

import javax.persistence.EntityManager;

import nl.topicus.konijn.data.entity.BaseEntity;

import org.apache.wicket.model.LoadableDetachableModel;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Persistence Model
 * 
 * @author Joost Limburg
 * @author Jeroen Steenbeke
 * 
 * @param <T>
 */
public class PersistenceModel<T extends BaseEntity> extends
		LoadableDetachableModel<T> {
	private static final long serialVersionUID = 1L;

	@Inject
	private transient Provider<EntityManager> em;

	protected Object persistentClass = null;

	private Long id = null;

	@SuppressWarnings("unchecked")
	public PersistenceModel(T myObject) {
		org.apache.wicket.injection.Injector.get().inject(this);

		if (myObject.getId() == null) {
			this.persistentClass = myObject;
		} else {
			this.persistentClass = (Class<T>) myObject.getClass();
		}

		this.id = myObject.getId();
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected T load() {
		if (id != null)
			return (T) em.get().find((Class<T>) persistentClass, id);
		else
			return (T) persistentClass;
	}

	@Override
	public void onDetach() {
		em = null;
	}
}
