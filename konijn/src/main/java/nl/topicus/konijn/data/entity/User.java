package nl.topicus.konijn.data.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

@Entity
@Table(name = "NabaztagUser", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
/**
 * User entity
 * 
 * @author Joost Limburg
 * 
 */
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@OneToMany(mappedBy = "owner", cascade = { CascadeType.PERSIST,
			CascadeType.REMOVE }, fetch = FetchType.LAZY)
	private List<Nabaztag> nabaztags;

	@Column(nullable = false)
	private Roles roles;

	public User() {
		roles = new Roles();
	}

	/**
	 * Whether this user has the given role.
	 * 
	 * @param role
	 * @return whether this user has the given role
	 */
	public boolean hasRole(String role) {
		return this.roles.hasRole(role);
	}

	/**
	 * Whether this user has any of the given roles.
	 * 
	 * @param roles
	 *            set of roles
	 * @return whether this user has any of the given roles
	 */
	public boolean hasAnyRole(Roles roles) {
		return this.roles.hasAnyRole(roles);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setRoles(String roles) {
		this.roles.add(roles);
	}

	public Roles getRoles() {
		return roles;
	}

	public void setNabaztags(List<Nabaztag> nabaztags) {
		this.nabaztags = nabaztags;
	}

	public List<Nabaztag> getNabaztags() {
		return nabaztags;
	}
}