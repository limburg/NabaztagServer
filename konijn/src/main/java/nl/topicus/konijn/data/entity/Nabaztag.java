package nl.topicus.konijn.data.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "Nabaztag", uniqueConstraints = @UniqueConstraint(columnNames = "uid"))
/**
 * Nabaztag entity
 * 
 * @author Joost Limburg
 * 
 */
public class Nabaztag extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String uid;

	@Column(nullable = true)
	private String name;

	@Column(nullable = false)
	private int leftEar;

	@Column(nullable = false)
	private int rightEar;

	@Column(nullable = false)
	private int blink;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, fetch = FetchType.LAZY)
	@JoinColumn(name = "photoContainer", nullable = true)
	private PhotoContainer photoContainer;

	@ManyToOne(cascade = { CascadeType.PERSIST })
	@JoinColumn(name = "owner", nullable = true)
	private User owner;

	@OneToMany(mappedBy = "nabaztag", cascade = { CascadeType.PERSIST,
			CascadeType.REMOVE }, fetch = FetchType.LAZY)
	private List<Event> events;

	public Nabaztag() {

	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public void setLeftEar(int leftEar) {
		this.leftEar = leftEar;
	}

	public int getLeftEar() {
		return leftEar;
	}

	public void setRightEar(int rightEar) {
		this.rightEar = rightEar;
	}

	public int getRightEar() {
		return rightEar;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name != null ? name : getUid();
	}

	/**
	 * Must be between 0 and 2.
	 * 
	 * @param blink
	 */
	public void setBlink(int blink) {
		this.blink = blink;
	}

	public int getBlink() {
		return blink;
	}

	public void setPhotoContainer(PhotoContainer photoContainer) {
		this.photoContainer = photoContainer;
	}

	public PhotoContainer getPhotoContainer() {
		return photoContainer;
	}

}