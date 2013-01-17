/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseAdviceServiceImpl extends DefaultCrudService<Case4Advice>
		implements CaseAdviceService {
	private CaseAdviceDao caseAdviceDao;
	private MotorcadeDao motorcadeDao;
	private SyncBaseDao syncBaseDao;
	private WorkflowService workflowService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	private TaskService taskService;

	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}

	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	@Autowired
	public void setMotorcadeDao(MotorcadeDao motorcadeDao) {
		this.motorcadeDao = motorcadeDao;
	}

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
	 * 
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4Advice save(Case4Advice e, SyncBase sb) {
		// 默认的保存处理
		e = super.save(e);
		if (sb != null) {
			// 保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	/**
	 * 核准操作
	 * 
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

		// 更新投诉的相关信息
		advice.setHandleStatus(Case4Advice.HANDLE_STATUS_DONE);
		advice.setHandlerId(handlerId);
		advice.setHandlerName(handlerName);
		advice.setHandleDate(handleDate);
		advice.setHandleOpinion(handleOpinion);

		return this.caseAdviceDao.save(advice);
	}

	public String doStartFlow(String key, Long[] ids) {

		// 声明返回的流程实例id 多个逗号隔开
		// String procInstIds = "";

		// 循环Id数组
		int i = 0;
		for (Long id : ids) {

			Case4Advice case4Advice = this.caseAdviceDao.load(id);
			// 声明变量
			Map<String, Object> variables = new HashMap<String, Object>();
			// 发起流程
			String procInstId = this.workflowService.startFlowByKey(key,
					this.returnParam(case4Advice, variables));
			// 完成第一步办理
			Task task = this.taskService.createTaskQuery()
					.processInstanceId(procInstId).singleResult();
			this.workflowService.completeTask(task.getId());
			// 保存流程与交通违法信息的关系
			WorkflowModuleRelation workflowModuleRelation = new WorkflowModuleRelation();
			workflowModuleRelation.setMid(id);
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(Case4InfractTraffic.class
					.getSimpleName());
			this.workflowModuleRelationService.save(workflowModuleRelation);
			// procInstIds += procInstId + ",";
			// 将交通违法信息的状态更改为处理中
			case4Advice.setStatus(CaseBase.STATUS_HANDLING);
			this.caseAdviceDao.save(case4Advice);
			i++;
		}

		return String.valueOf(i);
	}

	// 返回的全局参数
	private Map<String, Object> returnParam(Case4Advice case4Advice,
			Map<String, Object> variables) {
		// variables.put("case4Advice_id", case4Advice.getId());
		// variables.put("case4InfractTraffic_carPlate",
		// case4InfractTraffic.getCarPlate());
		// variables.put("case4InfractTraffic_carId",
		// case4InfractTraffic.getCarId());
		// variables.put("case4InfractTrafficr_motorcadeName",
		// case4InfractTraffic.getMotorcadeName());
		// variables.put("motorcadeId", case4InfractTraffic.getMotorcadeId());
		// // 查找分公司Id
		// if (case4Advice.getMotorcadeId() != null) {
		// Motorcade m = this.motorcadeDao.load(case4InfractTraffic
		// .getMotorcadeId());
		// variables.put("filialeId", m.getUnit().getId());
		// }
		// variables.put("case4InfractTrafficr_from", case4InfractTraffic
		// .getFrom() != null ? case4InfractTraffic.getFrom() : "");
		// variables
		// .put("case4InfractTraffic_happenDate",
		// case4InfractTraffic.getHappenDate().getTime() != null ? DateUtils
		// .formatCalendar(
		// case4InfractTraffic.getHappenDate(),
		// "yyyy-MM-dd HH:mm") : "");
		// variables.put("case4InfractTrafficr_address",
		// case4InfractTraffic.getAddress());
		// variables.put("case4InfractTrafficr_subject", case4InfractTraffic
		// .getSubject() != null ? case4InfractTraffic.getSubject() : "");
		// // 组装主题
		// variables
		// .put("subject",
		// case4InfractTraffic.getCarPlate()
		// + "交通违法处理："
		// + (case4InfractTraffic.getSubject() != null ? case4InfractTraffic
		// .getSubject() : ""));
		// variables.put("case4InfractTrafficr_infractCode",
		// case4InfractTraffic.getInfractCode());
		// variables.put("case4InfractTrafficr_jeom",
		// case4InfractTraffic.getJeom());
		// variables.put("case4InfractTrafficr_penalty",
		// case4InfractTraffic.getPenalty());
		// // 司机Id
		// variables.put("case4InfractTrafficr_driverId", case4InfractTraffic
		// .getDriverId() != null ? case4InfractTraffic.getDriverId()
		// : null);
		//
		return variables;
	}

}