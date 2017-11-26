package molab.main.java.dao;

import java.util.List;

import molab.main.java.entity.CtSubRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * A data access object (DAO) providing persistence and search support for
 * CtSubRunner entities. Transaction control of the save(), update() and
 * delete() operations can directly support Spring container-managed
 * transactions or they can be augmented to handle user-managed Spring
 * transactions. Each of these methods provides additional information for how
 * to configure it for the desired type of transaction control.
 * 
 * @see molab.main.java.entity.CtSubRunner
 * @author MyEclipse Persistence Tools
 */
@Repository
public class CtSubRunnerDao extends BaseDao<CtSubRunner> {
	private static final Logger log = LoggerFactory
			.getLogger(CtSubRunnerDao.class);
	// property constants
	public static final String RUNNER_ID = "runnerId";

	public CtSubRunner findById(java.lang.Integer id) {
		log.debug("getting CtSubRunner instance with id: " + id);
		try {
			CtSubRunner instance = (CtSubRunner) getHibernateTemplate().get(
					"molab.main.java.entity.CtSubRunner", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<CtSubRunner> findByExample(CtSubRunner instance) {
		log.debug("finding CtSubRunner instance by example");
		try {
			List<CtSubRunner> results = (List<CtSubRunner>) getHibernateTemplate()
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
		log.debug("finding CtSubRunner instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from CtSubRunner as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CtSubRunner> findByRunnerId(Object runnerId) {
		return findByProperty(RUNNER_ID, runnerId);
	}

	public List findAll() {
		log.debug("finding all CtSubRunner instances");
		try {
			String queryString = "from CtSubRunner";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

}