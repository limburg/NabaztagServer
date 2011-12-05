package nl.topicus.konijn.data.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "Event", uniqueConstraints = @UniqueConstraint(columnNames = {"nabaztag","eventClass"}))
/**
 * Event entity
 * 
 * @author Joost Limburg
 */
public class Event extends BaseEntity {
	public Event() {
	}

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "event", cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
			fetch = FetchType.LAZY)
	private List<EventSetting> settings;
	
	@Column(nullable = false)
	private String eventClass;

	@ManyToOne(cascade = {CascadeType.PERSIST})
	@JoinColumn(name = "nabaztag", nullable = false)
	private Nabaztag nabaztag;

	public List<EventSetting> getSettings() {
		return settings;
	}

	public void setSettings(List<EventSetting> settings) {
		this.settings = settings;
	}

	public String getEventClass() {
		return eventClass;
	}

	public void setEventClass(String eventClass) {
		this.eventClass = eventClass;
	}

	public Nabaztag getNabaztag() {
		return nabaztag;
	}

	public void setNabaztag(Nabaztag nabaztag) {
		this.nabaztag = nabaztag;
	}
	
	
}