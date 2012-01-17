/**
 * 
 */
package cn.bc.business.carman.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.core.service.DefaultCrudService;

/**
 * 迁移记录Service的实现
 * 
 * @author dragon
 */
public class CarByDriverHistoryServiceImpl extends
		DefaultCrudService<CarByDriverHistory> implements
		CarByDriverHistoryService {

	private CarByDriverHistoryDao carByDriverHistoryDao;
	public CarByDriverService carByDriverService;

	@Autowired
	public void setCarByDriverService(CarByDriverService carByDriverService) {
		this.carByDriverService = carByDriverService;
	}

	public CarByDriverHistoryDao getCarByDriverHistoryDao() {
		return carByDriverHistoryDao;
	}

	public void setCarByDriverHistoryDao(
			CarByDriverHistoryDao carByDriverHistoryDao) {
		this.carByDriverHistoryDao = carByDriverHistoryDao;
		this.setCrudDao(carByDriverHistoryDao);
	}

	public void upDateCarByDriver(CarMan carMan, Car car, int newclasses) {
		this.carByDriverHistoryDao.upDateCar4Driver(carMan, car, newclasses);

	}

	public CarByDriverHistory findNewestCarByDriverHistory(Long carManId) {
		return this.carByDriverHistoryDao.findNewestCar(carManId);
	}

	@Override
	public CarByDriverHistory save(CarByDriverHistory entity) {

		// 保存迁移历史记录
		entity = super.save(entity);
		// 加载该司机的营运班次记录
		CarByDriver carByDriver = this.carByDriverService
				.selectCarByDriver4CarManId(entity.getDriver().getId());
		// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作
		if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_XRZ
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_CLDCL
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_YWGSQH) {
			if (carByDriver != null) {
				// 如果存在就更新该司机的营运车辆
				this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver(),
						entity.getToCar(), entity.getToClasses());
			} else {
				// 如果不存在就增加一条记录
				// 生成新的营运班次记录
				CarByDriver newcarByDriver = new CarByDriver();
				newcarByDriver.setCar(entity.getToCar());
				newcarByDriver.setDriver(entity.getDriver());
				newcarByDriver.setClasses(entity.getToClasses());
				newcarByDriver.setAuthor(entity.getAuthor());
				newcarByDriver.setFileDate(entity.getFileDate());
				newcarByDriver.setStatus(BCConstants.STATUS_ENABLED);
				this.carByDriverService.save(newcarByDriver);
			}
		} else {
			// 删除营运车辆记录
			if (carByDriver != null) {
				this.carByDriverService.delete(carByDriver.getId());
			}

		}

		return entity;
	}

}