package nl.topicus.konijn.data.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
/**
 * Base entity
 * 
 * @author Joost Limburg
 */
public class BaseEntity implements IBaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 36, nullable = false)
	@GeneratedValue
	protected Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	protected Date createdAt = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "changed_at", nullable = false)
	protected Date changedAt = new Date();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#getId()
	 */
	public Long getId() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#setId(java.lang.String)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#getCreatedAt()
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#setCreatedAt(java.util.Date)
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#getChangedAt()
	 */
	public Date getChangedAt() {
		return changedAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#setChangedAt(java.util.Date)
	 */
	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rapin.ddd.model.IEntity#validate()
	 */
	public List<String> validate() {

		// create our list for errors
		List<String> errors = new ArrayList<String>();

		// Validate the model fields.
		if (this.id == null || this.id == 0) {
			errors.add("Identifier is null or empty.");
		}
		if (this.createdAt == null) {
			errors.add("Created at date is null.");
		}

		// if no errors occured we'll return null.
		if (errors.size() == 0) {
			errors = null;
		}

		// return errors that occured
		return errors;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof IBaseEntity)) {

			return false;
		}

		IBaseEntity other = (IBaseEntity) o;

		// if the id is missing, return false
		if (id == null)
			return false;

		// equivalence by id
		return id.equals(other.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		} else {
			return super.hashCode();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "id: " + id + ", createdAt: " + createdAt.toString()
				+ ", changedAt: " + changedAt.toString();
	}
}
