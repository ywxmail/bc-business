/**
 * 
 */
package cn.bc.business.carmodel.dao.hibernate.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.carmodel.dao.CarModelDao;
import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.db.jdbc.RowMapper;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;
import cn.bc.orm.hibernate.jpa.HibernateJpaNativeQuery;


/**
 * 车型配置Dao的hibernate jpa实现
 * 
 * @author wis
 */
public class CarModelDaoImpl extends HibernateCrudJpaDao<CarModel> implements CarModelDao{

	private static Log logger = LogFactory.getLog(CarModelDaoImpl.class);
	
	public List<Map<String, String>> findEnabled4Option() {
		String hql = "select cm.id,cm.factory_model from BS_CAR_MODEL cm";
		hql += " order by cm.order_";
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

	public CarModel findcarModelByFactoryModel(String factoryModel) {
		CarModel carModel = null;
		String hql = "select cm from CarModel cm where cm.factoryModel=?";
		List<?> list = this.getJpaTemplate().find(hql, factoryModel);
		carModel = (CarModel) list.get(0);
		return carModel;
	}
}