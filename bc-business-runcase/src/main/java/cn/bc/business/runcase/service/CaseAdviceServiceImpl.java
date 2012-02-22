/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseAdviceServiceImpl extends DefaultCrudService<Case4Advice> implements
		CaseAdviceService {
	private CaseAdviceDao caseAdviceDao;

	private SyncBaseDao syncBaseDao;

	@Autowired
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}
	
	public CaseAdviceDao getCaseAdviceDao() {
		return caseAdviceDao;
	}

	public void setCaseAdviceDao(CaseAdviceDao caseAdviceDao) {
		this.caseAdviceDao = caseAdviceDao;
		this.setCrudDao(caseAdviceDao);
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4Advice save(Case4Advice e, SyncBase sb) {
		//默认的保存处理
		e = super.save(e);
		if(sb != null){
			//保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	/**
	 * 核准操作
	 * @param fromAdviceId
	 * @param handlerId
	 * @param handlerName
	 * @param handleDate
	 * @param handleOpinion
	 * @return
	 */
	public Case4Advice doManage(Long fromAdviceId, Long handlerId,
			String handlerName, Calendar handleDate, String handleOpinion) {
		// 获取原来的投诉信息
		Case4Advice advice = this.caseAdviceDao.load(fromAdviceId);
		if (advice == null)
			throw new CoreException("要核准的投诉已不存在！fromAdviceId=" + fromAdviceId);
		
		//更新投诉的相关信息
		advice.setHandleStatus(Case4Advice.HANDLE_STATUS_DONE);
		advice.setHandlerId(handlerId);
		advice.setHandlerName(handlerName);
		advice.setHandleDate(handleDate);
		advice.setHandleOpinion(handleOpinion);
		
		return this.caseAdviceDao.save(advice);
	}
}