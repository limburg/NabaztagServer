package nl.topicus.konijn.data.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import org.hibernate.annotations.AccessType;

@Entity
@AccessType("field")
public class PhotoContainer extends BaseEntity {
	private static final long serialVersionUID = 8253440152082513665L;

	@Lob
	@Basic(fetch = FetchType.EAGER, optional = false)
	private byte[] image;

	/**
	 * Hibernate-only constructor.
	 */
	protected PhotoContainer() {
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
}
