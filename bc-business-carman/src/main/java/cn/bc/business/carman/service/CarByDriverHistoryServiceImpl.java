/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarByDriverDao;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;

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
	private OperateLogService operateLogService;
	public CarManDao carManDao;
	public CarDao carDao;
	public MotorcadeDao motorcadeDao;

	@Autowired
	public void setCarManDao(CarManDao carManDao) {
		this.carManDao = carManDao;
	}

	@Autowired
	public void setMotorcadeDao(MotorcadeDao motorcadeDao) {
		this.motorcadeDao = motorcadeDao;
	}

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

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
		boolean isNew = entity.isNew();
		// 保存迁移历史记录
		entity = super.save(entity);
		// // 记录司机迁移记录的操作日志
		// if (entity.getDriver() != null) {
		// if (isNew) {
		// // 记录新建日志
		// this.operateLogService.saveWorkLog(
		// CarByDriverHistory.class.getSimpleName(),
		// entity.getId().toString(),
		// "新建司机"
		// + entity.getDriver().getName()
		// + "迁移类型为"
		// + this.getMoveType().get(
		// String.valueOf(entity.getMoveType()))
		// + "的迁移记录", null, OperateLog.OPERATE_CREATE);
		// } else {
		// // 记录更新日志
		// this.operateLogService.saveWorkLog(
		// CarByDriverHistory.class.getSimpleName(),
		// entity.getId().toString(),
		// "更新司机"
		// + entity.getDriver().getName()
		// + "迁移类型为"
		// + this.getMoveType().get(
		// String.valueOf(entity.getMoveType()))
		// + "的迁移记录", null, OperateLog.OPERATE_UPDATE);
		// }
		//
		// } else {
		// // 记录车辆迁移记录的操作日志
		// if (isNew) {
		// // 记录新建日志
		// Car c = this.carDao.load(entity.getFromCar().getId());
		// this.operateLogService.saveWorkLog(CarByDriverHistory.class
		// .getSimpleName(), entity.getId().toString(),
		// "新建车辆" + c.getPlate() + "迁移类型为转车队的迁移记录", null,
		// OperateLog.OPERATE_CREATE);
		// } else {
		// // 记录更新日志
		// this.operateLogService.saveWorkLog(CarByDriverHistory.class
		// .getSimpleName(), entity.getId().toString(), "更新车辆"
		// + entity.getFromCar().getPlateType() + "."
		// + entity.getFromCar().getPlateNo() + "移类型为转车队的迁移记录",
		// null, OperateLog.OPERATE_UPDATE);
		// }
		//
		// }
		if (entity.getDriver() != null) {
			CarMan driver = this.carManDao.load(entity.getDriver().getId());
			// // 加载该司机的营运班次记录
			// CarByDriver carByDriver = this.carByDriverService
			// .selectCarByDriver4CarManId(entity.getDriver().getId());

			// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作
			if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_XRZ
					|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_CLDCL
					|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_YWGSQH
					|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHZC) {
				// if (carByDriver != null) {

				// 找到注销前该司机营运记录
				List<CarByDriver> oldCarByDrivers = carByDriverService
						.findCarByDriverInfo4DriverId(entity.getDriver()
								.getId());
				// 如果存在就更新该司机的营运车辆记录状态为注销
				this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver()
						.getId());
				// 如果是顶班转到新入职,车辆到车辆,由外公司迁回时，更新顶班车辆的营运司机信息
				if (oldCarByDrivers != null) {
					for (CarByDriver oldAr : oldCarByDrivers) {
						this.carByDriverHistoryDao.updateDriver4Car(oldAr
								.getCar().getId());
					}
				}
				// // 更新该司机表营运车辆信息，主车辆Id,迁移类型
				// this.carByDriverHistoryDao.updateDriverOperationCar(entity
				// .getDriver().getId(), entity.getToCar().getId(), entity
				// .getMoveType());
				// }
				// 如果不存在就增加一条营运班次记录
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
				this.carByDriverHistoryDao.updateDriver4Car(entity.getToCar()
						.getId());

				// ---更新司机的相关信息----------------------------------------------------------

				updateDriverRelatedInfo(entity, driver, isNew,
						BCConstants.STATUS_ENABLED, null, entity.getToCar()
								.getId());
				// 更新司机相关信息----------------------------------结束----------------------

				// 更新旧车辆的营运司机信息
				if (entity.getFromCar() != null) {
					this.carByDriverHistoryDao.updateDriver4Car(entity
							.getFromCar().getId());
				}

			} else if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_GSDGSYZX
					|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHWZX
					|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZXWYQX) {
				// 找到注销前该司机营运记录
				List<CarByDriver> oldCarByDrivers = carByDriverService
						.findCarByDriverInfo4DriverId(entity.getDriver()
								.getId());

				// 执行交回未注销，注销未有去向，公司到公司操作后，查找是否存在在案的营运班次，如果存在，则将其状态更新为注销
				this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver()
						.getId());
				// 更新该司机注销前车辆的营运司机信息
				if (oldCarByDrivers != null) {
					for (CarByDriver oldAr : oldCarByDrivers) {
						this.carByDriverHistoryDao.updateDriver4Car(oldAr
								.getCar().getId());
					}
				}

				// 更新原车辆的营运司机信息
				this.carByDriverHistoryDao.updateDriver4Car(entity.getFromCar()
						.getId());
				updateDriverRelatedInfo(entity, driver, isNew,
						BCConstants.STATUS_DISABLED, null, null);

				// if (entity.getToCar() != null) {
				// // 更新该司机表营运车辆信息，主车辆Id,迁移类型,迁移日期，顶班会同期结束日期，司状态(注销)，驾驶状态
				// this.carByDriverHistoryDao.updateDriverOperationCar(entity
				// .getDriver().getId(), entity.getToCar().getId(),
				// entity.getMoveType(), entity.getMoveDate(), null,
				// BCConstants.STATUS_DISABLED, entity.getToClasses());
				// } else {
				// // 主车辆Id为空时，营运班次为空
				// this.carByDriverHistoryDao.updateDriverOperationCar(entity
				// .getDriver().getId(), null, entity.getMoveType(),
				// entity.getMoveDate(), null,
				// BCConstants.STATUS_DISABLED, entity.getToClasses());
				//
				// }
				// // 记录司机的更新日志
				// this.operateLogService.saveWorkLog(
				// CarMan.class.getSimpleName(),
				// entity.getId().toString(),
				// "新建"
				// + driver.getName()
				// + "迁移类型为"
				// + this.getMoveType().get(
				// String.valueOf(entity.getMoveType()))
				// + "的迁移记录 而将" + "该司机的状态设为注销和更新相应的基本信息", null,
				// OperateLog.OPERATE_UPDATE);

			}

		}

		// 转车队的迁移记录
		if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZCD) {
			// 更新车辆表的所属公司和所属车队信息
			this.carByDriverHistoryDao.updateCar4UnitAndMotorcade(entity
					.getFromCar().getId(), entity.getToUnit(), entity
					.getToMotorcadeId());
			Car c = this.carDao.load(entity.getFromCar().getId());
			// 记录车辆迁移记录的操作日志
			if (isNew) {
				// 记录新建日志

				this.operateLogService.saveWorkLog(CarByDriverHistory.class
						.getSimpleName(), entity.getId().toString(),
						"新建车辆" + c.getPlate() + "转车队的迁移记录", null,
						OperateLog.OPERATE_CREATE);
			} else {
				// 记录更新日志
				this.operateLogService.saveWorkLog(CarByDriverHistory.class
						.getSimpleName(), entity.getId().toString(), "更新车辆"
						+ entity.getFromCar().getPlateType() + "."
						+ entity.getFromCar().getPlateNo() + "转车队的迁移记录", null,
						OperateLog.OPERATE_UPDATE);
			}

		}

		return entity;
	}

	/**
	 * @param entity
	 * @param driver
	 *            司机
	 * @param isNew
	 *            是否新建
	 * @param status
	 *            更新司机的状态
	 */
	private void updateDriverRelatedInfo(CarByDriverHistory entity,
			CarMan driver, boolean isNew, int status, Calendar endDate,
			Long toCarId) {
		// 新建
		if (isNew) {
			// 如果司机的状态为草稿:不更新司机的状态,只更新相关信息
			if (driver.getStatus() == BCConstants.STATUS_DRAFT) {
				markLog4Create(entity, driver.getStatus(), "新建", endDate,
						toCarId);
			} else {
				markLog4Create(entity, status, "新建", endDate, toCarId);
			}
		} else {
			// 更新
			// 如果司机的状态为草稿:不更新司机的状态,只更新相关信息
			if (driver.getStatus() == BCConstants.STATUS_DRAFT) {
				markLog4Create(entity, driver.getStatus(), "更新", endDate,
						toCarId);
			} else {
				markLog4Create(entity, status, "更新", endDate, toCarId);
			}

		}

	}

	/**
	 * @param entity
	 * @param status
	 *            要更新司机的状态
	 * @param type
	 *            日志类型：新建 |更新
	 * @param endDate
	 *            顶班合同结束日期
	 * 
	 * @param toCarId
	 *            主车辆
	 */
	private void markLog4Create(CarByDriverHistory entity, int status,
			String type, Calendar endDate, Long toCarId) {
		// 更新该司机表营运车辆信息，主车辆Id,迁移类型,迁移日期，顶班会同期结束日期，司状态(正常)，驾驶状态
		this.carByDriverHistoryDao.updateDriverOperationCar(entity.getDriver()
				.getId(), toCarId, entity.getMoveType(), entity.getMoveDate(),
				endDate, status, entity.getToClasses());
		// String content = "将司机" + entity.getDriver().getName();
		// if (status == BCConstants.STATUS_ENABLED) {
		// content = content + "的状态更新为:在案";
		// } else if (status == BCConstants.STATUS_DISABLED) {
		// content = content + "的状态更新为:注销";
		// } else if (status == BCConstants.STATUS_DRAFT) {
		// content = content + "的状态更新为:草稿";
		// }
		// // 如果主营运车辆不为空
		// if (toCarId != null) {
		// Car mainCar = this.carDao.load(toCarId);
		// content = content + "," + "主车辆更新为:" + mainCar.getPlateType() + "."
		// + mainCar.getPlateNo();
		// } else {
		// content = content + "," + "主车辆更新为:空";
		//
		// }
		// // 迁移类型
		// content = content + "," + "迁移类型更新为:"
		// + this.getMoveType().get(String.valueOf(entity.getMoveType()));
		// // 迁移日期
		// if (entity.getMoveDate() != null) {
		// content = content + "," + "迁移日期更新为:"
		// + DateUtils.formatCalendar2Day(entity.getMoveDate());
		// }
		// // 顶班合同结束日期
		// if (endDate != null) {
		// content = content + "," + "顶班合同结束日期:"
		// + DateUtils.formatCalendar2Day(endDate);
		// }
		// // 营运班次
		// content = content
		// + ","
		// + "更新营运班次为:"
		// + this.getDriverClasses().get(
		// String.valueOf(entity.getToClasses()));
		// // 车辆的营运司机

		// 日志
		this.operateLogService
				.saveWorkLog(
						CarByDriverHistory.class.getSimpleName(),
						entity.getId().toString(),
						type
								+ "司机"
								+ entity.getDriver().getName()
								+ this.getMoveType().get(
										String.valueOf(entity.getMoveType()))
								+ "的迁移记录", null, OperateLog.OPERATE_CREATE);
	}

	/**
	 * 保存顶班司机迁移记录
	 * 
	 */
	public void saveShiftwork(CarByDriverHistory entity, Long[] carIds) {
		// 保存迁移记录
		boolean isNew = entity.isNew();
		this.carByDriverHistoryDao.save(entity);

		// 保存迁移历史记录
		entity = super.save(entity);
		// 记录司机迁移记录的操作日志
		// if (entity.getDriver() != null) {
		// if (isNew) {
		// // 记录新建日志
		// this.operateLogService.saveWorkLog(
		// CarByDriverHistory.class.getSimpleName(),
		// entity.getId().toString(),
		// "新建顶班司机"
		// + entity.getDriver().getName()
		// + "迁移类型为"
		// + this.getMoveType().get(
		// String.valueOf(entity.getMoveType()))
		// + "的迁移记录", null, OperateLog.OPERATE_CREATE);
		// } else {
		// // 记录更新日志
		// this.operateLogService.saveWorkLog(
		// CarByDriverHistory.class.getSimpleName(),
		// entity.getId().toString(),
		// "更新顶班司机"
		// + entity.getDriver().getName()
		// + "迁移类型为"
		// + this.getMoveType().get(
		// String.valueOf(entity.getMoveType()))
		// + "的迁移记录", null, OperateLog.OPERATE_UPDATE);
		// }
		//
		// }
		// 保存营运班次信息
		CarMan driver = entity.getDriver();
		List<CarByDriver> carByDrivers = new ArrayList<CarByDriver>();
		CarByDriver carByDriver;
		// 顶班车辆
		Car car;
		// 如果顶班车不为空
		if (carIds != null) {
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

						for (CarByDriver oldAr : toDeleteArs) {
							// 更新已删除的顶班车辆的营运司机信息
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

			// 找到注销前该司机营运记录
			List<CarByDriver> oldCarByDrivers = carByDriverService
					.findCarByDriverInfo4DriverId(entity.getDriver().getId());

			// 更新司机营运班次记录：将不属于该司机迁移记录产生的其他营运记录的状态更新为注销
			this.carByDriverHistoryDao.updateCarByDriverStatus(entity
					.getDriver().getId(), entity.getId());
			// 更新该司机注销前车辆的营运司机
			if (oldCarByDrivers != null) {
				for (CarByDriver oldAr : oldCarByDrivers) {
					this.carByDriverHistoryDao.updateDriver4Car(oldAr.getCar()
							.getId());
				}
			}
			// ---更新司机的相关信息----------------------------------------------------------

			updateDriverRelatedInfo(entity, driver, isNew,
					BCConstants.STATUS_ENABLED, entity.getMoveDate(), entity
							.getToCar().getId());
			// 更新司机相关信息----------------------------------结束----------------------

			// // 更新该司机表营运车辆信息，主车辆Id,迁移类型,迁移日期，顶班会同期结束日期，司状态(正常)，驾驶状态
			// this.carByDriverHistoryDao.updateDriverOperationCar(entity
			// .getDriver().getId(), entity.getToCar().getId(), entity
			// .getMoveType(), entity.getMoveDate(), entity.getEndDate(),
			// BCConstants.STATUS_ENABLED, entity.getToClasses());
		}
	}

	public CarByDriverHistory getNeWsetCarByDriverHistory4CarAndMoveType(
			Long carId, int movety) {

		return this.carByDriverHistoryDao
				.findNeWsetCarByDriverHistory4CarAndMoveType(carId, movety);
	}

	/**
	 * 获取迁移类型值转换列表
	 * 
	 * @return
	 */
	protected Map<String, String> getMoveType() {
		Map<String, String> type = new LinkedHashMap<String, String>();
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_CLDCL), "车辆到车辆");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_GSDGSYZX),
				"公司到公司(已注销)");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZXWYQX), "注销未有去向");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_YWGSQH), "由外公司迁回");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHWZX), "交回未注销");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_XRZ), "新入职");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_ZCD), "转车队");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_DINGBAN), "顶班");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_JHZC), "交回后转车");
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_NULL), "(无)");
		return type;
	}

	// /**
	// * 特殊的营运班次(3，4都显示为顶班)
	// *
	// * @return
	// */
	// private Map<String, String> getDriverClasses() {
	// Map<String, String> type = new LinkedHashMap<String, String>();
	// type.put(String.valueOf(CarByDriver.TYPE_ZHENGBAN), "正班");
	// type.put(String.valueOf(CarByDriver.TYPE_FUBAN), "副班");
	// type.put(String.valueOf(CarByDriver.TYPE_DINGBAN), "顶班");
	// type.put(String.valueOf(CarByDriver.TYPE_ZHUGUA), "顶班");
	// type.put(String.valueOf(CarByDriver.TYPE_WEIDINGYI), "空");
	// return type;
	// }

}