package molab.main.java.dao;

import java.util.List;

import molab.main.java.entity.CtDevice;

import org.hibernate.LockMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * A data access object (DAO) providing persistence and search support for
 * CtDevice entities. Transaction control of the save(), update() and delete()
 * operations can directly support Spring container-managed transactions or they
 * can be augmented to handle user-managed Spring transactions. Each of these
 * methods provides additional information for how to configure it for the
 * desired type of transaction control.
 * 
 * @see molab.main.java.entity.CtDevice
 * @author MyEclipse Persistence Tools
 */
@Repository
public class CtDeviceDao extends BaseDao<CtDevice> {
	private static final Logger log = LoggerFactory
			.getLogger(CtDeviceDao.class);
	// property constants
	public static final String SERVER = "server";
	public static final String PORT = "port";
	public static final String SN = "sn";
	public static final String MANUFACTURE_ID = "manufactureId";
	public static final String MODEL_ID = "modelId";
	public static final String OS = "os";
	public static final String IMEI = "imei";
	public static final String USES = "uses";
	public static final String STATE = "state";

	public CtDevice findById(java.lang.Integer id) {
		log.debug("getting CtDevice instance with id: " + id);
		try {
			CtDevice instance = (CtDevice) getHibernateTemplate().get(
					"molab.main.java.entity.CtDevice", id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<CtDevice> findByExample(CtDevice instance) {
		log.debug("finding CtDevice instance by example");
		try {
			List<CtDevice> results = (List<CtDevice>) getHibernateTemplate()
					.findByExample(instance);
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}

	public List<CtDevice> findByProperty(String propertyName, Object value) {
		log.debug("finding CtDevice instance with property: " + propertyName
				+ ", value: " + value);
		try {
			String queryString = "from CtDevice as model where model."
					+ propertyName + "= ?";
			return getHibernateTemplate().find(queryString, value);
		} catch (RuntimeException re) {
			log.error("find by property name failed", re);
			throw re;
		}
	}

	public List<CtDevice> findByServer(Object server) {
		return findByProperty(SERVER, server);
	}

	public List<CtDevice> findByPort(Object port) {
		return findByProperty(PORT, port);
	}

	public List<CtDevice> findBySn(Object sn) {
		return findByProperty(SN, sn);
	}

	public List<CtDevice> findByManufactureId(Object manufactureId) {
		return findByProperty(MANUFACTURE_ID, manufactureId);
	}

	public List<CtDevice> findByModelId(Object modelId) {
		return findByProperty(MODEL_ID, modelId);
	}

	public List<CtDevice> findByOs(Object os) {
		return findByProperty(OS, os);
	}

	public List<CtDevice> findByImei(Object imei) {
		return findByProperty(IMEI, imei);
	}

	public List<CtDevice> findByUses(Object uses) {
		return findByProperty(USES, uses);
	}

	public List<CtDevice> findByState(Object state) {
		return findByProperty(STATE, state);
	}

	public List<CtDevice> findAll() {
		log.debug("finding all CtDevice instances");
		try {
			String queryString = "from CtDevice";
			return getHibernateTemplate().find(queryString);
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}

	public CtDevice merge(CtDevice detachedInstance) {
		log.debug("merging CtDevice instance");
		try {
			CtDevice result = (CtDevice) getHibernateTemplate().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public void attachDirty(CtDevice instance) {
		log.debug("attaching dirty CtDevice instance");
		try {
			getHibernateTemplate().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(CtDevice instance) {
		log.debug("attaching clean CtDevice instance");
		try {
			getHibernateTemplate().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}
	
	public CtDevice findByRunnerId(int runnerId) {
		String hql = "FROM CtDevice WHERE id=(SELECT deviceId FROM CtRunner WHERE id=?)";
		List<CtDevice> list = getHibernateTemplate().find(hql, runnerId);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
}