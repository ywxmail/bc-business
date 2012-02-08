/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.orm.jpa.JpaCallback;

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

	public void upDateCar4Driver(Long carManId) {
		// 更新该司机之前的营运车辆记录的状态为注销状态
		String hql = "update CarByDriver c set c.status=1 where c.driver.id = ?";
		this.executeUpdate(hql, new Object[] { carManId });
	}

	public CarByDriverHistory findNewestCar(final Long carManId) {
		final String hql = "select c from CarByDriverHistory c  where c.driver.id = ? order by c.moveDate desc";
		CarByDriverHistory carByDriverHistory = this.getJpaTemplate().execute(
				new JpaCallback<CarByDriverHistory>() {
					public CarByDriverHistory doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createQuery(hql);
						queryObject.setParameter(1, carManId);// jpa的索引号从1开始
						getJpaTemplate().prepareQuery(queryObject);
						queryObject.setFirstResult(0);
						queryObject.setMaxResults(1);
						try {
							return (CarByDriverHistory) queryObject
									.getSingleResult();
						} catch (NoResultException e) {
							return null;
						}
					}
				});
		return carByDriverHistory;
	}

	public void updateDriver4Car(Long carId) {
		String hql = "update Car c set c.driver=getDriverInfoByCarId(c.id) where c.id=?";
		this.executeUpdate(hql, new Object[] { carId });
	}

	public void updateCar4UnitAndMotorcade(Long carId, String unit,
			Long motorcadeId) {
		String hql = "update Car c set c.oldUnitName=?,c.motorcade.id=? where c.id=?";
		this.executeUpdate(hql, new Object[] { unit, motorcadeId, carId });
	}

}