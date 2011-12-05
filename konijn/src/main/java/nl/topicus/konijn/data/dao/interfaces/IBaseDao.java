package nl.topicus.konijn.data.dao.interfaces;

import java.util.List;

import javax.persistence.EntityManager;

import nl.topicus.konijn.data.entity.IBaseEntity;

import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

/**
 * Entity interface implemented by all persistent classes.
 * 
 * @author Joost Limburg
 */
public interface IBaseDao<T extends IBaseEntity> {

	/**
	 * @param emp
	 */
	public void setEmp(Provider<EntityManager> emp);

	/**
	 * Generic method to get an object based on class and identifier.
	 * 
	 * @param id
	 *            the identifier (primary key) of the object to get.
	 * @return a populated object.
	 */
	public T find(Long id);

	/**
	 * Generic method used to get all objects of a particular type. This is the
	 * same as lookup up all rows in a table.
	 * 
	 * @return the list of populated objects.
	 */
	public List<T> findAll();

	/**
	 * Generic method to save an object - handles both update and insert.
	 * 
	 * @param object
	 *            the object to save.
	 * @return the persisted object.
	 */
	@Transactional
	public T merge(T object);

	@Transactional
	public void save(T object);
	
	/**
	 * Generic method to delete an object based on class and id.
	 * 
	 * @param id
	 *            the identifier (primary key) of the object to remove.
	 */
	@Transactional
	public void remove(Long id);

	public List<T> findIn(List<Long> idList);

}
