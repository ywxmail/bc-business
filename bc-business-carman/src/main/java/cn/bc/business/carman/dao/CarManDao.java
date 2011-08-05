/**
 * 
 */
package cn.bc.business.carman.dao;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.dao.CrudDao;

/**
 * 司机责任人Dao
 * 
 * @author dragon
 */
public interface CarManDao extends CrudDao<CarMan> {
	/**
	 * 为司机责任人添加一个证件
	 * 
	 * @param carManId
	 * @param cert
	 * @return
	 */
	
	CarMan saveCert4CarMan(Long carManId, Cert cert);
	/**
	 * 为司机责任人添加一个证件关联
	 * 
	 * @param carManId
	 * @param certId
	 * @return
	 */
	CarMan saveCertRelationship(Long carManId, Long certId);
}