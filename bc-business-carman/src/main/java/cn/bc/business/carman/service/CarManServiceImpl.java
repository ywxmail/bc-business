/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.carman.dao.CarByDriverHistoryDao;
import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机责任人Service的实现
 * 
 * @author dragon
 */
public class CarManServiceImpl extends DefaultCrudService<CarMan> implements
		CarManService {
	private CarManDao carManDao;
	public CarByDriverHistoryDao carByDriverHistoryDao;

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
		// TODO Auto-generated method stub
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", BCConstants.STATUS_DELETED);
		super.update(id, attrs);
	}

	// 将删除方法改为更新方法
	@Override
	public void delete(Serializable[] ids) {
		// TODO Auto-generated method stub
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", BCConstants.STATUS_DELETED);
		super.update(ids, attrs);
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

	public void setShiftworkInfo(CarMan entity) {
		if (!entity.isNew()) {
			CarByDriverHistory h = this.carByDriverHistoryDao
					.findNewestCar(entity.getId());
			if (h != null) {
				if (h.getToCar() != null) {
					entity.setMainCarId(h.getToCar().getId());
				} else {
					entity.setMainCarId(null);
				}
				String carInfo = this.carManDao.getNewestCarInfo4Driver(entity
						.getId());
				entity.setCarInFo(carInfo);
				entity.setClasses(h.getToClasses());
				entity.setMoveDate(h.getMoveDate());
				entity.setMoveType(h.getMoveType());
				entity.setShiftworkEndDate(h.getEndDate());
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
}