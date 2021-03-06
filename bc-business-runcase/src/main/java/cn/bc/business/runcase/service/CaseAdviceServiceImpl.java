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
import cn.bc.web.ui.json.Json;
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

	public String doStartFlow(String key1, String key2, Long[] ids) {
		Json json = new Json();

		// 声明返回的流程实例id 多个逗号隔开
		// String procInstIds = "";

		// 循环Id数组
		int s = 0;// 成功发起的数量
		int f4Cid = 0;// 失败(没有车辆Id)发起的数量
		int f4Status = 0;// 失败(不是在案的)发起的数量
		boolean succeed = true;// 标记是否全部发起成功
		String code4Cid = "";// 未成功发起的受理号(没有车辆Id)
		String code4Status = "";// 未成功发起的受理号(不是在案状态)
		int type = 0;
		for (Long id : ids) {
			Case4Advice case4Advice = this.caseAdviceDao.load(id);
			String procInstId = null;// 流程id
			// 声明变量
			Map<String, Object> variables = new HashMap<String, Object>();
			if (case4Advice.getStatus() == CaseBase.STATUS_ACTIVE
					&& case4Advice.getCarId() != null) {
				// 在案的并且车辆id不为空的信息才能发起
				if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 发起公司投诉处理流程
					// 发起流程
					procInstId = this.workflowService.startFlowByKey(key2,
							this.returnParam(case4Advice, variables));

				} else if (case4Advice.getType() == CaseBase.TYPE_COMPLAIN) {// 发起客管投诉处理流程
					// 发起流程
					procInstId = this.workflowService.startFlowByKey(key1,
							this.returnParam(case4Advice, variables));
				}
				if (procInstId != null) {
					// 完成第一步办理
					Task task = this.taskService.createTaskQuery()
							.processInstanceId(procInstId).singleResult();
					this.workflowService.completeTask(task.getId());
					// 保存流程与交通违法信息的关系
					WorkflowModuleRelation workflowModuleRelation = new WorkflowModuleRelation();
					workflowModuleRelation.setMid(id);
					workflowModuleRelation.setPid(procInstId);
					workflowModuleRelation.setMtype(Case4Advice.class
							.getSimpleName());
					this.workflowModuleRelationService
							.save(workflowModuleRelation);
					// procInstIds += procInstId + ",";
					// 将交通违法信息的状态更改为处理中
					case4Advice.setStatus(CaseBase.STATUS_HANDLING);
					this.caseAdviceDao.save(case4Advice);
					s++;
				}
			} else {
				// 如果车辆id为空的提示要确定车辆
				if (case4Advice.getCarId() == null) {
					succeed = false;
					f4Cid++;
					if (f4Cid == 1) {
						code4Cid = case4Advice.getReceiveCode();
					} else {
						code4Cid = code4Cid + ","
								+ case4Advice.getReceiveCode();
					}
				} else {// 提示只有在案的才能发起
					succeed = false;
					f4Status++;
					if (f4Status == 1) {
						code4Status = case4Advice.getReceiveCode();
					} else {
						code4Status = code4Status + ","
								+ case4Advice.getReceiveCode();
					}
				}

			}

		}
		if (succeed) {
			json.put("msg", "成功发起" + s + "条"
					+ (type == CaseBase.TYPE_COMPLAIN ? "客管投诉" : "自接投诉") + "信息");
		} else {
			json.put("msg", (s != 0 ? "成功发起" + s + "条"
					+ (type == CaseBase.TYPE_COMPLAIN ? "客管投诉" : "自接投诉")
					+ "信息,其中" : "")
					+ (f4Status != 0 ? f4Status + "条因不是在案状态的投诉信息发起失败！受理号为："
							+ code4Status : "")
					+ (f4Cid != 0 ? f4Cid + "条因没有指定车辆的投诉信息发起失败！受理号为："
							+ code4Cid : ""));
		}
		json.put("success", succeed);
		return json.toString();
	}

	// 返回的全局参数
	private Map<String, Object> returnParam(Case4Advice case4Advice,
			Map<String, Object> variables) {
		// 流转
		variables.put("goTo", true);
		// 服务督办员是否能办理任务
		variables.put("isTransact4SupervisoryMember", false);
		// 分公司经理是否能办理
		variables.put("isTransact4BranchManager", false);

		variables.put("case4Advice_id", case4Advice.getId());
		// 受理号
		variables.put("case4Advice_receiveCode", case4Advice.getReceiveCode());
		// 投诉来源
		if (case4Advice.getType() == CaseBase.TYPE_COMPLAIN) {// 客管投诉
			// variables.put("case4Advice_from",
			// case4Advice.getFrom() != null ? case4Advice.getFrom() : "");
			variables.put("case4Advice_from", "客管投诉");

		} else if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 自接投诉
			variables.put("case4Advice_from", "自接投诉");
		}
		// 站场
		variables.put("case4Advice_yard", "");
		// 转协查时间
		variables.put("case4InfractTrafficr_turnInvestigationTime", "");
		// 处理期限
		if (case4Advice.getType() == CaseBase.TYPE_COMPLAIN) {// 客管投诉[发起日加四个自然日]
			// 当前时间
			Calendar startDate = Calendar.getInstance();
			// 处理期限的结束时间
			Calendar endtDate = Calendar.getInstance();
			endtDate.set(Calendar.DAY_OF_MONTH,
					startDate.get(Calendar.DAY_OF_MONTH) + 4);

			variables.put("case4Advice_treatmentPeriod",
					DateUtils.formatCalendar(startDate, "yyyy-MM-dd") + "~"
							+ DateUtils.formatCalendar(endtDate, "yyyy-MM-dd"));
		} else if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 自接投诉[五个工作日]
			variables.put("case4Advice_treatmentPeriod", "五个工作日");
		}

		// 乘车时间 (自接投诉)
		if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {
			// ridingStartTime
			variables
					.put("case4Advice_ridingTime",
							(case4Advice.getRidingStartTime() != null ? DateUtils
									.formatCalendar(
											case4Advice.getRidingStartTime(),
											"yyyy-MM-dd HH:mm") : "")
									+ "-"
									+ (case4Advice.getRidingEndTime() != null ? DateUtils
											.formatCalendar(case4Advice
													.getRidingEndTime(),
													"HH:mm") : ""));
		}

		// 投诉人
		variables.put("case4Advice_advisorName", case4Advice.getAdvisorName());

		variables.put("motorcadeId", case4Advice.getMotorcadeId());
		// 查找分公司Id
		if (case4Advice.getMotorcadeId() != null) {
			Motorcade m = this.motorcadeDao.load(case4Advice.getMotorcadeId());
			variables.put("filialeId", m.getUnit().getId());
			variables.put("filiale", m.getUnit().getName());
			variables.put("motorcade", m.getName());
			// 车队长负责人
			variables.put("principalName4motorcade", m.getPrincipalName());

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
		// 接案时间（公司投诉）
		if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {
			if (case4Advice.getHappenDate() != null) {
				variables
						.put("case4Advice_receiveDate",
								case4Advice.getHappenDate().getTime() != null ? DateUtils
										.formatCalendar(
												case4Advice.getHappenDate(),
												"yyyy-MM-dd HH:mm") : "");
			}
		}
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
		if (case4Advice.getType() == CaseBase.TYPE_COMPLAIN) {// 客管投诉
			variables.put("subject", "关于" + case4Advice.getCarPlate()
					+ "客管投诉处理：" + case4Advice.getSubject());

		} else if (case4Advice.getType() == CaseBase.TYPE_COMPANY_COMPLAIN) {// 自接投诉
			variables.put("subject", "关于" + case4Advice.getCarPlate()
					+ "自接投诉处理：" + case4Advice.getSubject());
		}
		// 投诉项目
		variables
				.put("case4Advice_complaintsProject", case4Advice.getSubject());
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