/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.springframework.orm.jpa.JpaCallback;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 迁移记录Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarByDriverHistoryDaoImpl extends
		HibernateCrudJpaDao<CarByDriverHistory> implements
		CarByDriverHistoryDao {

	public void upDateCar4Driver(CarMan carMan, Car car, int classes) {
		String hql = "update CarByDriver c set c.car.id=?,c.classes=? where c.driver.id = ?";
		this.executeUpdate(hql,
				new Object[] { car.getId(), classes, carMan.getId() });
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
}