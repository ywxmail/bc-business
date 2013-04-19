/**
 * 
 */
package cn.bc.business.carPrepare.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carPrepare.dao.CarPrepareDao;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.carPrepare.domain.CarPrepareItem;
import cn.bc.business.motorcade.dao.MotorcadeDao;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.workflow.service.WorkflowService;

/**
 * 出车准备Service的实现
 * 
 * @author zxr
 */
public class CarPrepareServiceImpl extends DefaultCrudService<CarPrepare>
		implements CarPrepareService {
	private CarPrepareDao carPrepareDao;
	private TaskService taskService;
	private WorkflowService workflowService;
	// private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	// private AttachService attachService;// 附件服务
	private CarDao carDao;
	private MotorcadeDao motorcadeDao;

	public CarPrepareDao getCarPrepareDao() {
		return carPrepareDao;
	}

	public void setCarPrepareDao(CarPrepareDao carPrepareDao) {
		this.carPrepareDao = carPrepareDao;
		this.setCrudDao(carPrepareDao);
	}

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}

	// @Autowired
	// public void setIdGeneratorService(IdGeneratorService idGeneratorService)
	// {
	// this.idGeneratorService = idGeneratorService;
	// }
	//
	// @Autowired
	// public void setAttachService(AttachService attachService) {
	// this.attachService = attachService;
	// }

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

	public String doAnnualPlan(String plan4Year, String plan4Month) {
		Calendar planDate = Calendar.getInstance();
		// 车辆集合
		List<Map<String, Object>> carList = null;
		// 车辆信息
		Map<String, Object> carInfo = null;
		// 设置计划更新日期
		// 设置年份
		planDate.set(Calendar.YEAR, Integer.valueOf(plan4Year));
		// 如果月份不为空就设置月份
		if (plan4Month != null) {
			planDate.set(Calendar.MONTH, Integer.valueOf(plan4Month) - 1);
			carList = this.carDao.findRetiredCarsOfMonth(planDate, null,
					planDate);
		} else {
			carList = this.carDao.findRetiredCarsOfMonth(null, null, planDate);
		}
		// 组装数据
		SystemContext context = SystemContextHolder.get();
		// 返回页面提示信息
		JSONObject json = new JSONObject();
		// 重复的车牌
		List<JSONObject> carPrepareInfo = new ArrayList<JSONObject>();
		List<JSONObject> carPrepareInfoError = new ArrayList<JSONObject>();
		try {
			if (carList != null && carList.size() != 0) {
				// 已存在车辆更新计划信息的数目
				int n = 0;
				for (int i = 0; i < carList.size(); i++) {
					// 每辆车的信息
					JSONObject eachCar = new JSONObject();
					carInfo = carList.get(i);
					// 判断是否已经存在相同车牌的更新计划
					String plateNo = (carInfo.get("plateNo") != null ? carInfo
							.get("plateNo").toString() : "");
					String plateType = (carInfo.get("plateType") != null ? carInfo
							.get("plateType").toString() : "");
					String scrapto = (carInfo.get("scrapto") != null ? carInfo
							.get("scrapto").toString() : "");
					String certNo2 = (carInfo.get("certNo2") != null ? carInfo
							.get("certNo2").toString() : "");
					String scrapDate = (carInfo.get("scrapDate") != null ? carInfo
							.get("scrapDate").toString() : "");
					String bsType = (carInfo.get("bsType") != null ? carInfo
							.get("bsType").toString() : "");
					String company = (carInfo.get("company") != null ? carInfo
							.get("company").toString() : "");
					String commerialEndDate = (carInfo.get("commerialEndDate") != null ? carInfo
							.get("commerialEndDate").toString() : "");
					String ccEndDate = (carInfo.get("ccEndDate") != null ? carInfo
							.get("ccEndDate").toString() : "");
					String greenslipEndDate = (carInfo.get("greenslipEndDate") != null ? carInfo
							.get("greenslipEndDate").toString() : "");
					String registerDate = (carInfo.get("registerDate") != null ? carInfo
							.get("registerDate").toString() : "");
					String motorcadeName = (carInfo.get("motorcadeName") != null ? carInfo
							.get("motorcadeName").toString() : "");

					// 组装导入的详细信息
					eachCar.put("plateNo", plateNo);
					eachCar.put("plateType", plateType);
					eachCar.put("scrapto", scrapto);
					eachCar.put("certNo2", certNo2);
					eachCar.put("scrapDate", scrapDate);
					eachCar.put("bsType", bsType);
					eachCar.put("company", company);
					eachCar.put("commerialEndDate", commerialEndDate);
					eachCar.put("ccEndDate", ccEndDate);
					eachCar.put("greenslipEndDate", greenslipEndDate);
					eachCar.put("registerDate", registerDate);
					eachCar.put("motorcadeName", motorcadeName);

					// CarPrepare oldCarPrepare = null;
					CarPrepare oldCarPrepare = this
							.getCarPrepareByPlateTypeAndPlateNo(plateType,
									plateNo);
					// 如果为空就插入数据
					if (oldCarPrepare == null) {
						// 点击查看详情查看到的状态
						eachCar.put("status", "生成");
						carPrepareInfo.add(eachCar);
						CarPrepare carPrepare = new CarPrepare();
						Set<CarPrepareItem> carPrepareItems = new LinkedHashSet<CarPrepareItem>();
						carPrepare.setStatus(CarPrepare.STATUS_STAYUPDATED);
						carPrepare.setAuthor(context.getUserHistory());
						carPrepare.setFileDate(Calendar.getInstance());
						carPrepare.setModifier(context.getUserHistory());
						carPrepare.setModifiedDate(Calendar.getInstance());
						carPrepare.setUid(CarPrepare.KEY_UID);
						carPrepare.setC1Scrapto(scrapto);
						carPrepare.setC2PlateType("粤A");
						carPrepare.setCode(certNo2);
						carPrepare
								.setPlanDate(DateUtils.getCalendar(scrapDate));
						carPrepare.setC1PlateNo(plateNo);
						carPrepare.setC1PlateType(plateType);
						carPrepare.setC1BsType(bsType);
						carPrepare.setC1Company(company);
						carPrepare
								.setC1Motorcade(carInfo.get("motorcadeId") != null ? Integer
										.valueOf(carInfo.get("motorcadeId")
												.toString()) : null);
						carPrepare.setC1CommeriaEndDate(DateUtils
								.getCalendar(commerialEndDate));
						carPrepare.setC1ContractEndDate(DateUtils
								.getCalendar(ccEndDate));
						carPrepare.setC1GreenslipEndDate(DateUtils
								.getCalendar(greenslipEndDate));
						carPrepare.setC1RegisterDate(DateUtils
								.getCalendar(registerDate));
						// 进度
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "交车", null,
								CarPrepareItem.STATUS_UNFINISHED, 1);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "提车", null,
								CarPrepareItem.STATUS_UNFINISHED, 2);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "报停计价器", null,
								CarPrepareItem.STATUS_UNFINISHED, 3);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "收转篮报废凭证", null,
								CarPrepareItem.STATUS_UNFINISHED, 4);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "报停车", null,
								CarPrepareItem.STATUS_UNFINISHED, 5);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "办指标", null,
								CarPrepareItem.STATUS_UNFINISHED, 6);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "回指标", null,
								CarPrepareItem.STATUS_UNFINISHED, 7);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "新车上牌", null,
								CarPrepareItem.STATUS_UNFINISHED, 8);
						initializeCarPrepareItemInfo(carPrepare,
								carPrepareItems, "出车", null,
								CarPrepareItem.STATUS_UNFINISHED, 9);
						carPrepare.setCarPrepareItem(carPrepareItems);
						this.carPrepareDao.save(carPrepare);
					} else {
						// 插入的数目减一
						n++;
						// 点击查看详情查看到的状态
						eachCar.put("status", "已存在(忽略处理)");
						carPrepareInfoError.add(eachCar);
					}
				}

				json.put("success", true);
				// 查找的数据信息
				carPrepareInfoError.addAll(carPrepareInfo);
				json.put("detail", carPrepareInfoError);

				if (n == 0) {
					json.put("msg", "成功生成" + carList.size() + "辆车的更新计划！");
				} else {
					// 提示有重复车牌
					json.put("isUpdate", true);
					json.put(
							"msg",
							"有"
									+ carList.size()
									+ "辆车符合生成更新计划的条件，"
									+ (carList.size() - n != 0 ? "其中有"
											+ (carList.size() - n) + "已成功生成，有"
											: "") + n + "辆车的更新计划已存在(已忽略)");
				}
			} else {
				json.put("success", false);
				json.put("msg", "没有找到指定日期内需要更新的车辆信息！");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	public void initializeCarPrepareItemInfo(CarPrepare entity,
			Set<CarPrepareItem> carPrepareItems, String name, Calendar date,
			int staus, int order) {
		CarPrepareItem carPrepareItem = new CarPrepareItem();
		carPrepareItem.setCarPrepare(entity);
		carPrepareItem.setName(name);
		carPrepareItem.setDate(date);
		// carPrepareItem.setDesc(null);
		carPrepareItem.setOrder(order);
		carPrepareItem.setStatus(staus);
		carPrepareItems.add(carPrepareItem);
	}

	public CarPrepare getCarPrepareByPlateTypeAndPlateNo(String plateType,
			String plateNo) {
		return this.carPrepareDao.getCarPrepareByPlateTypeAndPlateNo(plateType,
				plateNo);
	}

	public String doStartFlow(String key, Long carPrepartId, CarPrepare e) {
		// 声明变量
		Map<String, Object> variables = new HashMap<String, Object>();
		// 先保存信息
		CarPrepare carPrepare = this.save(e);
		// 获取发起流程时所需要的信息
		// 更新车辆信息(完善车牌号码、管理号，自编号...等信息)并将状态设置为草稿
		Car car = this.carDao.load(carPrepare.getC2CarId());
		car.setPlateType(carPrepare.getC2PlateType());
		car.setPlateNo(carPrepare.getC2PlateNo());
		if (carPrepare.getModifier() != null) {
			Motorcade m = this.motorcadeDao.load(carPrepare.getC2Motorcade());
			car.setMotorcade(m);
		}
		car.setBusinessType(carPrepare.getC2BsType());
		car.setCode(carPrepare.getC2CarCode());
		car.setStatus(BCConstants.STATUS_DRAFT);
		car.setManageNo(carPrepare.getC2ManageNo());
		car.setOriginNo(carPrepare.getC1PlateNo());
		car.setCertNo2(carPrepare.getCode());
		car = this.carDao.save(car);
		// 发起流程
		String procInstId = this.workflowService.startFlowByKey(key,
				this.returnParam(carPrepare, car, variables));
		// 完成第一步办理
		Task task = this.taskService.createTaskQuery()
				.processInstanceId(procInstId).singleResult();
		this.workflowService.completeTask(task.getId());

		return procInstId;
	}

	// 返回的全局参数
	private Map<String, Object> returnParam(CarPrepare carPrepare, Car car,
			Map<String, Object> variables) {
		// 主题
		variables.put("subject", "关于" + carPrepare.getC2PlateType() + "."
				+ carPrepare.getC2PlateNo() + "的出车处理流程");
		// 车牌号码
		variables.put("plate_gl", carPrepare.getC2PlateType() + "."
				+ carPrepare.getC2PlateNo());
		variables.put("carId_gl", carPrepare.getC2CarId());
		// 车辆状态[草稿]
		variables.put("status4Car", String.valueOf(car.getStatus()));

		// 出车性质
		variables.put("carActiveType", carPrepare.getC2CarActiveType());
		// 营运性质
		variables.put("bsType",
				this.returnCarActiveBsType(carPrepare.getC2BsType()));
		// 司机1
		variables.put("driver1_gl", carPrepare.getC2Driver1());
		variables.put("driverId1_gl", carPrepare.getC2Driver1Id());
		variables
				.put("certFwzg4Driver1_gl", carPrepare.getC2CertFWZG4Driver1());
		variables.put("charger4driver1", carPrepare.getC2Nature4Driver1());
		// 司机2
		variables.put("driver2_gl", carPrepare.getC2Driver2());
		variables.put("driverId2_gl", carPrepare.getC2Driver2Id());
		variables
				.put("certFwzg4Driver2_gl", carPrepare.getC2CertFWZG4Driver2());
		variables.put("charger4driver2", carPrepare.getC2Nature4Driver2());
		// 管理号
		variables
				.put("manageNo_gl", String.valueOf(carPrepare.getC2ManageNo()));
		// 出车日期
		Set<CarPrepareItem> carPrepareItems = carPrepare.getCarPrepareItem();
		if (!carPrepareItems.isEmpty()) {
			Iterator<CarPrepareItem> n = carPrepareItems.iterator();
			while (n.hasNext()) {
				CarPrepareItem cp = n.next();
				if (cp.getName().equals("出车") || cp.getName().equals("迁入")) {
					variables.put("carActiveDate_gl", DateUtils.formatCalendar(
							cp.getDate(), "yyyy-MM-dd"));
				}
			}
		}

		// 分公司 //车队
		if (carPrepare.getC2Motorcade() != null) {
			Motorcade motorcade = this.motorcadeDao.load(carPrepare
					.getC2Motorcade());
			// 分公司
			variables.put("filialeId_gl", carPrepare.getC2Branch());
			variables.put("filiale_gl", motorcade.getUnit().getName());
			// 车队
			variables.put("motorcadeId", carPrepare.getC2Motorcade());
			variables.put("motorcadeName_gl", motorcade.getName());
		}
		return variables;
	}

	// 根据营运性质判断出车时的营运性质
	private String returnCarActiveBsType(String c2BsType) {
		// 如果营运性质为“挂靠合同”则返回为挂靠
		if (c2BsType.equals("挂靠合同")) {
			return "挂靠";
		} else if (c2BsType.equals("员工制") || c2BsType.equals("承包合同")
				|| c2BsType.equals("大包车") || c2BsType.equals("大包车SS")
				|| c2BsType.equals("中标车")) {
			// 如果营运性质为“员工制”“承包合同”“大包车”“大包车SS”“中标车”则返回为承包
			return "承包";
		} else {
			return "其他";
		}
	}

}