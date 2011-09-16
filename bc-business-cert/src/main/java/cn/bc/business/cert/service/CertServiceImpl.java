/**
 * 
 */
package cn.bc.business.cert.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 证件Service的实现
 * 
 * @author dragon
 */
public class CertServiceImpl extends DefaultCrudService<Cert> implements
		CertService {
	private CertDao certDao;

	public CertDao getCertDao() {
		return certDao;
	}

	public void setCertDao(CertDao certDao) {
		this.certDao = certDao;
		this.setCrudDao(certDao);
	}
	
	@Override
	public void delete(Serializable id) {
		//删除证件
		this.certDao.delete(id);
	}
	
	@Override
	public void delete(Serializable[] ids) {
		//批量删除证件
		this.certDao.delete(ids);
	}

	/**
	 * 通过carManId查找证件
	 * @parma carManId 
	 * @return
	 */
	public Cert findCertByCarManId(Long carManId) {
		Cert cert = this.certDao.findCertByCarManId(carManId);
		return cert;
	}
	
	/**
	 * 查找司机证件列表
	 * @parma condition 
	 * @return
	 */
	public List<Map<String,Object>> list4man(Condition condition,Long carManId){
		return this.certDao.list4man(condition,carManId);
	}
	
	/**
	 * 查找司机证件分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	public Page<? extends Object> page4man(Condition condition,int pageNo, int pageSize){
		return this.certDao.page4man(condition,pageNo,pageSize);
	}

	/**
	 * 删除单个CarManNCert
	 * @parma certId
	 * @return
	 */
	public void deleteCarManNCert(Long certId) {
		if(certId != null){
			this.certDao.deleteCarManNCert(certId);
		}
	}
	
	/**
	 * 删除批量CarManNCert
	 * @parma certIds 
	 * @return
	 */
	public void deleteCarManNCert(Long[] certIds) {
		if(certIds != null && certIds.length>0){
			this.certDao.deleteCarManNCert(certIds);
		}
	}

	
	/**
	 * 保存证件与司机的关联表信息
	 * @parma carManId 
	 * @parma certId 
	 * @return
	 */
	public void carManNCert4Save(Long carManId, Long certId) {
		this.certDao.carManNCert4Save(carManId,certId);
	}

	/**
	 * 根据certId查找carMan信息
	 * @parma certId 
	 * @return
	 */
	public Map<String, Object> findCarManMessByCertId(Long certId) {
		Map<String, Object> queryMap = null;
		queryMap = this.certDao.findCarManMessByCertId(certId);
		return queryMap;
	}

	/**
	 * 查找汽车证件列表
	 * @parma condition 
	 * @return
	 */
	public List<? extends Object> list4car(Condition condition, Long carId) {
		return this.certDao.list4car(condition,carId);
	}

	/**
	 * 查找汽车证件分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	public Page<? extends Object> page4car(Condition condition, int pageNo,
			int pageSize) {
		return this.certDao.page4car(condition,pageNo,pageSize);
	}

	/**
	 * 删除单个CarNCert
	 * @parma certId
	 * @return
	 */
	public void deleteCarNCert(Long certId) {
		if(certId != null){
			this.certDao.deleteCarNCert(certId);
		}
	}

	/**
	 * 删除批量CarNCert
	 * @parma certIds 
	 * @return
	 */
	public void deleteCarNCert(Long[] certIds) {
		if(certIds != null && certIds.length>0){
			this.certDao.deleteCarNCert(certIds);
		}
	}

	/**
	 * 保存证件与车辆的关联表信息
	 * @parma carId 
	 * @parma certId 
	 * @return
	 */
	public void carNCert4Save(Long carId, Long certId) {
		this.certDao.carNCert4Save(carId,certId);
	}

	/**
	 * 根据certId查找car信息
	 * @parma certId 
	 * @return
	 */
	public Map<String, Object> findCarMessByCertId(Long certId) {
		Map<String, Object> queryMap = null;
		queryMap = this.certDao.findCarMessByCertId(certId);
		return queryMap;
	}

	/**
	 * 根据carId查找carMan详细信息
	 * @parma certId 
	 * @return
	 */
	public Map<String, Object> findCarByCarId(Long carId) {
		Map<String, Object> queryMap = null;
		queryMap = this.certDao.findCarByCarId(carId);
		return queryMap;
	}

	
}