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
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.runcase.dao.CaseAdviceDao;
import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
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
			workflowModuleRelation.setMtype(Case4Advice.class.getSimpleName());
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
		variables.put("case4Advice_id", case4Advice.getId());
		// 受理号
		variables.put("case4Advice_receiveCode", case4Advice.getReceiveCode());
		// 投诉来源
		variables.put("case4Advice_from",
				case4Advice.getFrom() != null ? case4Advice.getFrom() : "");
		// 站场
		variables.put("case4Advice_yard", "");
		// 转协查时间
		variables.put("case4InfractTrafficr_turnInvestigationTime", "");
		// 处理期限
		variables.put("case4Advice_treatmentPeriod", "");

		// 投诉人
		variables.put("case4Advice_advisorName", case4Advice.getAdvisorName());

		variables.put("motorcadeId", case4Advice.getMotorcadeId());
		// 查找分公司Id
		if (case4Advice.getMotorcadeId() != null) {
			Motorcade m = this.motorcadeDao.load(case4Advice.getMotorcadeId());
			variables.put("filialeId", m.getUnit().getId());
		}
		// 投诉电话
		variables
				.put("case4Advice_advisorPhone", case4Advice.getAdvisorPhone());
		// 投诉人性别
		variables.put("case4Advice_advisorSex", case4Advice.getAdvisorSex());
		// 事发时间
		variables.put(
				"case4Advice_happenDate",
				case4Advice.getHappenDate().getTime() != null ? DateUtils
						.formatCalendar(case4Advice.getHappenDate(),
								"yyyy-MM-dd HH:mm") : "");
		// 乘车路线(从)
		variables.put("case4Advice_pathFrom", case4Advice.getPathFrom());
		// 乘车路线(到)
		variables.put("case4Advice_pathTo", case4Advice.getPathTo());
		// 乘车路线
		variables.put("case4Advice_path", case4Advice.getPath());
		// 车牌号码
		variables.put("case4Advice_carPlate", case4Advice.getCarPlate());
		// 车辆id
		variables.put("case4Advice_carId", case4Advice.getCarId());
		// 资格证号码
		variables.put("case4Advice_driverCert", case4Advice.getDriverCert());
		// 计价器显示
		variables
				.put("case4Advice_machinePrice", case4Advice.getMachinePrice());
		// 实际收费
		variables.put("case4Advice_charge", case4Advice.getCharge());
		// 车色
		variables.put("case4Advice_carColor", case4Advice.getCarColor());
		// 乘车人数
		variables
				.put("case4Advice_number4Passenger",
						"男："
								+ (case4Advice.getPassengerManCount() != null ? case4Advice
										.getPassengerManCount() : 0)
								+ "，女："
								+ (case4Advice.getPassengerWomanCount() != null ? case4Advice
										.getPassengerWomanCount() : 0)
								+ "，儿童："
								+ (case4Advice.getPassengerChildCount() != null ? case4Advice
										.getPassengerChildCount() : 0));
		// 车票号码
		variables.put("case4Advice_ticket", case4Advice.getTicket());
		// 司机特征
		variables.put("case4Advice_driverFeature",
				case4Advice.getDriverFeature());
		// 司机性别
		variables.put("case4Advice_driverSex", case4Advice.getDriverSex());
		// 投诉内容
		variables.put("case4Advice_detail", case4Advice.getDetail());
		// 组装主题
		variables.put("subject", "关于" + case4Advice.getCarPlate() + "客管投诉处理："
				+ case4Advice.getSubject());
		return variables;
	}

	public String getCaseTrafficInfoByCarManId(Long carManId,
			Calendar happenDate) {
		Calendar startDate = Calendar.getInstance();
		// 开始日期为事发日期的前一年
		startDate.set(Calendar.YEAR, happenDate.get(Calendar.YEAR) - 1);
		startDate.set(Calendar.MONTH, happenDate.get(Calendar.MONTH));
		startDate.set(Calendar.DAY_OF_MONTH,
				happenDate.get(Calendar.DAY_OF_MONTH));
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);

		return this.caseAdviceDao.getCaseTrafficInfoByCarManId(carManId,
				startDate, happenDate);
	}

	public void updateCaseAdviceInfo4Flow(Long id,
			Map<String, Object> attributes) {
		this.caseAdviceDao.updateCaseAdviceInfo4Flow(id, attributes);

	}

}