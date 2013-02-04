/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.runcase.dao.CaseBusinessDao;
import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 营运事件营运违章Service的实现
 * 
 * @author dragon
 */
public class CaseBusinessServiceImpl extends DefaultCrudService<Case4InfractBusiness> implements
		CaseBusinessService {
	private CaseBusinessDao caseBusinessDao;

	private SyncBaseDao syncBaseDao;
	private WorkflowService workflowService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	
	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}
	
	public CaseBusinessDao getCaseBusinessDao() {
		return caseBusinessDao;
	}
	
	@Autowired
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}


	public void setCaseBusinessDao(CaseBusinessDao caseBusinessDao) {
		this.caseBusinessDao = caseBusinessDao;
		this.setCrudDao(caseBusinessDao);
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4InfractBusiness save(Case4InfractBusiness e, SyncBase sb) {
		//默认的保存处理
		e = super.save(e);
		if(sb != null){
			//保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	/**
	 * 结案操作
	 * @param fromBusinessId
	 * @param closeDate
	 * @return
	 */
	public Case4InfractBusiness doCloseFile(Long fromBusinessId,
			Calendar closeDate) {
		Case4InfractBusiness business = this.caseBusinessDao.load(fromBusinessId);
		if(business == null)
			throw new CoreException("要处理的营运违章已不存在！businessId=" + fromBusinessId);
		
		//更新营运违章相关信息
		business.setStatus(CaseBase.STATUS_CLOSED);
		
		// 设置创建人信息和最后修改人信息
		SystemContext context = SystemContextHolder.get();
		business.setModifier(context.getUserHistory());
		business.setModifiedDate(Calendar.getInstance());
		
		//设置结案人,结案日期
		business.setCloserId(context.getUserHistory().getId());
		business.setCloserName(context.getUserHistory().getName());
		business.setCloseDate(closeDate);
		
		return this.caseBusinessDao.save(business);
	}
	
	public List<Map<String,String>> doStartFlow(String key, Long[] ids) throws Exception {
		// 声明返回的信息
		List<Map<String,String>> returnValue=new ArrayList<Map<String,String>>();
		
		Map<String,String> returnMap;
		
		// 循环Id数组
		for (Long id : ids) {
			returnMap=new HashMap<String,String>();
			Case4InfractBusiness cib = this.caseBusinessDao.load(id);
			// 声明变量
			Map<String, Object> variables = new HashMap<String, Object>();
			// 发起流程
			String procInstId = this.workflowService.startFlowByKey(key,
					this.returnParam(cib, variables));

			// 保存流程与交通违法信息的关系
			WorkflowModuleRelation workflowModuleRelation = new WorkflowModuleRelation();
			workflowModuleRelation.setMid(id);
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(Case4InfractBusiness.class.getSimpleName());
			this.workflowModuleRelationService.save(workflowModuleRelation);
			// procInstIds += procInstId + ",";
			// 将状态更改为处理中
			cib.setStatus(CaseBase.STATUS_HANDLING);
			this.caseBusinessDao.save(cib);
			
			returnMap.put("moduleId", String.valueOf(id));
			returnMap.put("procInstId", procInstId);
			
			returnValue.add(returnMap);
		}
		
		return returnValue;
	}
	
	private Map<String,Object> returnParam(Case4InfractBusiness cib,Map<String,Object> variables){
		if(variables==null)return null;
		if(cib==null)return null;
		
		return variables;
	}
}