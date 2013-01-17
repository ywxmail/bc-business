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
import cn.bc.workflow.domain.ExcutionLog;
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
			//记录同步的详细关键信息
			String desc="";
			if(flag_status){
				desc+="将"+tempDriver.getName()+"的招聘信息状态从";
				switch(tempDriver.getStatus()){
					case TempDriver.STATUS_RESERVE:
						desc+="待聘";
						break;
					case TempDriver.STATUS_CHECK:
						desc+="审批中";
						break;
					case TempDriver.STATUS_PASS:
						desc+="聘用";
						break;
					case TempDriver.STATUS_GIVEUP:
						desc+="未聘用";
						break;
				}
				desc+="修改为审批中";
				//更新司机的状态为审批中
				tempDriver.setStatus(TempDriver.STATUS_CHECK);
				this.tempDriverDao.save(tempDriver);
			}
			SystemContext sc=SystemContextHolder.get();
			sc.setAttr(ExcutionLog.SYNC_INFO_FLAG,true);
			sc.setAttr(ExcutionLog.SYNC_INFO_VALUE, desc);
			
			variables=new HashMap<String, Object>();
			
			//入职审批流程加载全部信息
			if("CarManEntry".equals(key)){
				this.returnParam(tempDriver, variables);
			}else{
				variables.put("tempDriver_id", tempDriver.getId());
				variables.put("name", tempDriver.getName());
				variables.put("certIdentity", tempDriver.getCertIdentity());
			}
			
			//发起流程
			String procInstId= this.workflowService.startFlowByKey(key,variables);
			procInstIds+=procInstId+",";
			//增加流程关系
			workflowModuleRelation=new WorkflowModuleRelation();
			workflowModuleRelation.setMid(id);
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(TempDriver.WORKFLOW_MTYPE);
			this.workflowModuleRelationService.save(workflowModuleRelation);
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
		//variables.put("applyAttr", tempDriver.getApplyAttr()!=null?tempDriver.getApplyAttr():"");
		variables.put("formerUnit", tempDriver.getFormerUnit()!=null?tempDriver.getFormerUnit():"");
		variables.put("issue", tempDriver.getIssue()!=null?tempDriver.getIssue():"");
		variables.put("isCrimeRecode", tempDriver.getIsCrimeRecode()!=null?tempDriver.getIsCrimeRecode()+"":"");
		variables.put("model", tempDriver.getModel()!=null?tempDriver.getModel()+"":"");
		variables.put("certDriving", tempDriver.getCertDriving()!=null?tempDriver.getCertDriving()+"":"");
		variables.put("certDrivingStartDate", tempDriver.getCertDrivingStartDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getCertDrivingStartDate()):"");
		variables.put("certDrivingEndDate", tempDriver.getCertDrivingEndDate()!=null?DateUtils.formatCalendar2Day(tempDriver.getCertDrivingEndDate()):"");
		variables.put("certDrivingArchive", tempDriver.getCertDrivingArchive()!=null?tempDriver.getCertDrivingArchive():"");
		return variables;
	}
	
	private Map<String,Object> date4key(String key,Calendar calendar,Map<String,Object> variables){
		if(variables==null)
			return null;
		if(key==null||key.length()==0||calendar==null)
			return variables;
			
		variables.put(key+"4yyyy", DateUtils.formatCalendar(calendar, "yyyy"));
		variables.put(key+"4MM", DateUtils.formatCalendar(calendar, "MM"));
		variables.put(key+"4dd", DateUtils.formatCalendar(calendar, "dd"));
		
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
		
		//修改信誉档案的显示
		String credit=params.get("credit").toString();
		if(!"".equals(credit)){
			credit=credit.replace("bc/attach/", SystemContextHolder.get().getAttr("htmlPageNamespace")+"/bc/attach/");
			credit=credit.replaceAll("<font size=\"\\d\">", "");
			credit=credit.replace("</font>", "");
			credit=credit.replace("padding-right:8px;", "");
			params.put("credit", credit);
		}
		
		params.put("sex",tempDriver.getSex()==1?"男":"女");
		
		params.put("age",DateUtils.getAge(tempDriver.getBirthdate()));
		
		//出生日期格式化
		date4key("birthdate",tempDriver.getBirthdate(), params);
		//驾驶证初领日期
		date4key("certDrivingFirstDate",tempDriver.getCertDrivingFirstDate(), params);
		//驾驶证起效日期
		date4key("certDrivingStartDate",tempDriver.getCertDrivingStartDate(), params);
		//驾驶证无效日期
		date4key("certDrivingEndDate",tempDriver.getCertDrivingEndDate(), params);
		
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