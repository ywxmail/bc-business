/**
 * 
 */
package cn.bc.business.policy.dao.hibernate.jpa;

import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.policy.dao.PolicyDao;
import cn.bc.business.policy.domain.Policy;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 车辆保单Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class PolicyDaoImpl extends HibernateCrudJpaDao<Policy> implements
		PolicyDao {

	private static Log logger = LogFactory.getLog(PolicyDaoImpl.class);

	@SuppressWarnings("unchecked")
	public List<Policy> getPolicise(Long carId, Calendar happenTime) {
		String hql = "from Policy  where car.id=? and";
		hql += " ((? >= commerialStartDate and ? <= commerialEndDate) or (? >= greenslipStartDate and ? <= greenslipEndDate))";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return this.getJpaTemplate().find(hql, carId, happenTime, happenTime,
				happenTime, happenTime);
	}

}