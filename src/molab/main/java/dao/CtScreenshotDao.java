package molab.main.java.dao;

import java.util.List;

import molab.main.java.entity.CtScreenshot;

import org.hibernate.LockMode;
import org.hibernate.Query;

import static org.hibernate.criterion.Example.create;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * A data access object (DAO) providing persistence and search support for
 * CtScreenshot entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see molab.main.java.entity.CtScreenshot
 * @author MyEclipse Persistence Tools
 */
@Repository
public class CtScreenshotDao extends BaseDao<CtScreenshot> {
	private static final Logger log = LoggerFactory
			.getLogger(CtScreenshotDao.class);
	// property constants
	public static final String RUNNER_ID = "runnerId";
	public static final String IMAGE = "image";
	public static final String COMMENT = "comment";
	public static final String OPR_TIME = "oprTime";

	public CtScreenshot findById(java.lang.Integer id) {
		log.debug("getting CtScreenshot instance with id: " + id);
		try {
			CtScreenshot instance = (CtScreenshot) getHibernateTemplate().get(
					"molab.main.java.entity.CtScreenshot", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<CtScreenshot> findByExample(CtScreenshot instance) {
		log.debug("finding CtScreenshot instance by example");
		try {
			List<CtScreenshot> results = (List<CtScreenshot>) getHibernateTemplate()
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
		log.debug("finding CtScreenshot instance with property: "
				+ propertyName + ", value: " + value);
		try {
			String queryString = "from CtScreenshot as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CtScreenshot> findByRunnerId(Object runnerId) {
		return findByProperty(RUNNER_ID, runnerId);
	}

	public List<CtScreenshot> findByImage(Object image) {
		return findByProperty(IMAGE, image);
	}

	public List<CtScreenshot> findByComment(Object comment) {
		return findByProperty(COMMENT, comment);
	}

	public List<CtScreenshot> findByOprTime(Object oprTime) {
		return findByProperty(OPR_TIME, oprTime);
	}

	public List findAll() {
		log.debug("finding all CtScreenshot instances");
		try {
			String queryString = "from CtScreenshot";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public CtScreenshot merge(CtScreenshot detachedInstance) {
		log.debug("merging CtScreenshot instance");
		try {
			CtScreenshot result = (CtScreenshot) getHibernateTemplate().merge(
					detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(CtScreenshot instance) {
		log.debug("attaching dirty CtScreenshot instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(CtScreenshot instance) {
		log.debug("attaching clean CtScreenshot instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
}