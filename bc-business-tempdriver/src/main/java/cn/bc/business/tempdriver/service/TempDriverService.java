package cn.bc.business.tempdriver.service;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.service.CrudService;
import cn.bc.template.service.AddAttachFromTemplateService;

/**
 * 司机招聘Service
 * 
 * @author lbj
 * 
 */
public interface TempDriverService extends CrudService<TempDriver>,AddAttachFromTemplateService {
	
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
	 * 发起流程
	 * @param key 流程key值
	 * @param ids 招聘司机的ID
	 * @return
	 */
	String doStartFlow(String key,Long[] ids);
	
}
