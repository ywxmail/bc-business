/**
 * 
 */
package cn.bc.business.car.dao.hibernate.jpa;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 车辆Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarDaoImpl extends HibernateCrudJpaDao<Car> implements CarDao{

}