package cn.bc.business.tempdriver.service;

import java.io.IOException;
import java.util.Calendar;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.service.CrudService;
import cn.bc.docs.domain.Attach;
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
	 * @param flagStatus 状态值修改控制
	 * @return
	 */
	String doStartFlow(String key,Long[] ids,boolean flagStatus);
	
	
	/**
	 * 同步司机身份证照片的方法
	 */
	void doSyncPortrait() throws Exception;
	
	/**
	 * 批量更新状态
	 * @param ids
	 * @param status
	 */
	void doUpdateStatus(Long[] ids,int status);
	
	/**
	 * 批量更新面试日期
	 * @param ids
	 * @param status
	 */
	void doUpdateInterviewDate(Long[] ids,Calendar interviewDate);
	
	/**
	 * 发起流程
	 * @param listDriver 多个司机的JSON字符串信息
	 * @param key 流程编码
	 * @return 流程id
	 */
	String doStartFlow(String driverIds,String key,String subject,String listDriver);
	
	/**
	 * 从模版中返回附件
	 * 
	 * @param id
	 *            
	 * @param templateCode
	 *            模板编码
	 * @return 返回生成的附件信息
	 */
	Attach doGetAttachFromTemplate(Long id, String templateCode)
			throws IOException;

}
