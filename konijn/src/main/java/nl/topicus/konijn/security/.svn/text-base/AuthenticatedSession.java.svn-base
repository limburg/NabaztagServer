package nl.topicus.konijn.security;

import nl.topicus.konijn.data.dao.hibernate.UserDao;
import nl.topicus.konijn.data.entity.User;
import nl.topicus.konijn.models.PersistenceModel;

import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;

import com.google.inject.Inject;

/**
 * Authenticated Session
 * 
 * @author Joost Limburg
 * 
 */
public class AuthenticatedSession extends AuthenticatedWebSession {

	@Inject
	private UserDao userDao;

	private PersistenceModel<User> user;

	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param request
	 *            The current request object
	 */
	@Inject
	public AuthenticatedSession(Request request) {
		super(request);
	}

	/**
	 * @see org.apache.wicket.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean authenticate(final String username, final String password) {
		// Check username and password
		boolean check = false;

		if (username != null && password != null) {
			User tmpUser = userDao.getUser(username, password);

			if (tmpUser != null) {
				user = new PersistenceModel<User>(tmpUser);
				check = true;
			}
		}

		return check;
	}

	/**
	 * @see org.apache.wicket.authentication.AuthenticatedWebSession#getRoles()
	 */
	@Override
	public Roles getRoles() {
		if (isSignedIn() && user != null && user.getObject() != null) {
			// If the user is signed in, they have these roles
			return user.getObject().getRoles();
		}
		return null;
	}

	public void removeUser() {
		if (user != null)
			user.detach();

		this.user = null;
	}

	public User getUser() {
		if (user != null)
			return user.getObject();
		else
			return null;
	}
}
