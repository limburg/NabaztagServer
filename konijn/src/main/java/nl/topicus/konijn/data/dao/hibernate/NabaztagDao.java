package nl.topicus.konijn.data.dao.hibernate;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;

import nl.topicus.konijn.data.dao.interfaces.INabaztagDao;
import nl.topicus.konijn.data.entity.Nabaztag;

/**
 * User data access helper
 * 
 * @author Joost Limburg
 * 
 */
public class NabaztagDao extends BaseDao<Nabaztag> implements INabaztagDao {

	@Override
	public boolean authenticate(String uid) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<Nabaztag> c = cb.createQuery(Nabaztag.class);
		Root<Nabaztag> account = c.from(Nabaztag.class);
		Path<String> userQ = account.get("uid");

		c.where(cb.equal(userQ, uid));

		Nabaztag nab = null;
		boolean auth = false;
		try {
			nab = emp.get().createQuery(c).getSingleResult();

			if (nab != null)
				auth = nab.getUid().equals(uid);
		} catch (javax.persistence.NoResultException ex) {
		}

		return auth;
	}

	@Override
	public Nabaztag getNabaztag(String uid) {
		CriteriaBuilder cb = emp.get().getCriteriaBuilder();
		CriteriaQuery<Nabaztag> c = cb.createQuery(Nabaztag.class);
		Root<Nabaztag> account = c.from(Nabaztag.class);
		Path<String> userQ = account.get("uid");
		c.where(cb.equal(userQ, uid));

		Nabaztag nab = null;

		try {
			nab = emp.get().createQuery(c).getSingleResult();

		} catch (javax.persistence.NoResultException ex) {
		}

		return nab;
	}
}