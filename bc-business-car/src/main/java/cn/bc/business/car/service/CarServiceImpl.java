/**
 * 
 */
package cn.bc.business.car.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.event.BeforeSave4CarEvent;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车辆Service的实现
 * 
 * @author dragon
 */
public class CarServiceImpl extends DefaultCrudService<Car> implements
		CarService, ApplicationEventPublisherAware {
	protected final Log logger = LogFactory.getLog(getClass());
	private CarDao carDao;
	private ApplicationEventPublisher eventPublisher;

	public void setApplicationEventPublisher(
			ApplicationEventPublisher applicationEventPublisher) {
		this.eventPublisher = applicationEventPublisher;
	}

	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
		this.setCrudDao(carDao);
	}

	@Override
	public void delete(Serializable id) {
		// 删除车量
		this.carDao.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 批量删除车辆
		this.carDao.delete(ids);
	}

	/**
	 * 查找汽车列表
	 * 
	 * @parma condition
	 * @return
	 */
	public List<Map<String, Object>> list(Condition condition) {
		return this.carDao.list(condition);
	}

	/**
	 * 查找汽车分页
	 * 
	 * @parma condition
	 * @parma condition
	 * @return
	 */
	public Page<Map<String, Object>> page(Condition condition, int pageNo,
			int pageSize) {
		return this.carDao.page(condition, pageNo, pageSize);
	}

	/**
	 * 根据司机ID查找返回状态为启用中相关辆信息
	 * 
	 * @parma id
	 * @return
	 */
	public List<Car> selectAllCarByCarManId(Long id) {
		return (this.carDao.findAllcarBycarManId(id));
	}

	/**
	 * 根据车牌号查找车辆id
	 * 
	 * @parma carPlateNo
	 * @return Long
	 */
	public Long findcarIdByCarPlateNo(String carPlateNo) {
		Long carId = 0L;
		if (carPlateNo.length() > 0) {
			carId = this.carDao.findcarIdByCarPlateNo(carPlateNo);
		}
		return carId;
	}

	public Car findcarOriginNoByCode(String code) {
		return this.carDao.findcarOriginNoByCode(code);
	}

	public Map<String, Object> findcarInfoByCarPlateNo2(String carPlateNo) {
		return this.carDao.findcarInfoByCarPlateNo2(carPlateNo);
	}

	public Long checkCodeIsExists(Long excludeId, String code) {
		return this.carDao.checkCodeIsExists(excludeId, code);
	}

	public Long checkPlateIsExists(Long excludeId, String plateType,
			String plateNo) {
		return this.carDao.checkPlateIsExists(excludeId, plateType, plateNo);
	}

	@Override
	public Car save(Car entity) {
		return this.carDao.save(entity);
	}

	public void saveRedundantData(Car entity) {
		if (!entity.isNew()) {
			// 获取迁移类型为转车队迁记录的车队与公司

			// 获取司机信息
			String driverInfo = this.carDao
					.getDriverInfoByCarId(entity.getId());
			entity.setDriver(driverInfo);
			// 获取责任人信息
			String chargerInfo = this.carDao.getChargerInfoByCarId(entity
					.getId());
			entity.setCharger(chargerInfo);

			// 保存车辆前事件
			BeforeSave4CarEvent beforeSave4CarEvent = new BeforeSave4CarEvent(
					entity.getId(), null, null);
			this.eventPublisher.publishEvent(beforeSave4CarEvent);
			entity.setCompany(beforeSave4CarEvent.getCompany());
			entity.setMotorcade(beforeSave4CarEvent.getMotorcade());

		}
	}

}