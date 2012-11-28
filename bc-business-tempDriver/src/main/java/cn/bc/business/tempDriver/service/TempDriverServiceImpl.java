package cn.bc.business.tempdriver.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.tempdriver.dao.TempDriverDao;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.workflow.service.WorkflowService;

/**
 * 司机招聘Service的实现
 * 
 * @author lbj
 * 
 */
public class TempDriverServiceImpl extends DefaultCrudService<TempDriver> implements
		TempDriverService {
	private TempDriverDao tempDriverDao;
	private WorkflowService workflowService;
	private TempDriverWorkFlowService tempDriverWorkFlowService;
	
	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@Autowired
	public void setTempDriverWorkFlowService(
			TempDriverWorkFlowService tempDriverWorkFlowService) {
		this.tempDriverWorkFlowService = tempDriverWorkFlowService;
	}

	public void setTempDriverDao(TempDriverDao tempDriverDao) {
		this.tempDriverDao = tempDriverDao;
		this.setCrudDao(tempDriverDao);
	}

	public String doStartFlow(String key, Long[] ids) {
		//声明变量
		Map<String,Object> variables;
		TempDriver tempDriver;
		//声明返回的流程实例id 多个逗号隔开
		String procInstIds="";
		
		//循环Id数组
		for(Long id:ids){
			tempDriver=this.tempDriverDao.load(id);
			variables=new HashMap<String, Object>();
			//发起流程
			String procInstId= this.workflowService.startFlowByKey(key, this.returnFlowParam(tempDriver, variables));
			procInstIds+=procInstId+",";
			this.tempDriverWorkFlowService.save(Calendar.getInstance(), procInstId, tempDriver);
			//更新司机的状态为审批中
			tempDriver.setStatus(TempDriver.STATUS_CHECK);
			this.tempDriverDao.save(tempDriver);
		}
		
		return procInstIds;
	}
	
	//返回流程需加载的全局参数
	private Map<String,Object> returnFlowParam(TempDriver tempDriver,Map<String,Object> variables){
		variables.put("tDriver_id", tempDriver.getId());
		variables.put("tDriver_address", tempDriver.getAddress()+"");
		variables.put("tDriver_birthdate", DateUtils.formatCalendar2Day(tempDriver.getBirthdate()));
		variables.put("tDriver_caseAccident", tempDriver.getCaseAccident()+"");
		variables.put("tDriver_caseAdvice", tempDriver.getCaseAdvice()+"");
		variables.put("tDriver_caseBusiness", tempDriver.getCaseBusiness()+"");
		variables.put("tDriver_caseTraffic", tempDriver.getCaseTraffic()+"");
		variables.put("tDriver_certCYZG", tempDriver.getCertCYZG()+"");
		variables.put("tDriver_certFWZG", tempDriver.getCertFWZG()+"");
		variables.put("tDriver_certIdentity", tempDriver.getCertIdentity()+"");
		variables.put("tDriver_desc", tempDriver.getDesc()+"");
		variables.put("tDriver_education", tempDriver.getEducation()+"");
		variables.put("list_tDriver_family", tempDriver.getListFamily()+"");
		variables.put("list_tDriver_workExperience", tempDriver.getListWorkExperience()+"");
		variables.put("tDriver_marry", tempDriver.getMarry()+"");
		variables.put("tDriver_name", tempDriver.getName());
		variables.put("tDriver_nation", tempDriver.getNation()+"");
		variables.put("tDriver_newAddress", tempDriver.getNewAddress()+"");
		variables.put("tDriver_origin", tempDriver.getOrigin()+"");
		variables.put("tDriver_phone", tempDriver.getPhone()+"");
		variables.put("tDriver_region", tempDriver.getRegion()+"");
		variables.put("tDriver_sex", tempDriver.getSex());
		variables.put("tDriver_status", tempDriver.getStatus());
		variables.put("tDriver_uid", tempDriver.getUid());
		variables.put("tDriver_credit", tempDriver.getCredit()+"");
		if(tempDriver.getModifier()!=null){
			variables.put("tDriver_modifierId", tempDriver.getModifier().getId());
			variables.put("tDriver_modifier", tempDriver.getModifier().getName());
			variables.put("tDriver_modifierCode", tempDriver.getModifier().getCode());
			variables.put("tDriver_modifiedDate", DateUtils.formatCalendar2Day(tempDriver.getModifiedDate()));
		}
		//variables.put("subject","待招聘司机："+tempDriver.getName()+"的入职流程");

		return variables;
	}

	public TempDriver loadByCertIdentity(String certIdentity) {
		return this.tempDriverDao.loadByCertIdentity(certIdentity);
	}

	public boolean isUniqueCertIdentity(Long id, String certIdentity) {
		return this.tempDriverDao.isUniqueCertIdentity(id, certIdentity);
	}

}
