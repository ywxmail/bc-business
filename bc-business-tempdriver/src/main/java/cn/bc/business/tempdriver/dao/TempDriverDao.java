package cn.bc.business.tempdriver.dao;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.dao.CrudDao;

/**
 * 司机招聘Dao
 * 
 * @author lbj
 * 
 */
public interface TempDriverDao extends CrudDao<TempDriver> {
	
	
	/**
	 * 身份证号唯一性检测
	 * 
	 * @param id 招聘司机Id
	 * @param certIdentity 身份证号码
	 * @return
	 */
	boolean isUniqueCertIdentity(Long id,String certIdentity);
	
	/**
	 * 身份证号查对象
	 * 
	 * @param certIdentity 身份证号码
	 * @return
	 */
	TempDriver loadByCertIdentity(String certIdentity);
	
	/**
	 * 查招聘司机表是否已保存此身份证号的心
	 * 
	 * @param certIdentity 身份证号码
	 * @return true 是，false 否
	 */
	boolean isExistCertIdentity(String certIdentity);
}


