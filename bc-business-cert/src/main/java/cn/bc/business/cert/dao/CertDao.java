/**
 * 
 */
package cn.bc.business.cert.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.cert.domain.Cert;
import cn.bc.core.Page;
import cn.bc.core.dao.CrudDao;
import cn.bc.core.query.condition.Condition;


/**
 * 证件Dao
 * 
 * @author dragon
 */
public interface CertDao extends CrudDao<Cert> {

	/**
	 * 通过carManId查找证件
	 * @parma carManId 
	 * @return
	 */
	Cert findCertByCarManId(Long carManId);

	/**
	 * 查找证件列表
	 * @parma condition 
	 * @return
	 */
	List<Map<String, Object>> list4man(Condition condition,Long carManId);
	
	/**
	 * 查找汽车分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	Page<? extends Object> page4man(Condition condition, int pageNo, int pageSize);

	/**
	 * 删除单个CarManNCert
	 * @parma certId 
	 * @return
	 */
	void deleteCarManNCert(Long certId);
	
	/**
	 * 删除批量CarManNCert
	 * @parma certIds 
	 * @return
	 */
	void deleteCarManNCert(Long[] certIds);

	/**
	 * 保存证件与司机的关联表信息
	 * @parma carManId 
	 * @parma certId 
	 * @return
	 */
	void carManNCert4Save(Long carManId, Long certId);
	
	/**
	 * 根据certId查找carMan信息
	 * @parma certId 
	 * @return
	 */
	Map<String, Object> findCarManMessByCertId(Long certId);

	/**
	 * 查找汽车证件列表
	 * @parma condition 
	 * @return
	 */
	List<? extends Object> list4car(Condition condition, Long carId);

	/**
	 * 查找汽车证件分页
	 * @parma condition 
	 * @parma Page 
	 * @return
	 */
	Page<? extends Object> page4car(Condition condition, int pageNo,
			int pageSize);

	/**
	 * 删除单个CarNCert
	 * @parma certId 
	 * @return
	 */
	void deleteCarNCert(Long certId);

	/**
	 * 删除批量CarNCert
	 * @parma certIds 
	 * @return
	 */
	void deleteCarNCert(Long[] certIds);

	/**
	 * 保存证件与车辆的关联表信息
	 * @parma carId 
	 * @parma certId 
	 * @return
	 */
	void carNCert4Save(Long carId, Long certId);

	/**
	 * 根据certId查找car信息
	 * @parma certId 
	 * @return
	 */
	Map<String, Object> findCarMessByCertId(Long certId);

	/**
	 * 根据carId查找carMan详细信息
	 * @parma certId 
	 * @return
	 */
	Map<String, Object> findCarByCarId(Long carId);
	
}