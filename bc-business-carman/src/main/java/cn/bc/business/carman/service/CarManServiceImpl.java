/**
 * 
 */
package cn.bc.business.carman.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.RichEntity;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机责任人Service的实现
 * 
 * @author dragon
 */
public class CarManServiceImpl extends DefaultCrudService<CarMan> implements
		CarManService {
	private CarManDao carManDao;

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
		attrs.put("status", RichEntity.STATUS_DELETED);
		super.update(id, attrs);
	}

	// 将删除方法改为更新方法
	@Override
	public void delete(Serializable[] ids) {
		// TODO Auto-generated method stub
		Map<String, Object> attrs = new HashMap<String, Object>();
		attrs.put("status", RichEntity.STATUS_DELETED);
		super.update(ids, attrs);
	}

	/**
	 * 根据车辆ID查找返回状态为启用中相关司机信息
	 * 
	 * @parma id
	 * @return
	 */

	public List<CarMan> selectAllCarManByCarId(Long id) {
		// TODO Auto-generated method stub
		return (this.carManDao.findAllcarManBycarId(id));

	}

}