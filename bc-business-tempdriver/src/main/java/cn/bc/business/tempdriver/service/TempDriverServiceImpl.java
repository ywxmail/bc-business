package cn.bc.business.tempdriver.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.tempdriver.dao.TempDriverDao;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.template.domain.Template;
import cn.bc.template.service.TemplateService;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 司机招聘Service的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverServiceImpl extends DefaultCrudService<TempDriver> implements
		TempDriverService {
	private static Log logger = LogFactory
			.getLog(TempDriverServiceImpl.class);
	
	private TempDriverDao tempDriverDao;
	private WorkflowService workflowService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	
	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}

	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setTempDriverDao(TempDriverDao tempDriverDao) {
		this.tempDriverDao = tempDriverDao;
		this.setCrudDao(tempDriverDao);
	}

	public String doStartFlow(String key, Long[] ids,boolean flag_status) {
		//声明变量
		Map<String,Object> variables;
		TempDriver tempDriver;
		//流程模块关系domain
		WorkflowModuleRelation workflowModuleRelation;
		//声明返回的流程实例id 多个逗号隔开
		String procInstIds="";
		
		//循环Id数组
		for(Long id:ids){
			tempDriver=this.tempDriverDao.load(id);
			variables=new HashMap<String, Object>();
			//发起流程
			String procInstId= this.workflowService.startFlowByKey(key, this.returnParam(tempDriver, variables));
			procInstIds+=procInstId+",";
			workflowModuleRelation=new WorkflowModuleRelation();
			workflowModuleRelation.setMid(id);
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(TempDriver.WORKFLOW_MTYPE);
			this.workflowModuleRelationService.save(workflowModuleRelation);
			if(flag_status){
				//更新司机的状态为审批中
				tempDriver.setStatus(TempDriver.STATUS_CHECK);
				this.tempDriverDao.save(tempDriver);
			}
			
		}
		
		return procInstIds;
	}
	
	//返回的全局参数
	private Map<String,Object> returnParam(TempDriver tempDriver,Map<String,Object> variables){
		variables.put("tempDriver_id", tempDriver.getId());
		variables.put("tempDriver_status", tempDriver.getStatus());
		variables.put("tempDriver_uid", tempDriver.getUid());
		variables.put("tempDriver_desc", tempDriver.getDesc()!=null?tempDriver.getDesc():"");
		if(tempDriver.getModifier()!=null){
			variables.put("tempDriver_modifierId", tempDriver.getModifier().getId());
			variables.put("tempDriver_modifier", tempDriver.getModifier().getName());
			variables.put("tempDriver_modifierCode", tempDriver.getModifier().getCode());
			variables.put("tempDriver_modifiedDate", DateUtils.formatCalendar2Day(tempDriver.getModifiedDate()));
		}
		
		variables.put("address", tempDriver.getAddress() != null?tempDriver.getAddress():"");
		variables.put("birthdate", DateUtils.formatCalendar2Day(tempDriver.getBirthdate()));
		variables.put("certDrivingFirstDate", tempDriver.getCertDrivingFirstDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getCertDrivingFirstDate()):"");
		variables.put("cyStartYear", tempDriver.getCyStartYear()!=null?tempDriver.getCyStartYear().toString():"");
		variables.put("certCYZG", tempDriver.getCertCYZG()!=null?tempDriver.getCertCYZG():"");
		variables.put("certFWZG", tempDriver.getCertFWZG()!=null?tempDriver.getCertFWZG():"");
		variables.put("certIdentity", tempDriver.getCertIdentity());
		variables.put("education", tempDriver.getEducation()!=null?tempDriver.getEducation():"");
		variables.put("list_family", tempDriver.getListFamily()!=null?tempDriver.getListFamily():"");
		variables.put("list_workExperience", tempDriver.getListWorkExperience()!=null?tempDriver.getListWorkExperience():"");
		variables.put("marry", tempDriver.getMarry()!=null?tempDriver.getMarry():"");
		variables.put("name", tempDriver.getName());
		variables.put("nation", tempDriver.getNation()!=null?tempDriver.getNation():"");
		variables.put("newAddress", tempDriver.getNewAddress()!=null?tempDriver.getNewAddress():"");
		variables.put("origin", tempDriver.getOrigin()!=null?tempDriver.getOrigin():"");
		variables.put("phone", tempDriver.getPhone()!=null?tempDriver.getPhone():"");
		variables.put("region", tempDriver.getRegion());
		variables.put("sex", tempDriver.getSex());
		variables.put("credit", tempDriver.getCredit()!=null?tempDriver.getCredit():"");
		variables.put("validStartDate", tempDriver.getValidStartDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getValidStartDate()):"");
		variables.put("validEndDate", tempDriver.getValidEndDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getValidEndDate()):"");
		variables.put("creditDate", tempDriver.getCreditDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getCreditDate()):"");
		variables.put("interviewDate", tempDriver.getInterviewDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getInterviewDate()):"");
		variables.put("registerDate", tempDriver.getRegisterDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getRegisterDate()):"");
		variables.put("creditDesc", tempDriver.getCreditDesc()!=null?tempDriver.getCreditDesc():"");
		variables.put("crimeRecode", tempDriver.getCrimeRecode()!=null?tempDriver.getCrimeRecode():"");
		variables.put("backGround", tempDriver.getBackGround()!=null?tempDriver.getBackGround():"");
		variables.put("entryCar", tempDriver.getEntryCar()!=null?tempDriver.getEntryCar():"");
		variables.put("applyAttr", tempDriver.getApplyAttr()!=null?tempDriver.getApplyAttr():"");
		variables.put("formerUnit", tempDriver.getFormerUnit()!=null?tempDriver.getFormerUnit():"");
		variables.put("issue", tempDriver.getIssue()!=null?tempDriver.getIssue():"");
		variables.put("isCrimeRecode", tempDriver.getIsCrimeRecode()!=null?tempDriver.getIsCrimeRecode()+"":"");
		return variables;
	}

	public TempDriver loadByCertIdentity(String certIdentity) {
		return this.tempDriverDao.loadByCertIdentity(certIdentity);
	}

	public boolean isUniqueCertIdentity(Long id, String certIdentity) {
		return this.tempDriverDao.isUniqueCertIdentity(id, certIdentity);
	}
	
	private AttachService attachService;// 附件服务
	private TemplateService templateService;// 模板服务
	
	@Autowired
	public void setTemplateService(TemplateService templateService) {
		this.templateService = templateService;
	}
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public Attach doAddAttachFromTemplate(Long id, String templateCode)
			throws IOException {
		TempDriver tempDriver=this.load(id);

		
		// 获取模板
		Template template = this.templateService.loadByCode(templateCode);
		if (template == null) {
			logger.error("模板不存在,返回null:code=" + templateCode);
			throw new CoreException("模板不存在,code=" + templateCode);
		}

		String ptype = TempDriver.ATTACH_TYPE;
		String puid = tempDriver.getUid();
		
		
		// 不能格式化
		if (!template.isFormatted()) {
			Attach attach = template.format2Attach(null, ptype,puid);
			this.attachService.save(attach);
			return attach;
		}
		
		// 声明格式化参数
		Map<String, Object> params = new HashMap<String, Object>();
		params=this.returnParam(tempDriver, params);
		
		params.put("sex",tempDriver.getSex()==1?"男":"女");
		
		// 根据模板参数获取的替换值
		Map<String, Object> mapFormatSql = new HashMap<String, Object>();
		mapFormatSql.put("id", id);
		Map<String, Object> mapParams = templateService.getMapParams(
				template.getId(), mapFormatSql);
		if (mapParams != null)
			params.putAll(mapParams);
		
		//加载系统上下文属性
		params.put(SystemContext.class.getSimpleName(),SystemContextHolder.get());
		
		Attach attach = template.format2Attach(params, ptype, puid);
		this.attachService.save(attach);
		return attach;
	}

	public void doSyncPortrait() throws Exception {
		this.tempDriverDao.doSyncPortrait();
	}

	public void doUpdateStatus(Long[] ids, int status) {
		Map<String,Object> attributes=new HashMap<String, Object>();
		attributes.put("status", status);
		attributes.put("modifiedDate",Calendar.getInstance());
		attributes.put("modifier",SystemContextHolder.get().getUserHistory());
		this.tempDriverDao.update(ids, attributes);
	}

	public void doUpdateInterviewDate(Long[] ids, Calendar interviewDate) {
		Map<String,Object> attributes=new HashMap<String, Object>();
		attributes.put("interviewDate", interviewDate);
		attributes.put("modifiedDate",Calendar.getInstance());
		attributes.put("modifier",SystemContextHolder.get().getUserHistory());
		this.tempDriverDao.update(ids, attributes);
	}
}