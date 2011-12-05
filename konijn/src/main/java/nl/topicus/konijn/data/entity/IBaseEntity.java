package nl.topicus.konijn.data.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Entity interface implemented by all persistent classes.
 * 
 * @author Joost Limburg
 */
public interface IBaseEntity extends Serializable {

	/**
	 * @return The unique identifier.
	 */
	public Long getId();

	/**
	 * @param id
	 */
	public void setId(Long id);

	/**
	 * @return The date and time of creation.
	 */
	public Date getCreatedAt();

	/**
	 * @param createdAt
	 */
	public void setCreatedAt(Date createAt);

	/**
	 * @return The date and time of modification.
	 */
	public Date getChangedAt();

	/**
	 * @param changedAt
	 */
	public void setChangedAt(Date changedAt);

	/**
	 * Default model validation.
	 * 
	 * @return
	 */
	public List<String> validate();

}
