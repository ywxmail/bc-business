/**
 * 
 */
package cn.bc.business.carman.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
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
		
		if(entity.getDriver()!=null){
			if(entity.getMoveType()==CarByDriverHistory.MOVETYPE_DINGBAN){
				//处理顶班司机的迁移记录
				
			}else{
				// 加载该司机的营运班次记录
				CarByDriver carByDriver = this.carByDriverService
						.selectCarByDriver4CarManId(entity.getDriver().getId());
				
				// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作
				if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_XRZ
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_CLDCL
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_YWGSQH) {
					if (carByDriver != null) {
						// 如果存在就更新该司机的营运车辆记录状态为注销
						this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver()
								.getId());
					}
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
					// 更新新车辆的营运司机信息
					this.carByDriverHistoryDao.updateDriver4Car(entity.getToCar()
							.getId());
					// 更新旧车辆的营运司机信息
					if (entity.getFromCar() != null) {
						this.carByDriverHistoryDao.updateDriver4Car(entity.getFromCar()
								.getId());
					}

				} else if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_GSDGSYZX
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHWZX
						|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZXWYQX) {
					if (carByDriver != null) {
						// 执行交回未注销，注销未有去向，公司到公司操作后，查找是否存在在案的营运班次，如果存在，则将其状态更新为注销
						this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver()
								.getId());
						// 更新原车辆的营运司机信息
						this.carByDriverHistoryDao.updateDriver4Car(entity.getFromCar()
								.getId());
					}
				}
				
			}
		}

		return entity;
	}

}