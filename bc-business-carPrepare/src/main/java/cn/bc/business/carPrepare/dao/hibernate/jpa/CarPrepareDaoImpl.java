/**
 * 
 */
package cn.bc.business.carPrepare.dao.hibernate.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.carPrepare.dao.CarPrepareDao;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 出车准备Dao的hibernate jpa实现
 * 
 * @author zxr
 */
public class CarPrepareDaoImpl extends HibernateCrudJpaDao<CarPrepare>
		implements CarPrepareDao {
	private static Log logger = LogFactory.getLog(CarPrepareDaoImpl.class);

	// private JdbcTemplate jdbcTemplate;
	//
	//
	// @Autowired
	// public void setDataSource(DataSource dataSource) {
	// this.jdbcTemplate = new JdbcTemplate(dataSource);
	// }

	public CarPrepare getCarPrepareByPlateTypeAndPlateNo(String plateType,
			String plateNo) {
		CarPrepare carPrepare = null;
		String hql = "select c from CarPrepare c where c.c1PlateType = ? and c.c1PlateNo = ? ";
		hql += " order by c.planDate Desc";
		if (logger.isDebugEnabled()) {
			logger.debug("hql:" + hql);
			logger.debug("plateType: " + plateType + " plateNo:" + plateNo);
		}

		List<?> list = this.getJpaTemplate().find(hql, plateType, plateNo);
		if (list.size() == 1) {
			carPrepare = (CarPrepare) list.get(0);
			return carPrepare;
		} else if (list.size() < 1) {
			return null;
		} else {
			carPrepare = (CarPrepare) list.get(0);
			if (logger.isDebugEnabled()) {
				logger.debug("有两条或两条以上关于" + plateType + "." + plateNo
						+ "的车辆计划信息，只返回预计交车日最新的一条！");
			}
			return carPrepare;
		}
	}
}