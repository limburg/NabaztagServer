package nl.topicus.konijn.data.dao.interfaces;

import nl.topicus.konijn.data.entity.User;

/**
 * Entity interface implemented by user.
 * 
 * @author Joost Limburg
 */
public interface IUserDao extends IBaseDao<User> {

	public User getUser(String username, String password);

	User getUser(String username);
}
