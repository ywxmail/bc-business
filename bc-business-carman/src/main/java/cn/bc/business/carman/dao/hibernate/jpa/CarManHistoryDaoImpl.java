/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import cn.bc.business.carman.dao.CarManHistoryDao;
import cn.bc.business.carman.domain.CarManHistory;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 司机迁移历史Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarManHistoryDaoImpl extends HibernateCrudJpaDao<CarManHistory> implements CarManHistoryDao{

}