/**
 * 
 */
package cn.bc.business.blacklist.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.blacklist.dao.BlacklistDao;
import cn.bc.business.blacklist.domain.Blacklist;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;

/**
 * 黑名单Service的实现
 * 
 * @author dragon
 */
public class BlacklistServiceImpl extends DefaultCrudService<Blacklist>
		implements BlacklistService {
	private BlacklistDao blacklistDao;
	private OperateLogService operateLogService;
	public CarDao carDao;

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

	public BlacklistDao getBlacklistDao() {
		return blacklistDao;
	}

	public void setBlacklistDao(BlacklistDao blacklistDao) {
		this.blacklistDao = blacklistDao;
		this.setCrudDao(blacklistDao);
	}

	@Override
	public void delete(Serializable id) {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 循环删除
		for (Serializable id : ids) {
			super.delete(id);
		}

	}

	@Override
	public Blacklist save(Blacklist entity) {
		boolean isNew = entity.isNew();
		entity = super.save(entity);
		if (isNew) {
			Car c = this.carDao.load(entity.getCar().getId());
			// 记录新建日志
			this.operateLogService.saveWorkLog(Blacklist.class.getSimpleName(),
					entity.getId().toString(), "锁定车辆" + c.getPlate() + "的黑名单",
					null, OperateLog.OPERATE_CREATE);

		} else {

			// 如果执行解锁操作就生成一条解锁日志
			if (entity.getStatus() == Blacklist.STATUS_UNLOCK) {
				this.operateLogService.saveWorkLog(Blacklist.class
						.getSimpleName(), entity.getId().toString(), "解锁车辆"
						+ entity.getCar().getPlateType() + "."
						+ entity.getCar().getPlateNo() + "的黑名单", null,
						OperateLog.OPERATE_UPDATE);
			} else {
				// 记录更新日志
				this.operateLogService.saveWorkLog(Blacklist.class
						.getSimpleName(), entity.getId().toString(), "更新车辆"
						+ entity.getCar().getPlateType() + "."
						+ entity.getCar().getPlateNo() + "的黑名单", null,
						OperateLog.OPERATE_UPDATE);

			}

		}
		return entity;
	}

}