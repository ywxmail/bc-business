package cn.bc.business.insuranceType.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.insuranceType.dao.InsuranceTypeDao;
import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;

public class InsuranceTypeDaoImpl extends HibernateCrudJpaDao<InsuranceType>
		implements InsuranceTypeDao {

	private static Log logger = LogFactory.getLog(InsuranceTypeDaoImpl.class);
	
	public List<Map<String, String>> findEnabled4Option() {
		String hql ="select i.id,i.name from bs_insurance_type i";
				hql += " where i.status_=0 and type_=1";
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
