package cn.bc.business.tempdriver.service;

import java.util.Calendar;

import cn.bc.business.tempdriver.dao.TempDriverWorkFlowDao;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.business.tempdriver.domain.TempDriverWorkFlow;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机招聘流程记录Service的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverWorkFlowServiceImpl extends DefaultCrudService<TempDriverWorkFlow> implements
		TempDriverWorkFlowService {
	private TempDriverWorkFlowDao tempDriverWorkFlowDao;

	public void setTempDriverWorkFlowDao(TempDriverWorkFlowDao tempDriverWorkFlowDao) {
		this.tempDriverWorkFlowDao = tempDriverWorkFlowDao;
		this.setCrudDao(tempDriverWorkFlowDao);
	}
	

	public void save(Calendar startTime, String procInstId,TempDriver tempDriver) {
		TempDriverWorkFlow t=tempDriverWorkFlowDao.create();
		t.setOfferStatus(TempDriverWorkFlow.OFFER_STATUS_CHECK);
		t.setProcInstId(procInstId);
		t.setStartTime(startTime);
		t.setTempDriver(tempDriver);
		tempDriverWorkFlowDao.save(t);
	}

	public void doUpdatePass(String procInstId) {
		this.tempDriverWorkFlowDao.update(procInstId, TempDriverWorkFlow.OFFER_STATUS_PASS, TempDriver.STATUS_PASS);
	}


	public void doUpdateGiveUp(String procInstId) {
		this.tempDriverWorkFlowDao.update(procInstId, TempDriverWorkFlow.OFFER_STATUS_NOPASS, TempDriver.STATUS_GIVEUP);
	}

	
}
