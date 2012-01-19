/**
 * 
 */
package cn.bc.business.runcase.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseTrafficServiceImpl extends DefaultCrudService<Case4InfractTraffic> implements
		CaseTrafficService {
	private CaseTrafficDao caseTrafficDao;

	private SyncBaseDao syncBaseDao;

	@Autowired
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}

	public CaseTrafficDao getCaseTrafficDao() {
		return caseTrafficDao;
	}


	public void setCaseTrafficDao(CaseTrafficDao caseTrafficDao) {
		this.caseTrafficDao = caseTrafficDao;
		this.setCrudDao(caseTrafficDao);
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4InfractTraffic save(Case4InfractTraffic e, SyncBase sb) {
		//默认的保存处理
		e = super.save(e);
		if(sb != null){
			//保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}
}