/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.Calendar;

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
		String hql = "update CarByDriver c set c.status=1 where c.driver.id = ? and c.status = 0";
		this.executeUpdate(hql, new Object[] { carManId });
	}

	public CarByDriverHistory findNewestCar(final Long carManId) {
		final String hql = "select c from CarByDriverHistory c  where c.driver.id = ? and c.status != -1 order by c.moveDate desc";
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

	public CarByDriverHistory findNewestCarByDriverHistoryByCarId(
			final Long carId) {
		final String hql = "select c from CarByDriverHistory c  where (c.fromCar.id = ? or c.toCar.id = ?) and c.status != -1 order by c.moveDate desc";
		CarByDriverHistory carByDriverHistory = this.getJpaTemplate().execute(
				new JpaCallback<CarByDriverHistory>() {
					public CarByDriverHistory doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createQuery(hql);
						queryObject.setParameter(1, carId);// jpa的索引号从1开始
						queryObject.setParameter(2, carId);
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
		String hql = "update Car c set c.company=?,c.motorcade.id=? where c.id=?";
		this.executeUpdate(hql, new Object[] { unit, motorcadeId, carId });
	}

	public void updateCarByDriverStatus(Long driverId, Long pid) {
		String hql = "update CarByDriver c set c.status=1 where c.driver.id=? and c.pid !=?";
		this.executeUpdate(hql, new Object[] { driverId, pid });

	}

	public void updateDriverOperationCar(Long driverId, Long mainCarId,
			int moveType, Calendar moveDate, Calendar endDate, int status,
			int classes) {
		// 如果迁移类型为顶班时的处理
		if (moveType == CarByDriverHistory.MOVETYPE_DINGBAN) {
			String hql = "update CarMan c set c.carInFo=getCarInfoByDriverId(c.id),c.mainCarId=?,c.moveType=?,c.moveDate=?,c.shiftworkEndDate=?,c.status=?,c.classes=? where c.id=?";
			this.executeUpdate(hql, new Object[] { mainCarId, moveType,
					moveDate, endDate, status, classes, driverId });
		} else {
			// 其他类型的处理
			if (mainCarId == null) {
				String hql = "update CarMan c set c.carInFo=getCarInfoByDriverId(c.id),c.mainCarId=null,c.shiftworkEndDate=null,c.classes=0,c.moveType=?,c.moveDate=?,c.status=? where c.id=?";
				this.executeUpdate(hql, new Object[] { moveType, moveDate,
						status, driverId });
			} else {
				String hql = "update CarMan c set c.carInFo=getCarInfoByDriverId(c.id),c.shiftworkEndDate=null,c.mainCarId=?,c.moveType=?,c.moveDate=?,c.status=?,c.classes=? where c.id=?";
				this.executeUpdate(hql, new Object[] { mainCarId, moveType,
						moveDate, status, classes, driverId });
			}
		}

	}

	public CarByDriverHistory findNeWsetCarByDriverHistory4CarAndMoveType(
			final Long carId, final int movety) {
		final String hql = "select h from CarByDriverHistory h where h.fromCar.id = ? and h.moveType = ? order by h.moveDate desc";
		CarByDriverHistory carByDriverHistory = this.getJpaTemplate().execute(
				new JpaCallback<CarByDriverHistory>() {
					public CarByDriverHistory doInJpa(EntityManager em)
							throws PersistenceException {
						Query queryObject = em.createQuery(hql);
						queryObject.setParameter(1, carId);
						queryObject.setParameter(2, movety);
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