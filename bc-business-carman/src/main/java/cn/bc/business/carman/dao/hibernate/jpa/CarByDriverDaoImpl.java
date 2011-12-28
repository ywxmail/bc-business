/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.contract.domain.ContractCarRelation;
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
		Car car = null;
		String hql = "select c.car from CarByDriver c where c.driver.id=? and c.classes=?";
		List<?> list = this.getJpaTemplate()
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

	@Override
	public CarByDriver save(CarByDriver entity) {
		// 保存信息
		entity = super.save(entity);

		// 这句很关键，保证信息先保存，然后再执行下面的更新
		// 如果不执行这句，实测结果是hibernate先执行了下面的更新语句再执行上面的保存语句而导致司机信息没有更新，比较奇怪
		this.getJpaTemplate().flush();

		// 更新车辆的司机信息
		updateCar4Driver(entity.getCar().getId());
		
		//
		this.insertTest();
		return entity;
	}

	//测试使用hibernate的hql语句向数据库插入数据
	private void insertTest() {
//		this.getJpaTemplate().persist(new ContractCarRelation(new Long(139440),new Long(108719)));
//		String hql = "from ContractCarRelation where contractId=139440";
//		List list = this.getJpaTemplate().find(hql);
//		System.out.println(list.toString());
		
//		String hql = "insert into Example (name, code) select 'name1','code1' from Dual";
//		this.executeUpdate(hql,null);
	}

	/**
	 * 更新车辆模块的司机信息
	 * 
	 * @param carId
	 */
	public void updateCar4Driver(Long carId) {
		// 只适用于对当前在案车辆的处理:getDriverInfoByCarId是自定义的存储函数
		// 参考：http://qun.qq.com/air/#6577377/bbs/view/cd/1/td/8
		// 所更新司机字段的格式为：张三(正班),李四(副班),小明(顶班)(先按营运班次正序排序再按司机的入职时间正序排序)

		String hql = "update Car car set car.driver = getDriverInfoByCarId(id) where car.id=?";
		List<Object> args = new ArrayList<Object>();
		args.add(carId);
		if (logger.isDebugEnabled()) {
			logger.debug("hql=" + hql);
			logger.debug("carId=" + carId);
		}
		this.executeUpdate(hql, args);
	}
}