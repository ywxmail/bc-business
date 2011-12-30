package cn.bc.business.motorcade.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.BCConstants;
import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.AndCondition;
import cn.bc.core.query.condition.impl.EqualsCondition;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

public class MotorcadeDaoImpl extends HibernateCrudJpaDao<Motorcade> implements
		MotorcadeDao {
	private static Log logger = LogFactory.getLog(MotorcadeDaoImpl.class);

	public List<Motorcade> findActive() {
		return this
				.createQuery()
				.condition(
						new AndCondition().add(
								new EqualsCondition("status",
										BCConstants.STATUS_ENABLED)).add(
								new OrderCondition("code", Direction.Asc)))
				.list();
	}

	public List<Map<String, String>> find4Option() {
		String hql = "select m.id,m.name from BS_MOTORCADE m where m.status_ = 0 order by m.code";
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
		}
		return HibernateJpaNativeQuery.executeNativeSql(getJpaTemplate(), hql,
				null, new RowMapper<Map<String, String>>() {
					public Map<String, String> mapRow(Object[] rs, int rowNum) {
						Map<String, String> oi = new HashMap<String, String>();
						int i = 0;
						oi.put("key", rs[i++].toString());
						oi.put("value", rs[i++].toString());
						return oi;
					}
				});
	}
}
