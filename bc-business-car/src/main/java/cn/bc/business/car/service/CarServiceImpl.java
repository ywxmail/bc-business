/**
 * 
 */
package cn.bc.business.car.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.StringUtils;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.event.BeforeSave4CarEvent;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;

/**
 * 车辆Service的实现
 * 
 * @author dragon
 */
public class CarServiceImpl extends DefaultCrudService<Car> implements
		CarService, ApplicationEventPublisherAware {
	protected final Log logger = LogFactory.getLog(getClass());
	private CarDao carDao;
	private OperateLogService operateLogService;
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

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
		// 删除车辆
		this.carDao.delete(id);

		// 记录删除日志
		this.operateLogService.saveWorkLog(Car.class.getSimpleName(),
				String.valueOf(id), "删除车辆", null, OperateLog.OPERATE_DELETE);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 批量删除车辆
		this.carDao.delete(ids);

		// 记录删除日志
		this.operateLogService.saveWorkLog(Car.class.getSimpleName(),
				StringUtils.arrayToCommaDelimitedString(ids), "删除车辆", null,
				OperateLog.OPERATE_DELETE);
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
		boolean isNew = entity.isNew();
		Long oldCarId = entity.getId();
		if (!isNew) {
			// ==重新获取车辆的沉余字段信息[司机信息,责任人信息,所属公司,车队]==

			// 获取司机信息
			String driverInfo = this.carDao
					.getDriverInfoByCarId(entity.getId());
			entity.setDriver(driverInfo);

			// 获取责任人信息
			String chargerInfo = this.carDao.getChargerInfoByCarId(entity
					.getId());
			entity.setCharger(chargerInfo);

			// 发布"保存车辆前"事件
			BeforeSave4CarEvent beforeSave4CarEvent = new BeforeSave4CarEvent(
					entity.getId(), null, null);
			this.eventPublisher.publishEvent(beforeSave4CarEvent);

			// 如果事件返回有车队信息，证明有转车队的迁移记录，故使用该记录的车队和公司信息
			Motorcade m = beforeSave4CarEvent.getMotorcade();
			if (m != null) {
				entity.setCompany(beforeSave4CarEvent.getCompany());
				entity.setMotorcade(m);
			}
		}

		if (isNew) {
			entity = this.carDao.save(entity);
			if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
				// 记录草稿日志

				// 记录新建日志
				this.operateLogService.saveWorkLog(Car.class.getSimpleName(),
						entity.getId().toString(), "新建" + entity.getPlate()
								+ "的车辆信息", null, OperateLog.OPERATE_CREATE);

			} else if (entity.getStatus() == BCConstants.STATUS_ENABLED) {
				// 记录新建日志

				// 记录新建日志
				this.operateLogService.saveWorkLog(Car.class.getSimpleName(),
						entity.getId().toString(), "新建" + entity.getPlate()
								+ "的车辆信息并录入", null, OperateLog.OPERATE_CREATE);

			}

		} else {

			if (oldCarId != null) {
				Car c = this.carDao.load(oldCarId);
				entity = this.carDao.save(entity);
				if (c.getStatus() == BCConstants.STATUS_DRAFT
						&& entity.getStatus() == BCConstants.STATUS_ENABLED) {
					// 记录更新日志
					this.operateLogService.saveWorkLog(Car.class
							.getSimpleName(), entity.getId().toString(), "将车辆"
							+ entity.getPlate() + "入库", null,
							OperateLog.OPERATE_UPDATE);
				} else {
					// 记录更新日志
					this.operateLogService.saveWorkLog(Car.class
							.getSimpleName(), entity.getId().toString(), "更新"
							+ entity.getPlate() + "的车辆信息", null,
							OperateLog.OPERATE_UPDATE);

				}

			}

		}

		return entity;
	}

	public Car findcarOriginNoByOwnership(String ownership, Calendar fileDate) {
		return this.carDao.findcarOriginNoByOwnership(ownership, fileDate);
	}

	/**
	 * 根据状态查找车辆列表
	 * @param status
	 * @return
	 */
	public List<Car> selectCarByStatus(Integer status) {
		return this.carDao.selectCarByStatus(status);
	}
}