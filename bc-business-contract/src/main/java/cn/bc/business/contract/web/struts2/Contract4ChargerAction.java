/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.text.SimpleDateFormat;
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
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.domain.ContractFeeDetail;
import cn.bc.business.contract.service.Contract4ChargerService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 经济合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4ChargerAction extends
		FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	private Contract4ChargerService contract4ChargerService;
	// private Contract4LabourService contract4LabourService;
	private OptionService optionService;
	private Long carId;
	public Long driverId;
	public AttachWidget attachsUI;

	public Map<String, String> statusesValue;
	// public Map<String, String> chargerInfoMap; // 责任人Map
	public List<Map<String, String>> quittersList; // 可选提前终止方列表
	public List<Map<String, String>> signTypeList; // 可选签约类型
	public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> contractVersionNoList; // 可选合同版本号列表
	public List<Map<String, String>> paymentDates; // // 可选缴费日列表
	public String assignChargerIds; // 多个责任人Id
	public String assignChargerNames; // 多个责任人name
	public String[] chargerNameAry;
	public Map<String, Object> carInfoMap; // 车辆Map
	public boolean isExistContract; // 是否存在合同
	public boolean isDoMaintenance = false;// 是否进行维护操作
	public boolean scrapToPower;// 是否能看残值归属
	public String feeDetails;// 收费明细的json字符串
	public String price;// 金额

	public List<Map<String, String>> scrapToList; // 残值归属
	public List<Map<String, String>> carMaintainList; // 残值归属
	public List<Map<String, String>> chargersInfoList; // 司机责任人Map
	public List<Map<String, String>> payTypeList;// 收费方式列表(每月、每季、每年、一次性)
	public boolean canCopy;// 是否能复制

	public boolean isChange = false;// 经济合同的责任人是否变更
	public String contractFee4EndDate;// 相关的每月承包费(上一份合同结果时间段的)的结果日期
	public String contractFee4Price;// 相关的每月承包费(上一份合同结果时间段的)的金额
	public Long oldContractId; // 合同ID

	public Map<String, Object> contractFeeInfoMap; // 车辆Map

	@Autowired
	public void setContract4ChargerService(
			Contract4ChargerService contract4ChargerService) {
		this.contract4ChargerService = contract4ChargerService;
		this.setCrudService(contract4ChargerService);
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Override
	public boolean isReadonly() {
		// 经济合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		// 是否能读写经济合同残值归属管理角色
		scrapToPower = context.hasAnyRole(
				getText("key.role.bs.contract4charger.scrapTo"),
				getText("key.role.bc.admin"),
				getText("key.role.bs.contract4charger"),
				getText("key.role.bs.contract4charger.entering"));

		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bc.admin"));
	}

	public boolean isEntering() {
		// 经济合同草稿信息录入
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bs.contract4charger.entering"));
	}

	// 执行转车，续约，操作时需要的参数
	public Long contractId; // 合同ID
	public int opType; // 操作类型
	public String signType;
	public String stopDate;// 合同实际结束日期

	// 新建表单
	public String create() throws Exception {

		// 如果是转车操作就复制上一份合同
		if (opType == Contract.OPTYPE_CHANGECHARGER
				|| opType == Contract.OPTYPE_RENEW
				|| opType == Contract.OPTYPE_CHANGECHARGER2) {
			Contract4Charger newContract = this.contract4ChargerService
					.doCopyContract(contractId, this.opType, this.signType);

			// 初始化E
			this.setE(newContract);
			// 默认新合同的签订日期，合同始日期与上一份合同的结束日期相同
			Calendar signDate4Charger = DateUtils.getCalendar(this.stopDate);
			this.getE().setSignDate(signDate4Charger);
			this.getE().setStartDate(signDate4Charger);
			this.chargersInfoList = formatChargers(newContract.getExt_str2());
			carId = this.contract4ChargerService
					.findCarIdByContractId(newContract.getPid());
			// 标识是否执行续约，重发包，过户操作
			isChange = true;
			// 旧合同Id
			oldContractId = contractId;

		} else {
			// 初始化E
			this.setE(createEntity());

		}

		// 初始化表单的配置信息
		this.formPageOption = buildFormPageOption(true);

		// 初始化表单的其他配置
		this.initForm(true);

		this.afterCreate(this.getE());

		return "form";
	}

	@Override
	protected void afterCreate(Contract4Charger entity) {

		super.afterCreate(entity);
		if (carId != null) {
			// 查找此车辆是否存在经济合同,若存在前台提示
			// isExistContract =
			// this.contract4ChargerService.isExistContract(carId);
			// if(isExistContract == false){
			// 根据carId查找车辆的车牌号码
			carInfoMap = this.contract4ChargerService.findCarByCarId(carId);
			entity.setExt_str1(isNullObject(carInfoMap.get("plate_type") + "."
					+ carInfoMap.get("plate_no")));
			entity.setWordNo(isNullObject(carInfoMap.get("code")));// 车辆自编号
			entity.setBusinessType(isNullObject(carInfoMap.get("bs_type")));// 车辆自编号
			// }
		}

		if (driverId != null) {
			// 根据driverId查找车辆的车牌号码
			carInfoMap = this.contract4ChargerService
					.findCarByCarManId(driverId);
			if (carInfoMap != null && !carInfoMap.isEmpty()) {
				entity.setExt_str1(isNullObject(carInfoMap.get("plate_type")
						+ "." + carInfoMap.get("plate_no")));
				entity.setWordNo(isNullObject(carInfoMap.get("code")));
				entity.setBusinessType(isNullObject(carInfoMap.get("bs_type")));
				carId = Long.valueOf(isNullObject(carInfoMap.get("id")));
			}
		}
		// 经过过户，续约，生发包操作后的新建
		if (opType == Contract.OPTYPE_CHANGECHARGER
				|| opType == Contract.OPTYPE_RENEW
				|| opType == Contract.OPTYPE_CHANGECHARGER2) {
			// 提前终止合同方
			this.quittersList = quitterOptionItem(this.getE().getExt_str2());

			// 构建附件控件
			attachsUI = buildAttachsUI(false, false);

		} else {

			entity.setPatchNo(this.getIdGeneratorService().next(
					Contract4Charger.KEY_UID));
			entity.setOpType(Contract.OPTYPE_CREATE);
			entity.setMain(Contract.MAIN_NOW);
			entity.setVerMajor(Contract.MAJOR_DEFALUT);
			entity.setVerMinor(Contract.MINOR_DEFALUT);
			entity.setUid(this.getIdGeneratorService().next(
					Contract4Charger.KEY_UID));
			SimpleDateFormat format4month = new SimpleDateFormat("yyyyMM");
			entity.setCode("CLHT" + format4month.format(new Date()));
			entity.setType(Contract.TYPE_CHARGER);
			// entity.setStatus(Contract.STATUS_NORMAL);
			entity.setSignType(getText("contract4Charger.optype.create"));
			entity.setIncludeCost(true);
			// 提前终止方列表
			this.quittersList = new ArrayList<Map<String, String>>();
			// 构建附件控件
			attachsUI = buildAttachsUI(true, false);
		}

	}

	@Override
	protected void beforeSave(Contract4Charger entity) {
		super.beforeSave(entity);
		// 插入险种值
		try {
			Set<ContractFeeDetail> feeDetails = null;
			if (this.feeDetails != null && this.feeDetails.length() > 0) {
				feeDetails = new LinkedHashSet<ContractFeeDetail>();
				ContractFeeDetail resource;
				JSONArray jsons = new JSONArray(this.feeDetails);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new ContractFeeDetail();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setOrderNo(i);
					resource.setContract(this.getE());
					resource.setCode(json.getString("code"));
					resource.setSpec(json.getString("spec"));
					resource.setName(json.getString("name"));
					resource.setCount(Integer.parseInt(json.getString("count")));
					resource.setPayType(Integer.parseInt(json
							.getString("payType")));
					resource.setPrice(Float.parseFloat(json.getString("price")
							.replace(",", "")));
					if (json.getString("startDate") != null
							&& json.getString("startDate").length() > 0) {
						Calendar startDate = DateUtils.getCalendar(json
								.getString("startDate"));
						if (startDate != null) {
							resource.setStartDate(startDate);

						}
					}
					if (json.getString("endDate") != null
							&& json.getString("endDate").length() > 0) {
						Calendar endDate = DateUtils.getCalendar(json
								.getString("endDate"));
						if (endDate != null) {
							resource.setEndDate(endDate);
						}

					}

					resource.setDescription(json.getString("description"));
					feeDetails.add(resource);
				}
			}
			if (this.getE().getContractFeeDetail() != null) {
				this.getE().getContractFeeDetail().clear();
				this.getE().getContractFeeDetail().addAll(feeDetails);

			} else {
				this.getE().setContractFeeDetail(feeDetails);
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
	public String save() throws Exception {
		Json json = new Json();
		Contract4Charger e = this.getE();
		Long excludeId = null;
		// 保存之前检测自编号是否唯一:仅在新建时检测
		excludeId = this.contract4ChargerService.checkCodeIsExist(e.getId(),
				e.getCode());
		if (excludeId != null) {
			json.put("success", false);
			json.put("msg", getText("contract4Labour.code.exist2"));
			this.json = json.toString();
			return "json";
		} else {
			// 如果Pid不为空，且为新建的该合同执行的是过户或重发包或续约操作
			if (this.getE().isNew() && this.getE().getPid() != null) {
				// 设置最后更新人的信息
				SystemContext context = this.getSystyemContext();
				// 设置创建人
				e.setAuthor(context.getUserHistory());
				e.setFileDate(Calendar.getInstance());

				// 获取旧合同id
				Long fromContractId = e.getPid();
				Contract newContract = null;
				String msg = "";
				json = new Json();
				// 执行基类的保存
				this.beforeSave(e);

				newContract = this.contract4ChargerService.doOperate(carId,
						this.getE(), assignChargerIds, fromContractId,
						this.stopDate);

				if (e.getOpType() == Contract.OPTYPE_RENEW) {// 续约
					msg = getText("contract4Charger.renew.success");
				} else if (e.getOpType() == Contract.OPTYPE_CHANGECHARGER) {// 过户
					msg = getText("contract4Charger.changeCharger.success");
				} else if (e.getOpType() == Contract.OPTYPE_CHANGECHARGER2) {// 重发包
					msg = getText("contract4Charger.changeCharger2.success");
				}
				json.put("id", newContract.getId());
				json.put("oldId", fromContractId);
				json.put("success", true);
				json.put("msg", msg);
				this.json = json.toString();
				return "json";

			} else {
				// 普通保存

				// 执行基类的保存
				this.beforeSave(e);
				// 将上一份经济合同的实际结束日期保存起来
				if (e.getStatus() == BCConstants.STATUS_DRAFT
						&& this.stopDate.length() != 0) {
					e.setExt_str3(this.stopDate);
				}
				// 设置最后更新人的信息
				SystemContext context = this.getSystyemContext();
				e.setModifier(context.getUserHistory());
				e.setModifiedDate(Calendar.getInstance());
				// 获取责任人ID
				assignChargerIds = getChargerIds(this.getE().getExt_str2());
				this.contract4ChargerService.save(e, this.getCarId(),
						assignChargerIds, null);

				this.afterSave(e);

				json.put("id", e.getId());
				json.put("success", true);
				json.put("msg", getText("form.save.success"));
				this.json = json.toString();
				return "json";
			}
		}

	}

	// ---------------------入库开始--------------------------
	public String carMansId;// 入库时要查验是否为入库状态的责任人ID
	public Long draftCarId;// 未入库的车辆Id
	public String draftCarManId;// 未入库的司机Id
	public String draftcarByDriverHistoryId;// 未入库的迁移记录id

	/**
	 * 入库
	 * 
	 * @return
	 */
	public String warehousing() {
		this.beforeSave(this.getE());
		SystemContext context = this.getSystyemContext();
		// 设置创建人
		this.getE().setAuthor(context.getUserHistory());
		this.getE().setFileDate(Calendar.getInstance());
		// 如果是新建将实际停保日期设置到冗余字段
		if (this.getE().isNew() || this.stopDate.length() > 0) {
			this.getE().setExt_str3(this.stopDate);
		}
		carMansId = this.getChargerIds(this.getE().getExt_str2());

		this.json = this.contract4ChargerService.doWarehousing(carId,
				carMansId, this.getE(),
				(draftCarId != null ? draftCarId : null),
				(draftCarManId != null ? draftCarManId : null),
				(draftcarByDriverHistoryId != null ? draftcarByDriverHistoryId
						: null));

		return "json";
	}

	// ---------------------入库结束---------------------
	@Override
	protected void afterEdit(Contract4Charger e) {
		// == 合同维护处理
		// DecimalFormat format = new DecimalFormat("###,##0.00");
		// this.price=format.format(this.getE().getContractFeeDetail().);
		// 初始化责任人
		this.chargersInfoList = formatChargers(e.getExt_str2());
		// 提前终止合同方
		// this.quittersList = quitterOptionItem(e.getExt_str2());
		if (BCConstants.STATUS_DRAFT == this.getE().getStatus()) {
			this.quittersList = quitterOptionItem(e.getExt_str2());
		} else {
			this.quittersList = this.contract4ChargerService
					.getNormalChargerAndDriverByContractId(this.getE().getId());
		}
		// 根据合同获取车辆Id
		carId = this.contract4ChargerService.findCarIdByContractId(e.getId());
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);

		// 将次版本号加1
		if (e.getVerMinor() != null) {
			e.setVerMinor(e.getVerMinor() + 1);
		}

		// 操作类型设置为维护
		// 如果是草稿状态下打开，操作类型保持为原来的操作类型
		if (e.getStatus() != BCConstants.STATUS_DRAFT) {
			e.setOpType(Contract.OPTYPE_MAINTENANCE);
		}
	}

	@Override
	protected void afterOpen(Contract4Charger e) {
		// == 合同维护处理
		// 初始化责任人
		this.chargersInfoList = formatChargers(e.getExt_str2());
		// this.quittersList = quitterOptionItem(e.getExt_str2());
		this.quittersList = this.contract4ChargerService
				.getNormalChargerAndDriverByContractId(this.getE().getId());
		// 根据合同获取车辆Id
		carId = this.contract4ChargerService.findCarIdByContractId(e.getId());
		// setChargerList(e);
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 收费方式
		this.payTypeList = this.getPayWay();

		// 状态列表
		statusesValue = this.getBSStatuses3();
		// 表单可选项的加载
		initSelects();
	}

	/**
	 * 组装责任人姓名
	 * 
	 * @param drivers
	 * @return
	 */
	public List<Map<String, String>> formatChargers(String chargersStr) {
		String name = "";
		String id = "";
		List<Map<String, String>> chargersInfo = new ArrayList<Map<String, String>>();
		Map<String, String> charger;
		if (null != chargersStr && chargersStr.trim().length() > 0) {
			String[] chargerAry = chargersStr.split(";");
			for (int i = 0; i < chargerAry.length; i++) {
				charger = new HashMap<String, String>();
				name = chargerAry[i].split(",")[0];
				id = chargerAry[i].split(",")[1];
				charger.put("id", id);
				charger.put("name", name);
				chargersInfo.add(charger);
			}

		}
		return chargersInfo;
	}

	/**
	 * 组装责任人下拉列表
	 * 
	 * @param drivers
	 * @return
	 */
	public List<Map<String, String>> quitterOptionItem(String chargersStr) {
		String name = "";
		String id = "";
		List<Map<String, String>> chargersInfo = new ArrayList<Map<String, String>>();
		Map<String, String> charger;
		if (null != chargersStr && chargersStr.trim().length() > 0) {
			String[] chargerAry = chargersStr.split(";");
			for (int i = 0; i < chargerAry.length; i++) {
				charger = new HashMap<String, String>();
				name = chargerAry[i].split(",")[0];
				id = chargerAry[i].split(",")[1];
				charger.put("id", id);
				charger.put("name", name);
				chargersInfo.add(charger);
			}

		}
		return chargersInfo;
	}

	/**
	 * 获取责任人的id
	 * 
	 * @param chargersStr
	 * @return
	 */
	public String getChargerIds(String chargersStr) {
		String ids = "";
		if (null != chargersStr && chargersStr.trim().length() > 0) {
			String[] chargerAry = chargersStr.split(";");
			for (int i = 0; i < chargerAry.length; i++) {
				if (i > 0) {
					ids = ids + ",";
				}
				ids = ids + chargerAry[i].split(",")[1];

			}

		}

		return ids;
	}

	/** 判断指定的车辆是否已经存在经济合同 */
	public String isExistContract() {
		Json json = new Json();
		json.put("isExistContract",
				this.contract4ChargerService.isExistContract(carId));
		this.json = json.toString();
		return "json";
	}

	// ========判断经济合同自编号唯一代码开始========
	private Long excludeId;
	private String code;

	public Long getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(Long excludeId) {
		this.excludeId = excludeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String checkCodeIsExist() {
		Json json = new Json();
		Long excludeId = this.contract4ChargerService.checkCodeIsExist(
				this.excludeId, this.code);
		if (excludeId != null) {
			json.put("id", excludeId);
			json.put("isExist", "true"); // 存在重复自编号
			json.put("msg", getText("contract4Labour.code.exist"));
		} else {
			json.put("isExist", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// ========判断经济合同自编号唯一代码结束========

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Contract4Charger.ATTACH_TYPE;
		String puid = this.getE().getUid();
		boolean readonly = forceReadonly ? true : (this.isEntering()
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT ? false
				: this.isReadonly());
		AttachWidget attachsUI = this.buildAttachsUI(isNew, readonly, ptype,
				puid);

		// 自定义附件总控制按钮
		if (!attachsUI.isReadOnly()) {
			attachsUI.addHeadButton(AttachWidget.createButton("添加模板", null,
					"bc.contract4ChargerForm.addAttachFromTemplate", null));// 添加模板
		}
		attachsUI.addHeadButton(AttachWidget
				.defaultHeadButton4DownloadAll(null));// 打包下载
		if (!attachsUI.isReadOnly()) {
			attachsUI.addHeadButton(AttachWidget
					.defaultHeadButton4DeleteAll(null));// 删除
		}

		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {

		PageOption pageOption = new PageOption().setWidth(740).setMinWidth(250);

		if (this.useFormPrint())
			pageOption.setPrint("default.form");

		// 只有可编辑表单才按权限配置，其它情况一律配置为只读状态
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.put("readonly", readonly);
			// 如果有录入权限且状态为草稿的可以进行修改
		} else if (this.getE().getStatus() == BCConstants.STATUS_DRAFT
				&& this.isEntering()) {
			pageOption.put("readonly", false);
		} else {
			pageOption.put("readonly", true);
		}
		// 是否能复制
		if ((editable && !this.isReadonly()) || this.isEntering()) {
			canCopy = false;

		} else {
			canCopy = true;
		}

		// 添加按钮
		buildFormPageButtons(pageOption, editable);

		return pageOption;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 帮助信息
		pageOption.setHelp("jingjihetong");
		// 特殊处理的部分
		if (!this.isReadonly()) {// 有权限
			if (this.getE().getStatus() != BCConstants.STATUS_DRAFT
					&& (opType != Contract.OPTYPE_RENEW
							&& opType != Contract.OPTYPE_CHANGECHARGER && opType != Contract.OPTYPE_CHANGECHARGER2)) {// 编辑状态显示保存按钮
				// 非草稿状态和操作状态下双击
				if (!isDoMaintenance
						&& this.getE().getMain() == Contract.MAIN_NOW
						&& this.getE().getStatus() != Contract.STATUS_LOGOUT) {
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.maintenance"),
							null, "bc.contract4ChargerForm.doMaintenance"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Labour.optype.renew"), null,
							"bc.contract4ChargerForm.doRenew"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.optype.changeCharger"),
							null, "bc.contract4ChargerForm.doChangeCharger"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.optype.changeCharger2"),
							null, "bc.contract4ChargerForm.doChangeCharger2"));
					pageOption.addButton(new ButtonOption(
							getText("contract4Charger.logout"), null,
							"bc.contract4ChargerForm.doLogout"));
				}
			} else {
				// 经过续约，过户，重发包后
				// pageOption.addButton(new ButtonOption(
				// getText("label.save4Draft"), null,
				// "bc.contract4ChargerForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.warehousing"), null,
						"bc.contract4ChargerForm.warehousing"));

			}
			// 维护操作
			if (isDoMaintenance) {
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.contract4ChargerForm.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.saveAndClose"), null,
						"bc.contract4ChargerForm.saveAndClose"));

			}
		}
		// 如果有录入权限的就有保存按钮
		if ((this.isEntering() || !isReadonly())
				&& this.getE().getStatus() == BCConstants.STATUS_DRAFT) {
			pageOption.addButton(new ButtonOption(getText("label.save4Draft"),
					null, "bc.contract4ChargerForm.save"));

		}

	}

	@Override
	protected Contract4Charger createEntity() {
		Contract4Charger c = super.createEntity();
		c.setStatus(BCConstants.STATUS_DRAFT);
		return c;
	}

	// 表单可选项的加载
	public void initSelects() {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findActiveOptionItemByGroupKeys(new String[] {
						OptionConstants.CONTRACT_SIGNTYPE,
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CONTRACT_VERSION_NO,
						OptionConstants.MOTORCADE_PAYMENT_DATE,
						OptionConstants.CONTRACT4CHARGER_SCRAPTO,
						OptionConstants.CONTRACT4CHARGER_CARMAINTAIN });

		// 加载可选签约类型
		this.signTypeList = optionItems.get(OptionConstants.CONTRACT_SIGNTYPE);

		// 因原旧bc数据经济合同的签约类型有"续签",新bc已经将签约类型为"续签"的数据统一改为"续约".注释于2012.9.7 何智
		// if (!this.getE().isNew()
		// && this.getE().getSignType()
		// .equals(getText("contract4Labour.optype.renew"))) {
		// OptionItem.insertIfNotExist(signTypeList,
		// getText("contract4Labour.optype.renew"),
		// getText("contract4Labour.optype.renew"));
		// }

		// 加载可选营运性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选合同版本号列表
		this.contractVersionNoList = optionItems
				.get(OptionConstants.CONTRACT_VERSION_NO);
		// 加载缴费日列表
		this.paymentDates = optionItems
				.get(OptionConstants.MOTORCADE_PAYMENT_DATE);
		// 经济合同残值归属列表
		this.scrapToList = optionItems
				.get(OptionConstants.CONTRACT4CHARGER_SCRAPTO);
		// 经济合同车辆包修列表
		this.carMaintainList = optionItems
				.get(OptionConstants.CONTRACT4CHARGER_CARMAINTAIN);

		// 非新建状态插入不存在的值
		Contract4Charger e = this.getE();
		if (!e.isNew()) {
			OptionItem.insertIfNotExist(signTypeList, null, e.getSignType());// 签约类型
			OptionItem.insertIfNotExist(businessTypeList, null,
					e.getBusinessType());// 营运性质
			OptionItem.insertIfNotExist(paymentDates, e.getPaymentDate(),
					e.getPaymentDate());// 缴费日
			OptionItem.insertIfNotExist(contractVersionNoList,
					e.getContractVersionNo(), e.getContractVersionNo());// 合同版本号
			OptionItem.insertIfNotExist(scrapToList, null, e.getScrapTo());// 残值归属
			OptionItem.insertIfNotExist(carMaintainList, null,
					e.getCarMaintain());// 车辆包修
		}
	}

	/**
	 * 获取Contract的合同类型列表
	 * 
	 * @return
	 */
	protected Map<String, String> getEntityTypes() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.TYPE_LABOUR),
				getText("contract.select.labour"));
		types.put(String.valueOf(Contract.TYPE_CHARGER),
				getText("contract.select.charger"));
		return types;
	}

	public String isNullObject(Object obj) {
		if (null != obj) {
			return obj.toString();
		} else {
			return "";
		}
	}

	private List<Map<String, String>> getPayWay() {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		list.add(this.getListsKeyAndValue(ContractFeeDetail.PAY_TYPE_MONTH,
				getText("feeTemplate.payType.month")));
		list.add(this.getListsKeyAndValue(ContractFeeDetail.PAY_TYPE_SEASON,
				getText("feeTemplate.payType.season")));
		list.add(this.getListsKeyAndValue(ContractFeeDetail.PAY_TYPE_YEAR,
				getText("feeTemplate.payType.year")));
		list.add(this.getListsKeyAndValue(ContractFeeDetail.PAY_TYPE_ALL,
				getText("feeTemplate.payType.all")));
		return list;
	}

	// 生成OptionItem列表 key、value值
	private Map<String, String> getListsKeyAndValue(Object key, Object value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key.toString());
		map.put("value", value.toString());
		return map;
	}

	// 从模板添加附件
	@Override
	protected Attach buildAttachFromTemplate() throws Exception {
		return this.contract4ChargerService.doAddAttachFromTemplate(
				this.getId(), this.tpl);
	}

	// 根据车辆Id获取在案的正副班司机数量
	public String getDriverAmount() {
		if (carId != null) {
			this.json = String.valueOf(this.contract4ChargerService
					.getDriverAmount(carId));
		} else {
			this.json = String.valueOf(0);
		}
		return "json";
	}

	/**
	 * 根据结束日期获取相关承包款的信息
	 * 
	 * @return
	 */
	public String getContractFeeInfoByEndDate() {
		Json json = new Json();
		// 如果是进行重发包或过户操作，需要获取上份合同相关的每月基准承包费信息
		// 根据结束日期获取相关承包款的信息
		if (this.contractId != null) {
			contractFeeInfoMap = this.contract4ChargerService
					.getContractFeeInfoMapByEndDate(this.stopDate, contractId);
			if (contractFeeInfoMap != null) {
				contractFee4EndDate = isNullObject(contractFeeInfoMap
						.get("end_date"));
				contractFee4Price = isNullObject(contractFeeInfoMap
						.get("price"));
				json.put("contractFee4EndDate", contractFee4EndDate);
				json.put("contractFee4Price", contractFee4Price);
				this.json = json.toString();
			} else {
				this.json = "";
			}
		} else {
			this.json = "";
		}

		return "json";

	}

	// 查看车辆或司机的状态
	public String str2;

	public String checkDriverOrCarStatus() {
		if (carId != null && (str2 != null || str2.length() > 0)) {
			carMansId = this.getChargerIds(str2);
			this.json = this.contract4ChargerService.checkDriverOrCarStatus(
					carId, carMansId);
		} else {
			json = "";
		}
		return "json";
	}
}