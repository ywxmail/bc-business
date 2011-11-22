/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 迁移记录Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarByDriverHistoryDaoImpl extends
		HibernateCrudJpaDao<CarByDriverHistory> implements
		CarByDriverHistoryDao {

}