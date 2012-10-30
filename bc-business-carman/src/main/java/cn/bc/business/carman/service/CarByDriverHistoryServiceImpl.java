/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.ArrayList;
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
import cn.bc.core.exception.CoreException;
import cn.bc.core.exception.PermissionDeniedException;
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

	public CarByDriverHistory findNewestCarByDriverHistoryByCarId(Long carId) {
		return this.carByDriverHistoryDao
				.findNewestCarByDriverHistoryByCarId(carId);
	}

	public void upDateDriver4Car(Long carId) {
		this.carByDriverHistoryDao.updateDriver4Car(carId);

	}

	@Override
	public void delete(Serializable id) {
		CarByDriverHistory cbh = this.load(id);
		// CarMan cm = cbh.getDriver();
		// 查找出相关联的营运班次记录
		List<CarByDriver> carByDriver4Pid = this.carByDriverDao
				.findCarByDriverInfoByPid((Long) id);
		// 如果营运班次记录不为空，先删除营运班次记录
		if (carByDriver4Pid != null) {
			for (CarByDriver cbd : carByDriver4Pid) {
				this.carByDriverDao.delete(cbd.getId());
			}
		}
		if (cbh != null) {
			if (cbh.getStatus() == BCConstants.STATUS_DRAFT) {
				super.delete(id);
			} else {
				// 抛出不能删除的异常
				throw new PermissionDeniedException();
			}
		} else {
			// 抛出不能删除的异常
			throw new PermissionDeniedException();
		}

	}

	@Override
	public void delete(Serializable[] ids) {
		for (Serializable id : ids) {
			this.delete(id);
		}
	}

	@Override
	public CarByDriverHistory save(CarByDriverHistory entity) {
		boolean isNew = entity.isNew();
		boolean isNewest = true;
		// 旧迁移记录的状态
		int oldStatus = 0;
		if (!isNew) {
			CarByDriverHistory oldCarByDriverHistory = this.carByDriverHistoryDao
					.load(entity.getId());
			oldStatus = oldCarByDriverHistory.getStatus();
		}

		// 如果车辆最新的迁移记录的类型为转车队时可以进行编辑，历史的不能进行编辑
		if (entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZCD) {
			// 入库的操作的不作检测
			if (!(oldStatus == BCConstants.STATUS_DRAFT && entity.getStatus() == BCConstants.STATUS_ENABLED)
					|| entity.getStatus() == BCConstants.STATUS_DRAFT) {
				if (!entity.getId().equals(
						this.findNewestCarByDriverHistoryByCarId(
								entity.getFromCar().getId()).getId())) {
					if (!isNew) {
						throw new CoreException("这里不处理转车队类型迁移记录的历史信息修改！");
					}
					// 断言【以属于抛异常的一钟】
					// Assert.isTrue(
					// !(entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZCD
					// &&
					// !isNew),
					// "这里不处理转车队类型迁移记录的历史信息修改！");
				}
			}

		}

		// 判断该迁移记录是否最新的迁移记录[排除历史的转车队迁移记录]
		if (entity.getMoveType() != CarByDriverHistory.MOVETYPE_ZCD
				&& isNew == false) {
			// 编辑草稿的迁移记录标记为不是最新
			if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
				isNewest = false;
			} else {
				// 入库的操作的不作检测
				if (!(oldStatus == BCConstants.STATUS_DRAFT && entity
						.getStatus() == BCConstants.STATUS_ENABLED)) {

					if (!entity.getId().equals(
							this.findNewestCarByDriverHistory(
									entity.getDriver().getId()).getId())) {
						// 如果编辑的迁移记录不是最新的标记
						isNewest = false;
					}
				}
			}
		}
		// 如果修改不是最新的迁移记录就不更新任何数据
		if (!isNewest) {
			// 保存迁移历史记录
			entity = super.save(entity);

		} else {
			// int oldStatus = 0;
			// if (!isNew) {
			// // 获取非编辑前的迁移记录
			// CarByDriverHistory oldCarByDriverHistory = this.load(entity
			// .getId());
			// oldStatus = oldCarByDriverHistory.getStatus();
			//
			// }
			// 保存迁移历史记录
			entity = super.save(entity);
			// 转车队的迁移记录[不存在草稿的概念]
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
							.getSimpleName(), entity.getId().toString(), "新建车辆"
							+ c.getPlate() + "转车队的迁移记录", null,
							OperateLog.OPERATE_CREATE);
				} else {
					// 记录更新日志
					this.operateLogService.saveWorkLog(CarByDriverHistory.class
							.getSimpleName(), entity.getId().toString(), "更新车辆"
							+ entity.getFromCar().getPlateType() + "."
							+ entity.getFromCar().getPlateNo() + "转车队的迁移记录",
							null, OperateLog.OPERATE_UPDATE);
				}
			} else {
				// ---------------草稿的处理开始------------------------------------------
				if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
					if (isNew) {
						// 根据不同的迁移类型判断是否生成营运班次记录
						// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作要生成营运班次记录
						if (theMoveTypeOfRegisterDriver(entity)) {
							// 生成草稿的营运班次记录
							createCarByDriver(entity, BCConstants.STATUS_DRAFT);

						}
						// 生成新建草稿状态的操作日志
						makeOperateLog(entity, "新建司机", "草稿状态的迁移记录");
					} else {
						// 根据不同的迁移类型判断是否修改营运班次记录
						if (theMoveTypeOfRegisterDriver(entity)) {
							List<CarByDriver> carByDriverList = this.carByDriverDao
									.findCarByDriverInfoByPid(entity.getId());
							CarByDriver carByDriver = carByDriverList.get(0);
							Car toCar = entity.getToCar();
							// 如果有修改过草稿状态迁移记录的车辆就更新营运记录的车辆信息
							if (!carByDriver.getCar().equals(toCar)) {
								carByDriver.setCar(toCar);
								carByDriver.setModifiedDate(entity
										.getModifiedDate());
							}
							this.carByDriverService.save(carByDriver);
						}
						// 根据状态生成相应更新的操作日志
						makeOperateLog(entity, "更新司机", "草稿状态的迁移记录");
					}
					// ---------------草稿的处理结束------------------------------------------
					// --------------------------------------------------------------------

					// ----------------在案的处理开始--------------------------------------
					// ---------------------------------------------------------------
				} else if (entity.getStatus() == BCConstants.STATUS_ENABLED) {
					// 是否新建--------------------------
					if (isNew) {
						// 根据不同的迁移类型判断是否生成营运班次记录
						// 迁移类型为[新入职,车辆到车辆,由外公司迁回]时行的操作要生成营运班次记录
						if (theMoveTypeOfRegisterDriver(entity)) {
							// 找到注销前该司机在案营运记录
							List<CarByDriver> oldCarByDrivers = carByDriverService
									.findCarByDriverInfo4DriverId(entity
											.getDriver().getId());
							// 如果存在就更新该司机的营运车辆记录状态为注销
							this.carByDriverHistoryDao.upDateCar4Driver(entity
									.getDriver().getId());
							// 如果是顶班转到新入职,车辆到车辆,由外公司迁回时，更新顶班车辆的营运司机信息
							if (oldCarByDrivers != null) {
								for (CarByDriver oldAr : oldCarByDrivers) {
									this.carByDriverHistoryDao
											.updateDriver4Car(oldAr.getCar()
													.getId());
								}
							}
							// 生成正常的营运班次记录
							createCarByDriver(entity,
									BCConstants.STATUS_ENABLED);
							// 更新移往车辆的营运司机信息
							this.carByDriverHistoryDao.updateDriver4Car(entity
									.getToCar().getId());
							// 更新司机的营运车辆
							this.carByDriverHistoryDao
									.updateDriverOperationCar(entity
											.getDriver().getId(), entity
											.getToCar().getId(), entity
											.getMoveType(), entity
											.getMoveDate(), null,
											BCConstants.STATUS_ENABLED, entity
													.getToClasses());
							// 需要注销司机
						} else if (theMoveTypeOfLogoutDriver(entity)) {
							// 注销司机相关的更新
							logoutDriverOperate(entity);

						}
						// 生成新建正常状态的操作日志
						makeOperateLog(entity, "新建并入库司机", "的迁移记录");

						// 编辑状态下--------------------
					} else {
						if (theMoveTypeOfRegisterDriver(entity)) {
							// 找到注销前该司机在案营运记录
							List<CarByDriver> oldCarByDrivers = carByDriverService
									.findCarByDriverInfo4DriverId(entity
											.getDriver().getId());
							// 如果存在就更新该司机的在案的营运车辆记录状态为注销
							if (oldCarByDrivers != null) {
								for (CarByDriver oldAr : oldCarByDrivers) {
									// 如果迁移记录的车辆与在案的营运记录的车辆的不一样时就更新营运班次记录为注销
									if (!oldAr.getCar().equals(
											entity.getToCar())) {
										this.carByDriverHistoryDao
												.upDateCar4Driver(entity
														.getDriver().getId());
									}
								}
							}
							// 如果是顶班转到新入职,车辆到车辆,由外公司迁回时，更新顶班车辆的营运司机信息
							if (oldCarByDrivers != null) {
								for (CarByDriver oldAr : oldCarByDrivers) {
									this.carByDriverHistoryDao
											.updateDriver4Car(oldAr.getCar()
													.getId());
								}
							}
							// 将草稿的营运班次记录更新为在案状态
							List<CarByDriver> carByDriverList = this.carByDriverDao
									.findCarByDriverInfoByPid(entity.getId());
							CarByDriver carByDriver = carByDriverList.get(0);
							carByDriver.setStatus(BCConstants.STATUS_ENABLED);
							carByDriver.setClasses(entity.getToClasses());
							Car toCar = entity.getToCar();
							// 如果有修改过草稿状态迁移记录的车辆就更新营运记录的车辆信息
							if (!carByDriver.getCar().equals(toCar)) {
								carByDriver.setCar(toCar);
								carByDriver.setModifiedDate(entity
										.getModifiedDate());
							}
							// 保存营运记录
							this.carByDriverService.save(carByDriver);
							// 更新移往车辆的营运司机信息
							this.carByDriverHistoryDao.updateDriver4Car(entity
									.getToCar().getId());
							// 更新司机的营运车辆
							this.carByDriverHistoryDao
									.updateDriverOperationCar(entity
											.getDriver().getId(), entity
											.getToCar().getId(), entity
											.getMoveType(), entity
											.getMoveDate(), null,
											BCConstants.STATUS_ENABLED, entity
													.getToClasses());

						} else if (theMoveTypeOfLogoutDriver(entity)) {
							// 注销司机相关的更新
							logoutDriverOperate(entity);

						}
						// 将草稿的迁移记录入库
						if (oldStatus == BCConstants.STATUS_DRAFT
								&& entity.getStatus() == BCConstants.STATUS_ENABLED) {
							// 根据状态生成相应更新的操作日志
							makeOperateLog(entity, "将司机", "的迁移记录入库");
						} else if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
							makeOperateLog(entity, "更新司机", "草稿状态的迁移记录");
							// 在案状态下的操作日志
						} else {
							makeOperateLog(entity, "更新司机", "的迁移记录");
						}

					}

				}
				// ----------------在案处理结束--------------------------------
			}
		}

		return entity;
	}

	/**
	 * 需要将司机变为在案的迁移类型：新入职,车辆到车辆,由外公司迁回,交回后转车
	 * 
	 * @param entity
	 * @return
	 */
	private boolean theMoveTypeOfRegisterDriver(CarByDriverHistory entity) {
		return entity.getMoveType() == CarByDriverHistory.MOVETYPE_XRZ
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_CLDCL
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_YWGSQH
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHZC;
	}

	/**
	 * 需要注销司机的迁移类型：公司到公司(已注销),交回未注销,注销未有去向,未交证注销
	 * 
	 * @param entity
	 * @return
	 */
	private boolean theMoveTypeOfLogoutDriver(CarByDriverHistory entity) {
		return entity.getMoveType() == CarByDriverHistory.MOVETYPE_GSDGSYZX
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_JHWZX
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_ZXWYQX
				|| entity.getMoveType() == CarByDriverHistory.MOVETYPE_WJZZX;
	}

	/**
	 * 注销司机的相关更新
	 * 
	 * @param entity
	 */
	private void logoutDriverOperate(CarByDriverHistory entity) {
		// 找到注销前该司机营运记录
		List<CarByDriver> oldCarByDrivers = carByDriverService
				.findCarByDriverInfo4DriverId(entity.getDriver().getId());

		// 执行交回未注销，注销未有去向，公司到公司操作后，查找是否存在在案的营运班次，如果存在，则将其状态更新为注销
		this.carByDriverHistoryDao.upDateCar4Driver(entity.getDriver().getId());
		// 更新该司机注销前车辆的营运司机信息
		if (oldCarByDrivers != null) {
			for (CarByDriver oldAr : oldCarByDrivers) {
				this.carByDriverHistoryDao.updateDriver4Car(oldAr.getCar()
						.getId());
			}
		}

		// 更新原车辆的营运司机信息
		this.carByDriverHistoryDao
				.updateDriver4Car(entity.getFromCar().getId());

		// 更新司机的营运车辆
		this.carByDriverHistoryDao.updateDriverOperationCar(entity.getDriver()
				.getId(), null, entity.getMoveType(), entity.getMoveDate(),
				null, BCConstants.STATUS_DISABLED, entity.getToClasses());
	}

	/**
	 * 新建营运班次记录
	 * 
	 * @param entity
	 *            迁移记录
	 * @param statusValue
	 *            状态值
	 */
	private void createCarByDriver(CarByDriverHistory entity, int statusValue) {
		CarByDriver newcarByDriver = new CarByDriver();
		newcarByDriver.setCar(entity.getToCar());
		newcarByDriver.setDriver(entity.getDriver());
		newcarByDriver.setClasses(entity.getToClasses());
		newcarByDriver.setAuthor(entity.getAuthor());
		newcarByDriver.setFileDate(entity.getFileDate());
		newcarByDriver.setPid(entity.getId());
		newcarByDriver.setStatus(statusValue);
		this.carByDriverService.save(newcarByDriver);
	}

	/**
	 * 保存顶班司机迁移记录
	 * 
	 */
	public void saveShiftwork(CarByDriverHistory entity, Long[] carIds) {
		// 保存迁移记录
		boolean isNew = entity.isNew();
		boolean isNewest = true;
		// 判断该迁移记录是否最新的迁移记录[排除转车队的]
		if (isNew == false) {
			if (!entity.getId().equals(
					this.findNewestCarByDriverHistory(
							entity.getDriver().getId()).getId())) {
				// 如果编辑的迁移记录不是最新的标记
				isNewest = false;
			}
		}
		// 如果修改不是最新的迁移记录就不更新任何数据
		if (!isNewest) {
			// 保存迁移历史记录
			entity = super.save(entity);
		} else {
			int oldStatus = 0;
			if (!isNew) {
				CarByDriverHistory oldCarByDriverHistory = this.carByDriverHistoryDao
						.load(entity.getId());
				oldStatus = oldCarByDriverHistory.getStatus();
			}
			this.carByDriverHistoryDao.save(entity);
			// 保存迁移历史记录
			entity = super.save(entity);
			// 保存营运班次信息
			CarMan driver = this.carManDao.load(entity.getDriver().getId());
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
					carByDriver.setStatus(entity.getStatus());
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
			zhugaCarByDriver.setStatus(entity.getStatus());
			carByDrivers.add(zhugaCarByDriver);

			// 原来的营运班次
			List<CarByDriver> oldArs = this.carByDriverDao
					.find4Shiftwork(entity.getId());
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
					if (!oldArs.isEmpty()
							&& sameBelongs.size() != oldArs.size()) {
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
								this.carByDriverHistoryDao
										.updateDriver4Car(oldAr.getCar()
												.getId());
							}
						}
					}
				}

			}
			// 创建新的营运班次
			if (!carByDrivers.isEmpty()) {
				// 保存营运班次
				this.carByDriverDao.save(carByDrivers);

				if (entity.getStatus() == BCConstants.STATUS_ENABLED) {
					// 找到注销前该司机营运记录
					List<CarByDriver> oldCarByDrivers = carByDriverService
							.findCarByDriverInfo4DriverId(entity.getDriver()
									.getId());

					// 更新司机营运班次记录：将不属于该司机迁移记录产生的其他营运记录的状态更新为注销
					this.carByDriverHistoryDao.updateCarByDriverStatus(entity
							.getDriver().getId(), entity.getId());
					// 更新该司机注销前车辆的营运司机
					if (oldCarByDrivers != null) {
						for (CarByDriver oldAr : oldCarByDrivers) {
							this.carByDriverHistoryDao.updateDriver4Car(oldAr
									.getCar().getId());
						}
					}
					// ---更新司机的相关信息----------------------------------------------------------
					// 更新该司机表营运车辆信息，主车辆Id,迁移类型,迁移日期，顶班会同期结束日期，司状态(正常)，驾驶状态
					this.carByDriverHistoryDao.updateDriverOperationCar(entity
							.getDriver().getId(), entity.getToCar().getId(),
							entity.getMoveType(), entity.getMoveDate(), entity
									.getEndDate(), BCConstants.STATUS_ENABLED,
							entity.getToClasses());
				}
				// 操作日志
				if (isNew) {
					// 迁移记录为在案的操作
					if (entity.getStatus() == BCConstants.STATUS_ENABLED) {
						makeOperateLog(entity, "新建并入库司机", "的迁移记录");
					} else {
						makeOperateLog(entity, "新建司机", "草稿状态的迁移记录");
					}

				} else {
					// 入库操作的日志
					if (oldStatus == BCConstants.STATUS_DRAFT
							&& entity.getStatus() == BCConstants.STATUS_ENABLED) {
						makeOperateLog(entity, "将司机", "草稿状态的迁移记录入库");
						// 草稿状态下的操作日志
					} else if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
						makeOperateLog(entity, "更新司机", "草稿状态的迁移记录");
						// 在案状态下的操作日志
					} else {
						makeOperateLog(entity, "更新司机", "的迁移记录");
					}
				}
				// 更新司机相关信息----------------------------------结束----------------------

				// // 更新该司机表营运车辆信息，主车辆Id,迁移类型,迁移日期，顶班会同期结束日期，司状态(正常)，驾驶状态
				// this.carByDriverHistoryDao.updateDriverOperationCar(entity
				// .getDriver().getId(), entity.getToCar().getId(), entity
				// .getMoveType(), entity.getMoveDate(), entity.getEndDate(),
				// BCConstants.STATUS_ENABLED, entity.getToClasses());
			}
		}
	}

	/**
	 * 生成操作日志
	 * 
	 * @param entity
	 * @param srt1
	 *            更新或新建或将
	 * @param str2
	 *            的迁移记录或草稿状态的迁移记录或草稿状态的迁移记录入库
	 */
	private void makeOperateLog(CarByDriverHistory entity, String str1,
			String str2) {
		this.operateLogService.saveWorkLog(
				CarByDriverHistory.class.getSimpleName(),
				entity.getId().toString(),
				str1
						+ entity.getDriver().getName()
						+ this.getMoveType().get(
								String.valueOf(entity.getMoveType())) + str2,
				null, OperateLog.OPERATE_CREATE);
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
		type.put(String.valueOf(CarByDriverHistory.MOVETYPE_WJZZX), "未交证注销");
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