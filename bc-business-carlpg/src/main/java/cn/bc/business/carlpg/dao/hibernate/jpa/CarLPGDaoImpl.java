/**
 * 
 */
package cn.bc.business.carlpg.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.carlpg.dao.CarLPGDao;
import cn.bc.business.carlpg.domain.CarLPG;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;


/**
 * LPG配置Dao的hibernate jpa实现
 * 
 * @author lbj
 */
public class CarLPGDaoImpl extends HibernateCrudJpaDao<CarLPG> implements CarLPGDao{

	private static Log logger = LogFactory.getLog(CarLPGDaoImpl.class);

	public List<Map<String, String>> findEnabled4Option() {
		String hql = "select clm.id,clm.name_ from BS_CAR_LPGMODEL clm";
		hql += " order by clm.order_";
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

	public CarLPG findcarLPGByLPGModel(String name) {
		CarLPG carlpg = null;
		String hql = "select clm from CarLPG clm where clm.name=?";
		List<?> list = this.getJpaTemplate().find(hql, name);
		carlpg = (CarLPG) list.get(0);
		return carlpg;
	}

}