/**
 * 
 */
package cn.bc.business.runcase.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
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
		e = this.forceLoad(e.getId());
		return e;
	}
}