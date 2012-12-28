/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.dao.JiaoWeiJTWFDao;
import cn.bc.business.sync.dao.JinDunJTWFDao;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 营运事件交通违法Service的实现
 * 
 * @author dragon
 */
public class CaseTrafficServiceImpl extends
		DefaultCrudService<Case4InfractTraffic> implements CaseTrafficService {
	private CaseTrafficDao caseTrafficDao;
	private WorkflowService workflowService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	private TaskService taskService;
	private SyncBaseDao syncBaseDao; // 同步基表
	private JinDunJTWFDao jinDunJTWFDao; // 金盾网交通违法
	private JiaoWeiJTWFDao jiaoWeiJTWFDao; // 交委交通违法
	private CarDao carDao;
	private CarManDao carManDao;
	private MotorcadeDao motorcadeDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务

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
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}

	@Autowired
	public void setJinDunJTWFDao(JinDunJTWFDao jinDunJTWFDao) {
		this.jinDunJTWFDao = jinDunJTWFDao;
	}

	@Autowired
	public void setJiaoWeiJTWFDao(JiaoWeiJTWFDao jiaoWeiJTWFDao) {
		this.jiaoWeiJTWFDao = jiaoWeiJTWFDao;
	}

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}

	@Autowired
	public void setCarManDao(CarManDao carManDao) {
		this.carManDao = carManDao;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setMotorcadeDao(MotorcadeDao motorcadeDao) {
		this.motorcadeDao = motorcadeDao;
	}

	public CaseTrafficDao getCaseTrafficDao() {
		return caseTrafficDao;
	}

	public void setCaseTrafficDao(CaseTrafficDao caseTrafficDao) {
		this.caseTrafficDao = caseTrafficDao;
		this.setCrudDao(caseTrafficDao);
	}

	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * 
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4InfractTraffic save(Case4InfractTraffic e, SyncBase sb) {
		// 默认的保存处理
		e = super.save(e);
		if (sb != null) {
			// 保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	public List<Case4InfractTraffic> doPatchSave(String syncIds) {
		List<Case4InfractTraffic> citList = new ArrayList<Case4InfractTraffic>();
		Case4InfractTraffic cit = null;

		// 遍历syncIds
		Long[] syncIdArray = StringUtils.stringArray2LongArray(syncIds
				.split(","));
		Long carId = null;
		for (Long syncId : syncIdArray) {
			SyncBase sb = this.syncBaseDao.load(syncId);
			cit = new Case4InfractTraffic();
			if (sb.getSyncType().equals(JinDunJTWF.KEY_TYPE)) { // 判断是否金盾网同步
				JinDunJTWF jinDunJTWF = this.jinDunJTWFDao.load(syncId);
				// 通过金断网交通违法并装交通违章对象
				carId = findCarId(jinDunJTWF.getCarPlateNo()); // 根据车牌查找carId;
				cit.setCaseNo(jinDunJTWF.getSyncCode());
				cit.setAddress(jinDunJTWF.getAddress());
				cit.setHappenDate(jinDunJTWF.getHappenDate());
				cit.setJeom(jinDunJTWF.getJeom());
				cit.setFrom("金盾网/" + jinDunJTWF.getSource());
			} else { // 交委同步

				JiaoWeiJTWF jiaoWeiJTWF = this.jiaoWeiJTWFDao.load(syncId);
				// 通过交委接口交通违法并装交通违章对象
				carId = findCarId(jiaoWeiJTWF.getCarPlateNo()); // 根据车牌查找carId;
				cit.setCaseNo(jiaoWeiJTWF.getSyncCode());
				cit.setSubject(jiaoWeiJTWF.getContent());
				cit.setJeom(jiaoWeiJTWF.getJeom());
				cit.setHappenDate(jiaoWeiJTWF.getHappenDate());
				cit.setFrom("交委/");
				// 设置金盾网违法地地址
				String address = this.jiaoWeiJTWFDao.findJinDunAddress(
						jiaoWeiJTWF.getSyncCode(), jiaoWeiJTWF.getCarPlateNo(),
						jiaoWeiJTWF.getHappenDate());
				if (address != null) {
					String[] vvs = address.split(";");
					cit.setAddress(vvs[0]);
				}

				/**
				 * TODO //根据违章顺序号查找金盾网交通违章记录
				 * if(jiaoWeiJTWF.getSyncCode().length() > 0){ JinDunJTWF
				 * jinDunJTWF =
				 * this.jinDunJTWFDao.findJinDunJTWFBySyscCode(jiaoWeiJTWF
				 * .getSyncCode()); if(null != jinDunJTWF &&
				 * jinDunJTWF.getAddress() != null &&
				 * jinDunJTWF.getAddress().trim().length() >
				 * 0){//判断此记录是否存在并且违章地点不为空
				 * cit.setAddress(jinDunJTWF.getAddress()); //设置违章地点 } }
				 **/
			}

			// 设置交通违法对象车辆相关信息
			Car car = carDao.load(carId);
			cit.setCarId(car.getId()); // 设置carId
			cit.setCarPlate(car.getPlate());
			cit.setMotorcadeId(car.getMotorcade().getId());
			cit.setMotorcadeName(car.getMotorcade().getName());

			// 设置来源
			cit.setSource(CaseBase.SOURCE_GENERATION);
			// 设置syncId
			cit.setSyncId(syncId);
			// 初始化信息
			cit.setUid(this.idGeneratorService
					.next(Case4InfractTraffic.ATTACH_TYPE));
			// 自动生成自编号
			cit.setCode(this.idGeneratorService
					.nextSN4Month(Case4InfractTraffic.KEY_CODE));
			cit.setType(CaseBase.TYPE_INFRACT_TRAFFIC);
			cit.setStatus(CaseBase.STATUS_ACTIVE);

			// 设置创建人信息和最后修改人信息
			SystemContext context = SystemContextHolder.get();
			cit.setAuthor(context.getUserHistory());
			cit.setFileDate(Calendar.getInstance());
			cit.setModifier(context.getUserHistory());
			cit.setModifiedDate(Calendar.getInstance());

			// 保存交通违章对象
			cit = super.save(cit);

			// 保存同步对象
			sb.setStatus(SyncBase.STATUS_GEN); // 设置同步对象的已生成
			this.syncBaseDao.save(sb);
			citList.add(cit);
		}
		return citList;
	}

	/** 根据车牌号查找carId */
	public Long findCarId(String carPlateNo) {
		Long carId = null;
		if (carPlateNo.length() > 0) { // 判断车牌号是否为空
			carId = this.carDao.findcarIdByCarPlateNo(carPlateNo);
		}
		return carId;
	}

	public String doStartFlow(String key, Long[] ids) {
		// 声明变量
		Map<String, Object> variables;
		Case4InfractTraffic case4InfractTraffic;
		// 声明返回的流程实例id 多个逗号隔开
		// String procInstIds = "";

		// 循环Id数组
		int i = 0;
		for (Long id : ids) {
			case4InfractTraffic = this.caseTrafficDao.load(id);
			variables = new HashMap<String, Object>();
			// 发起流程
			String procInstId = this.workflowService.startFlowByKey(key,
					this.returnParam(case4InfractTraffic, variables));
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
			case4InfractTraffic.setStatus(CaseBase.STATUS_HANDLING);
			this.caseTrafficDao.save(case4InfractTraffic);
			i++;
		}

		return String.valueOf(i);
	}

	// 返回的全局参数
	private Map<String, Object> returnParam(
			Case4InfractTraffic case4InfractTraffic,
			Map<String, Object> variables) {
		variables.put("case4InfractTraffic_id", case4InfractTraffic.getId());
		variables.put("case4InfractTraffic_carPlate",
				case4InfractTraffic.getCarPlate());
		variables.put("case4InfractTraffic_carId",
				case4InfractTraffic.getCarId());
		variables.put("case4InfractTrafficr_motorcadeName",
				case4InfractTraffic.getMotorcadeName());
		variables.put("motorcadeId", case4InfractTraffic.getMotorcadeId());
		// 查找分公司Id
		if (case4InfractTraffic.getMotorcadeId() != null) {
			Motorcade m = this.motorcadeDao.load(case4InfractTraffic
					.getMotorcadeId());
			variables.put("filialeId", m.getUnit().getId());
		}
		variables.put("case4InfractTrafficr_from", case4InfractTraffic
				.getFrom() != null ? case4InfractTraffic.getFrom() : "");
		variables
				.put("case4InfractTraffic_happenDate",
						case4InfractTraffic.getHappenDate().getTime() != null ? DateUtils
								.formatCalendar(
										case4InfractTraffic.getHappenDate(),
										"yyyy-MM-dd HH:mm") : "");
		variables.put("case4InfractTrafficr_address",
				case4InfractTraffic.getAddress());
		variables.put("case4InfractTrafficr_subject", case4InfractTraffic
				.getSubject() != null ? case4InfractTraffic.getSubject() : "");
		// 组装主题
		variables
				.put("subject",
						case4InfractTraffic.getCarPlate()
								+ "交通违法处理："
								+ (case4InfractTraffic.getSubject() != null ? case4InfractTraffic
										.getSubject() : ""));
		variables.put("case4InfractTrafficr_infractCode",
				case4InfractTraffic.getInfractCode());
		variables.put("case4InfractTrafficr_jeom",
				case4InfractTraffic.getJeom());
		variables.put("case4InfractTrafficr_penalty",
				case4InfractTraffic.getPenalty());

		return variables;
	}

	public String getCaseTrafficInfoByCarManId(Long carManId,
			Calendar happenDate) {
		String json = null;
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		CarMan carMan = this.carManDao.load(carManId);
		Calendar cert4DrivingFirstDate = carMan.getCert4DrivingFirstDate();
		// 判断违法信息所属的周期
		// 将初领驾驶证日期的年份设置为违法日期的年份
		cert4DrivingFirstDate.set(Calendar.YEAR, happenDate.get(Calendar.YEAR));
		if (happenDate.before(cert4DrivingFirstDate)) {
			startDate.set(Calendar.YEAR,
					cert4DrivingFirstDate.get(Calendar.YEAR) - 1);
			startDate.set(Calendar.MONTH,
					cert4DrivingFirstDate.get(Calendar.MONTH));
			startDate.set(Calendar.DAY_OF_MONTH,
					cert4DrivingFirstDate.get(Calendar.DAY_OF_MONTH));
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);

			endDate.set(Calendar.YEAR, cert4DrivingFirstDate.get(Calendar.YEAR));
			endDate.set(Calendar.MONTH,
					cert4DrivingFirstDate.get(Calendar.MONTH));
			endDate.set(Calendar.DAY_OF_MONTH,
					cert4DrivingFirstDate.get(Calendar.DAY_OF_MONTH) - 1);
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE, 59);
			endDate.set(Calendar.SECOND, 59);

		} else {
			startDate.set(Calendar.YEAR,
					cert4DrivingFirstDate.get(Calendar.YEAR));
			startDate.set(Calendar.MONTH,
					cert4DrivingFirstDate.get(Calendar.MONTH));
			startDate.set(Calendar.DAY_OF_MONTH,
					cert4DrivingFirstDate.get(Calendar.DAY_OF_MONTH));
			startDate.set(Calendar.HOUR_OF_DAY, 0);
			startDate.set(Calendar.MINUTE, 0);
			startDate.set(Calendar.SECOND, 0);

			endDate.set(Calendar.YEAR,
					cert4DrivingFirstDate.get(Calendar.YEAR) + 1);
			endDate.set(Calendar.MONTH,
					cert4DrivingFirstDate.get(Calendar.MONTH));
			endDate.set(Calendar.DAY_OF_MONTH,
					cert4DrivingFirstDate.get(Calendar.DAY_OF_MONTH) - 1);
			endDate.set(Calendar.HOUR_OF_DAY, 23);
			endDate.set(Calendar.MINUTE, 59);
			endDate.set(Calendar.SECOND, 59);

		}
		json = this.caseTrafficDao.getCaseTrafficInfoByCarManId(carManId,
				startDate, endDate);

		return json;
	}

	public void updateCaseTrafficInfo4Flow(Long id,
			Map<String, Object> attributes) {
		this.caseTrafficDao.updateCaseTrafficInfo4Flow(id, attributes);

	}
}