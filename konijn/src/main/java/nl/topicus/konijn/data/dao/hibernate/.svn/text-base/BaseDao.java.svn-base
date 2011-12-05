package nl.topicus.konijn.data.dao.hibernate;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import nl.topicus.konijn.data.dao.interfaces.IBaseDao;
import nl.topicus.konijn.data.entity.BaseEntity;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

/**
 * Base data access helper class
 * 
 * @author Joost Limburg
 *
 * @param <T>
 */
public abstract class BaseDao<T extends BaseEntity> implements IBaseDao<T> {
	@Inject
	protected Provider<EntityManager> emp;

	private Class<T> persistentClass;

	public BaseDao(final Class<T> clazz) {
		persistentClass = clazz;
	}

	/**
	 * Constructor for dependency injection.
	 * 
	 * @param persistentClass
	 *            the class type you'd like to persist.
	 */
	@SuppressWarnings("unchecked")
	protected BaseDao() {
		Class<?> cl = getClass();

		if (Object.class.getSimpleName().equals(
				cl.getSuperclass().getSimpleName())) {
			throw new IllegalArgumentException(
					"Default constructor does not support direct instantiation");
		}

		while (!BaseDao.class.getSimpleName().equals(
				cl.getSuperclass().getSimpleName())) {
			// case of multiple inheritance, we are trying to get the first
			// available generic info
			if (cl.getGenericSuperclass() instanceof ParameterizedType) {
				break;
			}
			cl = cl.getSuperclass();
		}

		if (cl.getGenericSuperclass() instanceof ParameterizedType) {
			persistentClass = (Class<T>) ((ParameterizedType) cl
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}
	}

	/**
	 * @param emp
	 */
	public void setEmp(Provider<EntityManager> emp) {
		this.emp = emp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.dddabs.model.IEntity#find(java.io.Serializable)
	 */
	public T find(Long id) {
		return emp.get().find(persistentClass, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#findAll()
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		List<T> list = emp.get()
				.createQuery("FROM " + persistentClass.getSimpleName())
				.getResultList();

		if (list == null)
			list = new ArrayList<T>();

		return list;
	}

	public List<T> findIn(List<Long> idList) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<T> query2 = cb.createQuery(persistentClass);
		Root<T> fetch2 = query2.from(persistentClass);
		query2.where(fetch2.<Long> get("id").in(idList));
		// execute the query
		List<T> result = emp.get().createQuery(query2).getResultList();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.dddabs.model.IEntity#save(java.lang.Object)
	 */
	@Transactional
	public T merge(T object) {
		return emp.get().merge(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.dddabs.model.IEntity#save(java.lang.Object)
	 */
	@Transactional
	public void save(T object) {
		emp.get().persist(object);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.dddabs.model.IEntity#remove(java.io.Serializable)
	 */
	@Transactional
	public void remove(Long id) {
		EntityManager em = emp.get();
		em.remove(em.find(persistentClass, id));
	}
}
