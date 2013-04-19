/**
 * 
 */
package cn.bc.business.carPrepare.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.carPrepare.domain.CarPrepareItem;
import cn.bc.business.carPrepare.service.CarPrepareService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.workflow.service.WorkflowModuleRelationService;

/**
 * 出车准备Action
 * 
 * @author zxr
 * 
 */
@SuppressWarnings("unused")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarPrepareAction extends FileEntityAction<Long, CarPrepare> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public OptionService optionService;
	public CarPrepareService carPrepareService;
	private MotorcadeService motorcadeService;
	private ActorService actorService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	private AttachService attachService;
	public CarService carService;
	public AttachWidget attachsUI;
	public Map<String, String> statusesValue;
	public List<Map<String, String>> businessTypeList; // 合同性质列表
	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> unitsList; // 可选分公司列表
	public List<Map<String, String>> scrapToList; // 残值归属
	public List<Map<String, String>> carVinList; // 车架号列表
	public JSONArray companyNames; // 公司名称列表
	public String carPrepareItems;// 车辆更新进度项目
	public String plan4Year;// 根据年度来生成车辆更新计划
	public String plan4Month;// 根据月度来生成车辆更新计划
	public String company;// 所属公司
	public String plateType;// 车牌类型
	public String plateNo;// 车牌号码

	public List<Map<String, Object>> workflowModuleRelations; // 工作流程集合

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setCarPrepareService(CarPrepareService carPrepareService) {
		this.carPrepareService = carPrepareService;
		this.setCrudService(carPrepareService);
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}

	@Override
	public boolean isReadonly() {
		// 出车准备管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carPrepare"),
				getText("key.role.bc.admin"));
	}

	/**
	 * 创建生成年度计划输入年份与月份的对话框
	 * 
	 * @return
	 */
	public String createPlanDateDialog() {
		return "success";
	}

	/**
	 * 生成年度车辆更新计划
	 * 
	 * @return
	 */
	public String createAnnualPlan() {
		// 判断月度计划是否为空
		if (plan4Month != null && plan4Month.length() != 0) {
			this.json = this.carPrepareService.doAnnualPlan(plan4Year,
					plan4Month);
		} else {
			this.json = this.carPrepareService.doAnnualPlan(plan4Year, null);
		}
		this.json = json.toString();
		return "json";
	}

	@Override
	protected void beforeSave(CarPrepare entity) {
		super.beforeSave(entity);
		// 插入进度项目
		try {
			Set<CarPrepareItem> carPrepareItems = null;
			String CarPrepareItemStr = "";
			if (this.carPrepareItems != null
					&& this.carPrepareItems.length() > 0) {
				carPrepareItems = new LinkedHashSet<CarPrepareItem>();
				CarPrepareItem resource;
				JSONArray jsons = new JSONArray(this.carPrepareItems);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new CarPrepareItem();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setOrder(i);
					resource.setCarPrepare(entity);
					resource.setName(json.getString("name"));
					if (json.getString("date") != null
							&& json.getString("date").length() > 0) {
						Calendar date = DateUtils.getCalendar(json
								.getString("date"));
						if (date != null) {
							resource.setDate(date);
						}
					}
					resource.setStatus(json.getInt("status"));
					resource.setDesc(json.getString("desc"));
					carPrepareItems.add(resource);
				}
			}
			if (this.getE().getCarPrepareItem() != null) {
				this.getE().getCarPrepareItem().clear();
				this.getE().getCarPrepareItem().addAll(carPrepareItems);
				this.getE().setCarPrepareItem(carPrepareItems);
			} else {
				this.getE().setCarPrepareItem(carPrepareItems);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void afterCreate(CarPrepare entity) {
		super.afterCreate(entity);
		// 设置车牌类型
		entity.setC1PlateType(getText("carPrepare.plate.type"));
		entity.setC2PlateType(getText("carPrepare.plate.type"));
		// 新建时初始化更新进度
		Set<CarPrepareItem> carPrepareItems = new LinkedHashSet<CarPrepareItem>();
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "交车", null, CarPrepareItem.STATUS_UNFINISHED,
				1);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "提车", null, CarPrepareItem.STATUS_UNFINISHED,
				2);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "报停计价器", null,
				CarPrepareItem.STATUS_UNFINISHED, 3);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "收转篮报废凭证", null,
				CarPrepareItem.STATUS_UNFINISHED, 4);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "报停车", null, CarPrepareItem.STATUS_UNFINISHED,
				5);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "办指标", null, CarPrepareItem.STATUS_UNFINISHED,
				6);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "回指标", null, CarPrepareItem.STATUS_UNFINISHED,
				7);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "新车上牌", null,
				CarPrepareItem.STATUS_UNFINISHED, 8);
		this.carPrepareService.initializeCarPrepareItemInfo(entity,
				carPrepareItems, "出车", null, CarPrepareItem.STATUS_UNFINISHED,
				9);
		entity.setCarPrepareItem(carPrepareItems);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		Date startTime = new Date();
		statusesValue = this.getBSStatuses1();
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CAR_COMPANY,
						OptionConstants.COMPANY_NAME,
						OptionConstants.CONTRACT4CHARGER_SCRAPTO });
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();

		// 加载可选合同性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 公司名称列表
		this.companyNames = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.COMPANY_NAME));

		// 分公司
		unitsList = this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT },
				new Integer[] { BCConstants.STATUS_ENABLED });
		// 经济合同残值归属列表
		this.scrapToList = optionItems
				.get(OptionConstants.CONTRACT4CHARGER_SCRAPTO);

		// 所属公司列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
		// OptionItem.insertIfNotExist(companyList, null,
		// getE().getC1Company());
		// 车架号列表
		if (this.getE().getC2Company() != null
				&& this.getE().getC2Company().length() != 0) {
			this.carVinList = carService.findCarVin4Option(
					new Integer[] { Car.CAR_STAUTS_NEWBUY }, this.getE()
							.getC2Company(), true);
		} else {
			this.carVinList = carService.findCarVin4Option(
					new Integer[] { Car.CAR_STAUTS_NEWBUY }, null, true);
		}
		if (this.getE().getC2Vin() != null) {
			Map<String, String> map = new HashMap<String, String>();
			String vinValue = this.getE().getC2Vin();
			map.put("key", String.valueOf(this.getE().getC2CarId()));
			map.put("value", vinValue);
			carVinList.add(map);
		}
		// 与模块相关的流程
		if (!this.getE().isNew()) {
			// 车牌号码
			List<String> plate = new ArrayList<String>();
			CarPrepare e = this.getE();
			// 旧车牌号码
			if (e.getC1PlateNo() != null && e.getC1PlateNo().length() != 0) {
				plate.add(e.getC1PlateType() + "." + e.getC1PlateNo());
			}
			// 新车牌号码
			if (e.getC2PlateNo() != null && e.getC2PlateNo().length() != 0) {
				plate.add(e.getC2PlateType() + "." + e.getC2PlateNo());
			}

			workflowModuleRelations = this.workflowModuleRelationService
					.findList(new String[] { "CarRetired", "CarActive" },
							new String[] { "plate", "plate_gl" },
							plate.toArray(new String[plate.size()]),
							new String[] { "subject" });
		}

	}

	// 根据公司获取可用的车架号
	public String getVinByCompany() {
		JSONObject json = new JSONObject();
		// 车架号列表
		this.carVinList = carService.findCarVin4Option(
				new Integer[] { Car.CAR_STAUTS_NEWBUY }, this.company, true);
		try {
			if (carVinList != null && carVinList.size() != 0) {
				json.put("success", true);
				json.put("carVinList", new JSONArray(this.carVinList));
			} else {
				json.put("success", false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.json = json.toString();
		return "json";
	}

	// 根据根据根据车牌号码检查是否已存在更新记录
	public String isExistingCarPrepare() {
		JSONObject json = new JSONObject();
		try {
			if (plateType != null && plateNo != null) {
				CarPrepare c = this.carPrepareService
						.getCarPrepareByPlateTypeAndPlateNo(plateType, plateNo);
				if (c != null) {
					if (this.getId() == null) {
						json.put("success", false);
						json.put("msg", "已经存在" + plateType + "." + plateNo
								+ "的车辆更新信息，不能再创建！");
					} else {
						if (this.getId().equals(c.getId())) {
							json.put("success", true);
						} else {
							json.put("success", false);
							json.put("msg", "已经存在" + plateType + "." + plateNo
									+ "的车辆更新信息，不能再新建！");
						}
					}
				} else {
					json.put("success", true);
				}
			} else {
				json.put("success", false);
				json.put("msg", "旧车车牌信息不全！");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		this.json = json.toString();
		return "json";
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {

		return super.buildFormPageOption(editable).setWidth(705)
				.setMinWidth(300).setHeight(540).setMinHeight(300);
	}

	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		// 添加默认的保存按钮
		pageOption.addButton(new ButtonOption(getText("label.save"), null,
				"bs.carPrepareForm.save"));
		pageOption.addButton(new ButtonOption(getText("label.saveAndClose"),
				null, "bs.carPrepareForm.saveAndClose"));
	}

	// ---发起流程---开始---
	public String tdIds;

	public String startFlow() {
		Json json = new Json();
		CarPrepare e = this.getE();
		this.beforeSave(e);
		String procInstId = this.carPrepareService.doStartFlow(
				getText("carPrepare.startFlow.carActive"), e.getId(), e);
		if (procInstId != null && procInstId.length() != 0) {
			json.put("success", true);
			json.put("msg", getText("成功发起驾驶员出车处理流程！"));

		} else {
			json.put("success", false);
			json.put("msg", getText("驾驶员出车处理流程发起失败！"));
		}
		this.json = json.toString();
		return "json";
	}

	// ---发起流程结束---

}