package cn.bc.business.tempdriver.service;

import java.util.Calendar;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.business.tempdriver.domain.TempDriverWorkFlow;
import cn.bc.core.service.CrudService;

/**
 * 司机招聘流程记录Service
 * 
 * @author lbj
 * 
 */
public interface TempDriverWorkFlowService extends CrudService<TempDriverWorkFlow> {

	/**
	 * 插入一条已发起的流程记录
	 * 
	 * @param startTime:发起时间，procInstId:流程实例Id，tempDriver:招聘司机；
	 * 
	 */
	void save(Calendar startTime,String procInstId,TempDriver tempDriver);
	
	
	/**
	 * 更新审批结果为聘用
	 * 	同事更新司机招聘表中的状态为聘用
	 * 
	 * @param procInstId:流程实例Id
	 */
	void doUpdatePass(String procInstId);
	
	/**
	 * 更新审批结果为弃用
	 * 	同事更新司机招聘表中的状态为弃用
	 * 
	 * @param procInstId:流程实例Id
	 */
	void doUpdateGiveUp(String procInstId);
}
