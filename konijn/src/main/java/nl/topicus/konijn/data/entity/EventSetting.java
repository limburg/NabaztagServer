package nl.topicus.konijn.data.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "EventSetting", uniqueConstraints = @UniqueConstraint(columnNames = {"event","name"}))
/**
 * Nabaztag entity
 * 
 * @author Joost Limburg
 * 
 */
public class EventSetting extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "event", nullable = false)
	private Event event;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

}