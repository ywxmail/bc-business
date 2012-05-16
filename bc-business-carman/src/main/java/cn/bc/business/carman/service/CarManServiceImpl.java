/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.log.domain.OperateLog;
import cn.bc.log.service.OperateLogService;

/**
 * 司机责任人Service的实现
 * 
 * @author dragon
 */
public class CarManServiceImpl extends DefaultCrudService<CarMan> implements
		CarManService {
	private CarManDao carManDao;
	public CarByDriverHistoryDao carByDriverHistoryDao;
	private OperateLogService operateLogService;

	@Autowired
	public void setOperateLogService(OperateLogService operateLogService) {
		this.operateLogService = operateLogService;
	}

	@Autowired
	public void setCarByDriverHistoryDao(
			CarByDriverHistoryDao carByDriverHistoryDao) {
		this.carByDriverHistoryDao = carByDriverHistoryDao;
	}

	public CarManDao getCarManDao() {
		return carManDao;
	}

	public void setCarManDao(CarManDao carManDao) {
		this.carManDao = carManDao;
		this.setCrudDao(carManDao);
	}

	public CarMan saveCert4CarMan(Long carManId, Cert cert) {
		return this.carManDao.saveCert4CarMan(carManId, cert);
	}

	public CarMan saveCertRelationship(Long carManId, Long certId) {
		return this.carManDao.saveCertRelationship(carManId, certId);
	}

	// 将删除方法改为更新方法
	@Override
	public void delete(Serializable id) {
		CarMan cm = this.load(id);
		// 只能删除草稿状态下的司机
		if (BCConstants.STATUS_DRAFT == cm.getStatus()) {
			// 删除司机
			super.delete(id);
			// 记录操作日志
			this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(),
					String.valueOf(id), "删除草稿状态的司机" + cm.getName(), null,
					OperateLog.OPERATE_DELETE);
		} else {
			// 如果不是草稿状态的司机，抛出异常
			throw new CoreException("coreException");
		}

		// // TODO Auto-generated method stub
		// Map<String, Object> attrs = new HashMap<String, Object>();
		// attrs.put("status", BCConstants.STATUS_DELETED);
		// super.update(id, attrs);
		// // // 记录删除日志(更新司机的状态为注销)
		// this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(),
		// String.valueOf(id), "删除司机", null, OperateLog.OPERATE_DELETE);

	}

	// 将删除方法改为更新方法
	@Override
	public void delete(Serializable[] ids) {
		// // TODO Auto-generated method stub
		// Map<String, Object> attrs = new HashMap<String, Object>();
		// attrs.put("status", BCConstants.STATUS_DELETED);
		// super.update(ids, attrs);
		// // 记录删除日志(更新司机的状态为注销)
		// this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(),
		// StringUtils.arrayToCommaDelimitedString(ids), "删除司机", null,
		// OperateLog.OPERATE_DELETE);

		for (Serializable id : ids) {
			CarMan cm = this.load(id);
			// 只能删除草稿状态下的司机
			if (BCConstants.STATUS_DRAFT == cm.getStatus()) {
				// 删除司机
				super.delete(id);
				// 记录操作日志
				this.operateLogService.saveWorkLog(
						CarMan.class.getSimpleName(), String.valueOf(id),
						"删除草稿状态的司机" + cm.getName(), null,
						OperateLog.OPERATE_DELETE);
			} else {
				// 如果不是草稿状态的司机，抛出异常
				throw new CoreException("coreException");
			}

		}
	}

	/**
	 * 根据车辆ID查找返回状态为启用中相关司机信息
	 * 
	 * @parma id
	 * @return
	 */

	public List<CarMan> selectAllCarManByCarId(Long id) {
		return this.carManDao.findAllcarManBycarId(id);
	}

	public Long checkCert4FWZGIsExists(Long excludeId, String cert4FWZG) {
		return this.carManDao.checkCert4FWZGIsExists(excludeId, cert4FWZG);
	}

	@Override
	public CarMan save(CarMan entity) {
		boolean isNew = entity.isNew();
		Long oldManId = entity.getId();

		// 更新司机的冗余字段
		if (!isNew) {
			// 获取最新迁移记录信息
			CarByDriverHistory h = this.carByDriverHistoryDao
					.findNewestCar(entity.getId());
			// 获取最新营运车辆
			String carInfo = this.carManDao.getNewestCarInfo4Driver(entity
					.getId());
			// 设置冗余字段值
			if (h != null) {
				// 主车辆
				if (h.getToCar() != null) {
					entity.setMainCarId(h.getToCar().getId());
				} else {
					entity.setMainCarId(null);
				}
				// 车辆信息
				entity.setCarInFo(carInfo);
				// 最新营运班次
				entity.setClasses(h.getToClasses());
				// 迁移日期
				entity.setMoveDate(h.getMoveDate());
				// 迁移类型
				entity.setMoveType(h.getMoveType());
				// 顶班合同结束日期
				entity.setShiftworkEndDate(h.getEndDate());
				// 迁移类型为公司到公司，交回未注销，注销未有去向的司机状态为注销
				// 非草稿状态下
				if (entity.getStatus() != BCConstants.STATUS_DRAFT) {
					if (h.getMoveType() == CarByDriverHistory.MOVETYPE_GSDGSYZX
							|| h.getMoveType() == CarByDriverHistory.MOVETYPE_JHWZX
							|| h.getMoveType() == CarByDriverHistory.MOVETYPE_ZXWYQX) {
						entity.setStatus(BCConstants.STATUS_DISABLED);
					} else {
						entity.setStatus(BCConstants.STATUS_ENABLED);
					}
				}
			}
		}

		if (isNew) {
			entity = this.carManDao.save(entity);

			if (entity.getStatus() == BCConstants.STATUS_DRAFT) {
				// 记录草稿日志
				this.operateLogService.saveWorkLog(
						CarMan.class.getSimpleName(),
						entity.getId().toString(), "新建司机" + entity.getName(),
						null, OperateLog.OPERATE_CREATE);

			} else if (entity.getStatus() == BCConstants.STATUS_ENABLED) {
				// 记录新建日志
				this.operateLogService.saveWorkLog(
						CarMan.class.getSimpleName(),
						entity.getId().toString(), "新建司机" + entity.getName()
								+ "并入库", null, OperateLog.OPERATE_CREATE);

			}
		} else {
			if (oldManId != null) {
				CarMan om = this.carManDao.load(oldManId);
				if (om.getStatus() == BCConstants.STATUS_DRAFT
						&& entity.getStatus() == BCConstants.STATUS_ENABLED) {
					this.operateLogService.saveWorkLog(CarMan.class
							.getSimpleName(), entity.getId().toString(), "将司机"
							+ entity.getName() + "入库", null,
							OperateLog.OPERATE_CREATE);
				} else {
					// 记录更新日志
					this.operateLogService.saveWorkLog(CarMan.class
							.getSimpleName(), entity.getId().toString(), "更新"
							+ entity.getName() + "司机的信息", null,
							OperateLog.OPERATE_UPDATE);

				}

			}

		}

		return super.save(entity);
	}

	public void updatePhone(Long carManId, String phone1, String phone2) {
		CarMan old = this.carManDao.load(carManId);
		this.carManDao.updatePhoneBycarManId(carManId, phone1, phone2);
		CarMan cm = this.carManDao.load(carManId);
		String oldphone1 = old.getPhone();
		String oldphone2 = old.getPhone1();
		if ((oldphone1 == null && oldphone2 == null)
				|| (oldphone1.length() == 0 && oldphone2.length() == 0)) {
			// 记录更新电话日志
			this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(), cm
					.getId().toString(), "更新司机" + cm.getName() + "的电话号码,由空改为 "
					+ phone1 + " " + phone2, null, OperateLog.OPERATE_UPDATE);
		} else if ((phone1 == null && phone2 == null)
				|| (phone1.length() == 0 && phone2.length() == 0)) {
			this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(), cm
					.getId().toString(), "更新司机" + cm.getName() + "的电话号码,由"
					+ oldphone1 + " " + oldphone2 + "改为空 ", null,
					OperateLog.OPERATE_UPDATE);

		} else {
			// 记录更新电话日志
			this.operateLogService.saveWorkLog(CarMan.class.getSimpleName(), cm
					.getId().toString(), "更新司机" + cm.getName() + "的电话号码,由"
					+ oldphone1 + " " + oldphone2 + "改为 " + phone1 + " "
					+ phone2, null, OperateLog.OPERATE_UPDATE);

		}

	}

	public String getDriverInfoByDriverId(Long carManId) {

		return this.carManDao.getCarManInfoByCarManId(carManId);
	}
}