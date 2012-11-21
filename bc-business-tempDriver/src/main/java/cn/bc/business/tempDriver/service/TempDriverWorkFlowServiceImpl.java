package cn.bc.business.tempDriver.service;

import cn.bc.business.tempDriver.dao.TempDriverWorkFlowDao;
import cn.bc.business.tempDriver.domain.TempDriverWorkFlow;
import cn.bc.core.service.DefaultCrudService;

/**
 * 安排Service的实现
 * 
 * @author dragon
 * 
 */
public class TempDriverWorkFlowServiceImpl extends DefaultCrudService<TempDriverWorkFlow> implements
		TempDriverWorkFlowService {
	private TempDriverWorkFlowDao tempDriverWorkFlowDao;

	public void setTempDriverWorkFlowDao(TempDriverWorkFlowDao tempDriverWorkFlowDao) {
		this.tempDriverWorkFlowDao = tempDriverWorkFlowDao;
		this.setCrudDao(tempDriverWorkFlowDao);
	}

	
}
