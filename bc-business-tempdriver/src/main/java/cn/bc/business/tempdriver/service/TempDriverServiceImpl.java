package cn.bc.business.tempdriver.service;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
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
import cn.bc.web.ui.json.Json;
import cn.bc.workflow.domain.ExcutionLog;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;
import cn.bc.workflow.service.WorkspaceServiceImpl;

/**
 * 司机招聘Service的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverServiceImpl extends DefaultCrudService<TempDriver> implements
		TempDriverService {
	private static Log logger = LogFactory.getLog(TempDriverServiceImpl.class);
	
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
		Attach attach = doGetAttachFromTemplate(id,templateCode);
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

	public String doStartFlow(String driverIds,String key,String subject,String listDriver){
		//声明变量
		Map<String,Object> variables=new HashMap<String, Object>();
		variables.put("list_driver", listDriver);
		variables.put("subject", subject);
		String procInstId=this.workflowService.startFlowByKey(key,variables);
		
		//流程模块关系domain
		WorkflowModuleRelation workflowModuleRelation;
		for(String id:driverIds.split(",")){
			//增加流程关系
			workflowModuleRelation=new WorkflowModuleRelation();
			workflowModuleRelation.setMid(Long.valueOf(id));
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(TempDriver.WORKFLOW_MTYPE);
			this.workflowModuleRelationService.save(workflowModuleRelation);
		}
		
		//发起流程
		return procInstId;
	}

	public Attach doGetAttachFromTemplate(Long id, String templateCode)
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
		return attach;
	}

	public boolean requestServiceCertificateValidate(Long id,
			String carManEntryKey,Json json){
		Assert.assertNotNull(id);
		Assert.assertNotNull(carManEntryKey);
		Assert.assertNotNull(json);
	
		TempDriver driver=this.tempDriverDao.load(id);
		
		//不存在流程关系
		if(!this.workflowModuleRelationService.hasRelation4Key(id,TempDriver.WORKFLOW_MTYPE,carManEntryKey))
			return false;
		//声明变量
		String _true="1";
		
		//设置需要获取的变量
		String[] args=new String[] {"isGiveUp","isPass","isPairDriver","pairDriverName","pairDriverNameId" };
		//复试组组长任务key
		String taskKey = "t100RetestHeadCheck";
		//本地变量
		String localKey = "isPass_lc";
		
		//获取最新的司机入职流程相关参数
		Map<String,Object> driver_wf=
				this.workflowModuleRelationService.findList(id, TempDriver.WORKFLOW_MTYPE,carManEntryKey,args).get(0);
		//流程id
		String pid = driver_wf.get("pid").toString();
		//流程状态
		int pStatus=Integer.valueOf(driver_wf.get("status").toString());
		//查找复试组长是否选择通过变量
		Object pass_lc=this.workflowService.findLocalValue(pid,taskKey,localKey);
		//复试组长 选项                                流程未到复试组长组长审批                 选择不通过
		boolean isPass_lc = _true.equals(pass_lc);
		//不具备发起的条件
		//流程未结束，(流程未到复试组长组长审批或复试组组长选择不通过)
		if(SuspensionState.ACTIVE.getStateCode()==pStatus && !isPass_lc)return false;
		
		//司机放弃入职变量
		boolean isGiveUp = _true.equals(driver_wf.get("isGiveUp"));
		//司机通过入职变量
		boolean isPass = _true.equals(driver_wf.get("isPass"));
		//是否有对班司机编辑
		boolean isPair= _true.equals(driver_wf.get("isPairDriver"));
		
		//无论是否有对班司机 ，都不具备发起的条件
		//流程结束，司机选择放弃
		if(WorkspaceServiceImpl.COMPLETE==pStatus && isGiveUp)return false;
		//流程结束，司机不通过
		if(WorkspaceServiceImpl.COMPLETE==pStatus && !isPass)return false;
		
		//设置司机信息
		json.put("id", id.toString());
		json.put("name", driver.getName());
		json.put("applyAttr", driver.getApplyAttr());//申请属性
		//设置对班司机的身份证很从业资格证
		json.put("certIdentity", driver.getCertIdentity());
		json.put("certCYZG", driver.getCertCYZG());
		json.put("pid", pid);
		json.put("pname",driver_wf.get("name").toString());
		//设置是否有对班 默认没有
		json.put("isPairDriver",false);
		
		//没有对班，司机具备发起服务资格证流程条件
		//流程结束  &&没有放弃 &&入职通过  
		if(!isPair && WorkspaceServiceImpl.COMPLETE==pStatus && !isGiveUp && isPass)return true;
		//流程未结束 && 复试组组长选择通过
		if(!isPair && SuspensionState.ACTIVE.getStateCode() == pStatus && isPass_lc)return true;
		
		//******存在对班 查找对班的入职情况***开始****
		//对班id
		Long pDriverId = Long.valueOf(driver_wf.get("pairDriverNameId").toString());
		TempDriver pDriver=this.tempDriverDao.load(pDriverId);
		json.put("isPairDriver",true);
		
		//对班存在入职流程
		if(this.workflowModuleRelationService.hasRelation4Key(pDriverId,TempDriver.WORKFLOW_MTYPE,carManEntryKey)){
			//获取对班司机最新的司机入职流程相关参数
			Map<String,Object> pDriver_wf=this.workflowModuleRelationService.findList(pDriverId, TempDriver.WORKFLOW_MTYPE
					,carManEntryKey,args).get(0);
			//流程id
			String pDriver_pid = pDriver_wf.get("pid").toString();
			//司机放弃入职变量
			boolean pDriver_isGiveUp =_true.equals(pDriver_wf.get("isGiveUp"));
			//司机通过入职变量
			boolean pDriver_isPass = _true.equals(pDriver_wf.get("isPass"));
			//是否有对班司机编辑
			boolean pDriver_isPair = _true.equals(pDriver_wf.get("isPairDriver"));
			//流程状态
			int pDriver_pStatus = Integer.valueOf(pDriver_wf.get("status").toString());
			//查找复试组长是否选择通过变量
			Object pDriver_pass_lc=this.workflowService.findLocalValue(pDriver_pid,taskKey,localKey);
			//复试组长 选项                                                              流程未到复试组长组长审批           选择不通过
			boolean pDriver_isPass_lc =_true.equals(pDriver_pass_lc);
			    
			//司机和对班都具备发起服务资格证流程的条件
			if(//条件1)流程结束  &&没有放弃 &&入职通过  && 有对班
				(WorkspaceServiceImpl.COMPLETE==pDriver_pStatus && !pDriver_isGiveUp && pDriver_isPass && pDriver_isPair)
				//条件2)流程未结束 && 复试组组长选择通过 && 有对班
				||(SuspensionState.ACTIVE.getStateCode()==pDriver_pStatus && pDriver_isPass_lc && pDriver_isPair)){
				//设置对班司机信息
				json.put("pair_id", pDriverId.toString());
				json.put("pair_name", pDriver.getName());
				json.put("pair_applyAttr", pDriver.getApplyAttr());//申请属性
				//设置对班司机的身份证很从业资格证
				json.put("pair_certIdentity", pDriver.getCertIdentity());
				json.put("pair_certCYZG", pDriver.getCertCYZG());
				json.put("pair_pid", pDriver_pid);
				json.put("pair_pname",pDriver_wf.get("name").toString());
				return true;
			}
		}
		//******存在对班 查找对班的入职情况***结束****
			
		//****查找对班但未符合发起的条件，入职通过的司机再找另外一个对班司机的情况****开始*****
		List<Map<String,Object>> others=this.workflowModuleRelationService.findList(null, TempDriver.WORKFLOW_MTYPE
				,carManEntryKey,new String[] {"isGiveUp","isPass","isPairDriver","pairDriverName","pairDriverNameId","tempDriver_id"});
		
		for(Map<String,Object> wmr:others){
			if(_true.equals(wmr.get("isPairDriver"))//有选择对班
					//选择原通过的司机
					&&String.valueOf(id).equals(wmr.get("pairDriverNameId"))){
				//流程id
				String new_pid = wmr.get("pid").toString();
				//司机放弃入职变量
				boolean new_isGiveUp = _true.equals(wmr.get("isGiveUp"));
				//司机通过入职变量
				boolean new_isPass = _true.equals(wmr.get("isPass"));
				//流程状态
				int new_pStatus= Integer.valueOf(wmr.get("status").toString());
				
				//查找复试组长是否选择通过变量
				Object new_pass_lc=this.workflowService.findLocalValue(new_pid,taskKey,localKey);
				//复试组长 选项                                                   流程未到复试组长组长审批                          选择不通过
				boolean new_isPass_lc = _true.equals(new_pass_lc);
				
				//新的对班司机
				TempDriver newPairDriver=this.tempDriverDao.load(Long.valueOf(wmr.get("tempDriver_id").toString()));
				
				//司机和新的对班都具备发起服务资格证流程的条件
				if(//条件1)流程结束  &&没有放弃 &&入职通过
					(WorkspaceServiceImpl.COMPLETE==new_pStatus && !new_isGiveUp && new_isPass)
					//条件2)流程未结束 && 复试组组长选择通过 
					||(SuspensionState.ACTIVE.getStateCode()==new_pStatus && new_isPass_lc)){
					//设置对班司机信息
					json.put("pair_id", newPairDriver.getId().toString());
					json.put("pair_name", newPairDriver.getName());
					json.put("pair_applyAttr", newPairDriver.getApplyAttr());//申请属性
					//设置对班司机的身份证很从业资格证
					json.put("pair_certIdentity", newPairDriver.getCertIdentity());
					json.put("pair_certCYZG", newPairDriver.getCertCYZG());
					json.put("pair_pid", new_pid);
					json.put("pair_pname",wmr.get("name").toString());
					return true;
				}
			}
		}
		//****查找对班但未符合发起的条件，入职通过的司机再找另外一个对班司机的情况****结束*****

		return false;
	}
}