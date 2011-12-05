package nl.topicus.konijn.data.dao.hibernate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import nl.topicus.konijn.data.dao.interfaces.IUserDao;
import nl.topicus.konijn.data.entity.User;

/**
 * User data access helper
 * 
 * @author Joost Limburg
 *
 */
public class UserDao extends BaseDao<User> implements IUserDao {

	@Override
	public User getUser(String username) {
		
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> account = c.from(User.class);
		Path<String> userQ = account.get("username");
		c.where(cb.equal(userQ, username));

		User user = null;

		try {
			user = emp.get().createQuery(c).getSingleResult();

		} catch (javax.persistence.NoResultException ex) {
		}

		return user;
	}
	
	@Override
	public User getUser(String username, String password) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<User> c = cb.createQuery(User.class);
		Root<User> account = c.from(User.class);
		Path<String> userQ = account.get("username");
		Path<String> passQ = account.get("password");
		c.where(cb.and(cb.equal(userQ, username), cb.equal(passQ, password)));

		User user = null;

		try {
			user = emp.get().createQuery(c).getSingleResult();

		} catch (javax.persistence.NoResultException ex) {
		}

		return user;
	}

}