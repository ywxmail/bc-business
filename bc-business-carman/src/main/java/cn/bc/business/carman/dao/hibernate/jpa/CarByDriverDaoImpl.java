/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机营运车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarByDriverDaoImpl extends HibernateCrudJpaDao<CarByDriver>
		implements CarByDriverDao {
	protected final Log logger = LogFactory.getLog(getClass());

	public Car findBycarManId(Long carManId) {
		// TODO Auto-generated method stub
		Car car = null;
		String hql = "select c.car from CarByDriver c where c.driver.id=? and c.classes=?";
		List list = this.getJpaTemplate()
				.find(hql,
						new Object[] { carManId,
								new Integer(CarByDriver.TYPE_ZHENGBAN) });
		if (list.size() == 1) {
			car = (Car) list.get(0);
			return car;
		} else if (list.size() == 0) {
			return null;
		} else {
			car = (Car) list.get(0);

			if (logger.isDebugEnabled()) {
				logger.debug("有两个或两个以上正班的信息，已选择其中一个正班信息显示！");

			}
			return car;
		}

	}

}