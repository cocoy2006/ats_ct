package molab.main.java.dao;

import java.util.List;

import molab.main.java.entity.CtRunner;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * A data access object (DAO) providing persistence and search support for
 * CtRunner entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see molab.main.java.entity.CtRunner
 * @author MyEclipse Persistence Tools
 */
@Repository
public class CtRunnerDao extends BaseDao<CtRunner> {
	private static final Logger log = LoggerFactory
			.getLogger(CtRunnerDao.class);
	// property constants
	public static final String DISPATCHER_ID = "dispatcherId";
	public static final String DEVICE_ID = "deviceId";
	public static final String AVERAGE_CPU = "averageCpu";
	public static final String AVERAGE_MEMORY = "averageMemory";
	public static final String INSTALL_TIME = "installTime";
	public static final String INSTALL_RESULT = "installResult";
	public static final String LOAD_TIME = "loadTime";
	public static final String UNINSTALL_TIME = "uninstallTime";
	public static final String LOGCAT = "logcat";
	public static final String STATE = "state";
	
	public CtRunner findById(java.lang.Integer id) {
		log.debug("getting CtRunner instance with id: " + id);
		try {
			CtRunner instance = (CtRunner) getHibernateTemplate().get(
					"molab.main.java.entity.CtRunner", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<CtRunner> findByExample(CtRunner instance) {
		log.debug("finding CtRunner instance by example");
		try {
			List<CtRunner> results = (List<CtRunner>) getHibernateTemplate()
					.findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<CtRunner> findByProperty(String propertyName, Object value) {
		log.debug("finding CtRunner instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from CtRunner as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CtRunner> findByDispatcherId(Object dispatcherId) {
		return findByProperty(DISPATCHER_ID, dispatcherId);
	}

	public List<CtRunner> findByDeviceId(Object deviceId) {
		return findByProperty(DEVICE_ID, deviceId);
	}

	public List<CtRunner> findByAverageCpu(Object averageCpu) {
		return findByProperty(AVERAGE_CPU, averageCpu);
	}

	public List<CtRunner> findByAverageMemory(Object averageMemory) {
		return findByProperty(AVERAGE_MEMORY, averageMemory);
	}

	public List<CtRunner> findByInstallTime(Object installTime) {
		return findByProperty(INSTALL_TIME, installTime);
	}

	public List<CtRunner> findByInstallResult(Object installResult) {
		return findByProperty(INSTALL_RESULT, installResult);
	}

	public List<CtRunner> findByLoadTime(Object loadTime) {
		return findByProperty(LOAD_TIME, loadTime);
	}

	public List<CtRunner> findByUninstallTime(Object uninstallTime) {
		return findByProperty(UNINSTALL_TIME, uninstallTime);
	}

	public List<CtRunner> findByLogcat(Object logcat) {
		return findByProperty(LOGCAT, logcat);
	}

	public List<CtRunner> findByState(Object state) {
		return findByProperty(STATE, state);
	}

	public List<CtRunner> findAll() {
		log.debug("finding all CtRunner instances");
		try {
			String queryString = "from CtRunner";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public CtRunner merge(CtRunner detachedInstance) {
		log.debug("merging CtRunner instance");
		try {
			CtRunner result = (CtRunner) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(CtRunner instance) {
		log.debug("attaching dirty CtRunner instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(CtRunner instance) {
		log.debug("attaching clean CtRunner instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public List<CtRunner> findByState(int state, String server, int port) {
		String hql = "from CtRunner where state=? and deviceId in (select id from Device where server=? and port=?)";
		return getHibernateTemplate().find(hql, state, server, port);
	}
	
}