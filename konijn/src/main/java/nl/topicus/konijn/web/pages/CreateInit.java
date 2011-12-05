package nl.topicus.konijn.web.pages;

import nl.topicus.konijn.data.dao.hibernate.NabaztagDao;
import nl.topicus.konijn.data.dao.hibernate.UserDao;
import nl.topicus.konijn.data.entity.Nabaztag;
import nl.topicus.konijn.data.entity.User;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;

import com.google.inject.Inject;

/**
 * Big boom
 * 
 * @author Joost Limburg
 * 
 */
public class CreateInit extends WebPage {

	private static final long serialVersionUID = 1L;
	@Inject
	private NabaztagDao nabDao;

	@Inject
	private UserDao userDao;

	public CreateInit() {
		User user = new User();
		user.setPassword("wicket");
		user.setUsername("test");
		user.setRoles(Roles.USER);
		userDao.save(user);

		
		Nabaztag nab = new Nabaztag();
		nab.setUid("0013d38627e8");
		nab.setLeftEar(8);
		nab.setRightEar(1);
		nab.setBlink(1);
		nab.setOwner(user);
		nab.setName("Mr. Bunbun");
		nabDao.save(nab);
		
	}
}
