package molab.main.java.dao;

import java.util.List;

import molab.main.java.entity.CtDispatcher;

import org.hibernate.LockMode;
import org.hibernate.Query;

import static org.hibernate.criterion.Example.create;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * A data access object (DAO) providing persistence and search support for
 * CtDispatcher entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see molab.main.java.entity.CtDispatcher
 * @author MyEclipse Persistence Tools
 */
@Repository
public class CtDispatcherDao extends BaseDao<CtDispatcher> {
	private static final Logger log = LoggerFactory
			.getLogger(CtDispatcherDao.class);
	// property constants
	public static final String USER_ID = "userId";
	public static final String APPLICATION_ID = "applicationId";
	public static final String OPR_TIME = "oprTime";
	public static final String STATE = "state";

	public CtDispatcher findById(java.lang.Integer id) {
		log.debug("getting CtDispatcher instance with id: " + id);
		try {
			CtDispatcher instance = (CtDispatcher) getHibernateTemplate().get(
					"molab.main.java.entity.CtDispatcher", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<CtDispatcher> findByExample(CtDispatcher instance) {
		log.debug("finding CtDispatcher instance by example");
		try {
			List<CtDispatcher> results = (List<CtDispatcher>) getHibernateTemplate()
					.findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List findByProperty(String propertyName, Object value) {
		log.debug("finding CtDispatcher instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from CtDispatcher as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CtDispatcher> findByUserId(Object userId) {
		return findByProperty(USER_ID, userId);
	}

	public List<CtDispatcher> findByApplicationId(Object applicationId) {
		return findByProperty(APPLICATION_ID, applicationId);
	}

	public List<CtDispatcher> findByOprTime(Object oprTime) {
		return findByProperty(OPR_TIME, oprTime);
	}

	public List<CtDispatcher> findByState(Object state) {
		return findByProperty(STATE, state);
	}

	public List findAll() {
		log.debug("finding all CtDispatcher instances");
		try {
			String queryString = "from CtDispatcher";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public CtDispatcher merge(CtDispatcher detachedInstance) {
		log.debug("merging CtDispatcher instance");
		try {
			CtDispatcher result = (CtDispatcher) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(CtDispatcher instance) {
		log.debug("attaching dirty CtDispatcher instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(CtDispatcher instance) {
		log.debug("attaching clean CtDispatcher instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}