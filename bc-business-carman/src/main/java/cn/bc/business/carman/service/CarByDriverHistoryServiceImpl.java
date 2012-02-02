/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
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
	public CarByDriverDao carByDriverDao;

	@Autowired
	public void setCarByDriverDao(CarByDriverDao carByDriverDao) {
		this.carByDriverDao = carByDriverDao;
	}

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

	public void upDateCarByDriver(Long carManId) {
		this.carByDriverHistoryDao.upDateCar4Driver(carManId);

	}

	public CarByDriverHistory findNewestCarByDriverHistory(Long carManId) {
		return this.carByDriverHistoryDao.findNewestCar(carManId);
	}

	public void upDateDriver4Car(Long carId) {
		this.carByDriverHistoryDao.updateDriver4Car(carId);

	}

	@Override
	public CarByDriverHistory save(CarByDriverHistory entity) {

		// 保存迁移历史记录
		entity = super.save(entity);

		if (entity.getDriver() != null) {
			if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_DINGBAN) {
				// 处理顶班司机的迁移记录

			} else {
				// 加载该司机的营运班次记录
				CarByDriver carByDriver = this.carByDriverService
						.selectCarByDriver4CarManId(entity.getDriver().getId());

				// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作
				if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_XRZ
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_CLDCL
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_YWGSQH) {
					if (carByDriver != null) {
						// 如果存在就更新该司机的营运车辆记录状态为注销
						this.carByDriverHistoryDao.upDateCar4Driver(entity
								.getDriver().getId());
					}
					// 如果不存在就增加一条记录
					// 生成新的营运班次记录
					CarByDriver newcarByDriver = new CarByDriver();
					newcarByDriver.setCar(entity.getToCar());
					newcarByDriver.setDriver(entity.getDriver());
					newcarByDriver.setClasses(entity.getToClasses());
					newcarByDriver.setAuthor(entity.getAuthor());
					newcarByDriver.setFileDate(entity.getFileDate());
					newcarByDriver.setPid(entity.getId());
					newcarByDriver.setStatus(BCConstants.STATUS_ENABLED);
					this.carByDriverService.save(newcarByDriver);
					// 更新新车辆的营运司机信息
					this.carByDriverHistoryDao.updateDriver4Car(entity
							.getToCar().getId());
					// 更新旧车辆的营运司机信息
					if (entity.getFromCar() != null) {
						this.carByDriverHistoryDao.updateDriver4Car(entity
								.getFromCar().getId());
					}

				} else if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_GSDGSYZX
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHWZX
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZXWYQX) {
					if (carByDriver != null) {
						// 执行交回未注销，注销未有去向，公司到公司操作后，查找是否存在在案的营运班次，如果存在，则将其状态更新为注销
						this.carByDriverHistoryDao.upDateCar4Driver(entity
								.getDriver().getId());
						// 更新原车辆的营运司机信息
						this.carByDriverHistoryDao.updateDriver4Car(entity
								.getFromCar().getId());
					}
				}

			}
		}

		return entity;
	}

	/**
	 * 保存顶班司机迁移记录
	 * 
	 */
	public void saveShiftwork(CarByDriverHistory entity, Long[] carIds) {
		// 保存迁移记录
		this.carByDriverHistoryDao.save(entity);

		// 保存营运班次信息
		CarMan driver = entity.getDriver();
		List<CarByDriver> carByDrivers = new ArrayList<CarByDriver>();
		CarByDriver carByDriver;
		// 顶班车辆
		Car car;
		for (Long carId : carIds) {
			carByDriver = new CarByDriver();
			car = new Car();
			car.setId(carId);
			carByDriver.setDriver(driver);
			carByDriver.setCar(car);
			carByDriver.setClasses(CarByDriver.TYPE_DINGBAN);
			carByDriver.setAuthor(entity.getAuthor());
			carByDriver.setFileDate(entity.getFileDate());
			carByDriver.setModifier(entity.getModifier());
			carByDriver.setModifiedDate(entity.getModifiedDate());
			carByDriver.setDescription(entity.getDescription());
			carByDriver.setPid(entity.getId());
			carByDrivers.add(carByDriver);
		}
		// 主挂车辆
		CarByDriver zhugaCarByDriver = new CarByDriver();
		zhugaCarByDriver.setDriver(driver);
		zhugaCarByDriver.setCar(entity.getToCar());
		zhugaCarByDriver.setClasses(CarByDriver.TYPE_ZHUGUA);
		zhugaCarByDriver.setAuthor(entity.getAuthor());
		zhugaCarByDriver.setFileDate(entity.getFileDate());
		zhugaCarByDriver.setModifier(entity.getModifier());
		zhugaCarByDriver.setModifiedDate(entity.getModifiedDate());
		zhugaCarByDriver.setDescription(entity.getDescription());
		zhugaCarByDriver.setPid(entity.getId());
		carByDrivers.add(zhugaCarByDriver);

		// 原来的营运班次
		List<CarByDriver> oldArs = this.carByDriverDao.find4Shiftwork(entity
				.getId());
		// 如果原来的营运班次不为空，不是新建时
		if (oldArs != null) {
			// 对碰找出新加的、要删除的
			if (carByDrivers != null && carByDrivers.size() > 0) {
				List<CarByDriver> sameBelongs = new ArrayList<CarByDriver>();// 没有改变的belong
				List<CarByDriver> newBelongs = new ArrayList<CarByDriver>();// 新加的belong

				// 对碰找出新加的、相同的
				boolean same;
				CarByDriver old = null;
				for (int i = 0; i < carByDrivers.size(); i++) {
					carByDriver = carByDrivers.get(i);
					same = false;
					for (CarByDriver oldAr : oldArs) {
						if (oldAr.getCar().getId()
								.equals(carByDriver.getCar().getId())) {
							same = true;
							old = oldAr;
							break;
						}
					}
					if (same) {
						sameBelongs.add(carByDriver);
						// 复制信息
						carByDrivers.remove(carByDriver);
						old.setModifier(carByDriver.getModifier());
						old.setModifiedDate(carByDriver.getModifiedDate());
						old.setDescription(carByDriver.getDescription());
						old.setStatus(carByDriver.getStatus());
						old.setPid(carByDriver.getPid());
						old.setClasses(carByDriver.getClasses());
						carByDrivers.add(i, old);
					} else {
						newBelongs.add(carByDriver);

					}
				}

				// 删除不再存在的隶属关系
				if (!oldArs.isEmpty() && sameBelongs.size() != oldArs.size()) {
					List<CarByDriver> toDeleteArs = new ArrayList<CarByDriver>();
					for (CarByDriver oldAr : oldArs) {
						same = false;
						for (CarByDriver belong : sameBelongs) {
							if (oldAr.getCar().getId()
									.equals(belong.getCar().getId())) {
								same = true;
								break;
							}
						}
						if (!same) {
							toDeleteArs.add(oldAr);
						}
					}
					if (!toDeleteArs.isEmpty()) {
						// 删除
						List<Long> toDeleteIds = new ArrayList<Long>();
						for (CarByDriver oldAr : toDeleteArs) {
							toDeleteIds.add(oldAr.getId());
							carByDrivers.remove(oldAr);
						}
						this.carByDriverDao.delete(toDeleteIds
								.toArray(new Serializable[0]));
						// 更新已删除的顶班车辆的营运司机信息
						for (CarByDriver oldAr : toDeleteArs) {
							this.carByDriverHistoryDao.updateDriver4Car(oldAr
									.getCar().getId());
						}
					}
				}
			}

		}
		// 创建新的营运班次
		if (!carByDrivers.isEmpty()) {
			// 保存营运班次
			this.carByDriverDao.save(carByDrivers);
		}
	}
}