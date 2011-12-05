package nl.topicus.konijn.security;

import nl.topicus.konijn.data.entity.User;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authorization.strategies.role.IRoleCheckingStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 * User authorizer
 * 
 * @author Joost Limburg
 *
 */
public class UserRolesAuthorizer implements IRoleCheckingStrategy {

	/**
	 * Construct.
	 */
	public UserRolesAuthorizer() {
	}

	/**
	 * @see org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy#hasAnyRole(Roles)
	 */
	public boolean hasAnyRole(Roles roles) {
		AuthenticatedSession authSession = (AuthenticatedSession) Session.get();
		User user = authSession.getUser();
		boolean check = false;

		if (user != null)
			check = user.hasAnyRole(roles);

		return check;
	}

}
