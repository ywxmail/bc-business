package cn.bc.business.tempdriver.dao;

import cn.bc.business.tempdriver.domain.TempDriverWorkFlow;
import cn.bc.core.dao.CrudDao;

/**
 * 司机招聘流程记录Dao
 * 
 * @author lbj
 * 
 */
public interface TempDriverWorkFlowDao extends CrudDao<TempDriverWorkFlow> {
	
	/**
	 * 获取去对象
	 * @param procInstId 流程实例id
	 * @return
	 */
	TempDriverWorkFlow loadByProcInstId(String procInstId);
	
	/**
	 * 更新审批结果
	 * 		同时更新司机招聘表中的状态
	 * 
	 * @param procInstId 流程实例Id 
	 * @param offerStatus 审批结果
	 * @param tempDriverStatus 招聘司机状态
	 * 
	 */
	void update(String procInstId,int offerStatus ,int tempDriverStatus);
}
