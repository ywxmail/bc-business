/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Charger;
import cn.bc.business.contract.service.Contract4ChargerService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
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
public class Contract4ChargerOperate2Action extends
		FileEntityAction<Long, Contract4Charger> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	private Contract4ChargerService contract4ChargerService;
	// private Contract4LabourService contract4LabourService;
	private AttachService attachService;
	private OptionService optionService;
	private Long carId;
	public Long driverId;
	public AttachWidget attachsUI;

	public Map<String, String> statusesValue;
	public Map<String, String> chargerInfoMap; // 责任人Map

	public List<Map<String, String>> signTypeList; // 可选签约类型
	public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> contractVersionNoList; // 可选合同版本号列表
	public List<Map<String, String>> paymentDates; // // 可选缴费日列表
	public String assignChargerIds; // 多个责任人Id
	public String assignChargerNames; // 多个责任人name
	public String[] chargerNameAry;
	public Map<String, Object> carInfoMap; // 车辆Map
	public boolean isExistContract; // 是否存在合同
	public Json json;

	@Autowired
	public void setContract4ChargerService(
			Contract4ChargerService contract4ChargerService) {
		this.contract4ChargerService = contract4ChargerService;
		this.setCrudService(contract4ChargerService);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
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
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bc.admin"));
	}

	/**
	 * 设置责任人姓名
	 * 
	 * @param assignChargerIds
	 * @param assignChargerNames
	 * @return
	 */
	private String setChargerName(String assignChargerIds,
			String assignChargerNames) {
		String chargerName = "";
		if (assignChargerIds.length() > 0) {
			String[] ids = assignChargerIds.split(",");
			String[] names = assignChargerNames.split(",");
			for (int i = 0; i < ids.length; i++) { // 设置责任人如:姓名1,id1;姓名2,id2;
				chargerName += names[i] + ",";
				chargerName += ids[i] + ";";
			}
		}
		return chargerName;
	}

	// === 经济合同续约,过户,重发包 代码开始 ====//
	private Long id;
	private String signType;
	private int opType;
	private Boolean isDisableSignType;
	public String stopDate;// 合同实际结束日期

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public int getOpType() {
		return opType;
	}

	public void setOpType(int opType) {
		this.opType = opType;
	}

	public Boolean getIsDisableSignType() {
		return isDisableSignType;
	}

	public void setIsDisableSignType(Boolean isDisableSignType) {
		this.isDisableSignType = isDisableSignType;
	}

	private Boolean isSaved; // 是否已保存

	public Boolean getIsSaved() {
		return isSaved;
	}

	public void setIsSaved(Boolean isSaved) {
		this.isSaved = isSaved;
	}

	@Override
	public String create() throws Exception {

		Contract4Charger newContract = this.contract4ChargerService
				.doCopyContract(this.id, this.opType, this.signType);

		// 初始化E
		this.setE(newContract);

		// 默认新合同的签订日期，合同始日期与上一份合同的结束日期相同
		Calendar signDate4Charger = DateUtils.getCalendar(this.stopDate);
		this.getE().setSignDate(signDate4Charger);
		this.getE().setStartDate(signDate4Charger);

		this.formPageOption = buildFormPageOption(true);

		// 设置责任人列表信息
		setChargerList();
		// 操作保存是否成功标识
		isSaved = false;
		// 设置附件
		attachsUI = buildAttachsUI(false, false);

		// 初始化表单的其他配置
		this.initForm(true);

		return "form";
	}

	@Override
	public String save() throws Exception {

		// 合同实际结束日期
		Calendar stopDate4Charger = DateUtils.getCalendar(this.stopDate);
		json = new Json();
		Contract4Charger e = this.getE();
		Long excludeId = null;
		// 保存之前检测自编号是否唯一:仅在新建时检测
		excludeId = this.contract4ChargerService.checkCodeIsExist(e.getId(),
				e.getCode());
		if (excludeId != null) {
			json.put("success", false);
			json.put("msg", getText("contract4Labour.code.exist2"));
			return "json";
		}

		// 设置最后更新人的信息
		SystemContext context = this.getSystyemContext();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		// 设置责任人姓名
		e.setExt_str2(setChargerName(assignChargerIds, assignChargerNames));

		if (isSaved) {// 普通保存
			// 执行基类的保存
			this.beforeSave(e);

			this.contract4ChargerService.save(e, this.getCarId(),
					assignChargerIds, assignChargerNames);

			this.afterSave(e);
			json.put("id", e.getId());
			json.put("success", true);
			json.put("msg", getText("form.save.success"));
			return "json";
		} else {// 操作保存

			// 设置创建人
			e.setAuthor(context.getUserHistory());
			e.setFileDate(Calendar.getInstance());

			// 获取旧合同id
			Long fromContractId = e.getPid();
			Contract newContract = null;
			String msg = "";
			json = new Json();

			newContract = this.contract4ChargerService.doOperate(carId,
					this.getE(), assignChargerIds, fromContractId,
					stopDate4Charger);
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
			return "json";
		}

	}

	// === 经济合同续约,过户,重发包 代码结束 ===//

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getContractStatuses();
		// 表单可选项的加载
		initSelects();
	}

	/** 设置责任人显示列表 */
	private void setChargerList() {
		Contract4Charger e = this.contract4ChargerService.load(this.id);
		// 根据contractId查找所属的carId
		carId = this.contract4ChargerService.findCarIdByContractId(e.getId());
		// 根据contractId查找所属的责任人ID列表
		List<String> chargerIdList = this.contract4ChargerService
				.findChargerIdByContractId(e.getId());
		// 根据contractId查找所属的责任人姓名列表
		List<String> chargerNameList = this.contract4ChargerService
				.findChargerNameByContractId(e.getId());
		// 组装责任人信息
		if ((chargerIdList != null && chargerIdList.size() > 0)
				&& (e.getExt_str2() != null && e.getExt_str2().length() > 0)) {
			chargerInfoMap = new HashMap<String, String>();
			for (int i = 0; i < chargerIdList.size(); i++) {
				chargerInfoMap
						.put(chargerIdList.get(i), chargerNameList.get(i));
			}
		}
	}

	/** 判断指定的车辆是否已经存在经济合同 */
	public String isExistContract() {
		json = new Json();
		json.put("isExistContract",
				this.contract4ChargerService.isExistContract(carId));
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
		json = new Json();
		Long excludeId = this.contract4ChargerService.checkCodeIsExist(
				this.excludeId, this.code);
		if (excludeId != null) {
			json.put("id", excludeId);
			json.put("isExist", "true"); // 存在重复自编号
			json.put("msg", getText("contract4Labour.code.exist"));
		} else {
			json.put("isExist", "false");
		}
		return "json";
	}

	// ========判断经济合同自编号唯一代码结束========

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = Contract4Charger.KEY_UID;
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(isFlashUpload());
		attachsUI.addClazz("formAttachs");
		attachsUI.addAttach(this.attachService.findByPtype(ptype, this.getE()
				.getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		// 只读控制
		attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly());
		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(720)
				.setMinWidth(250).setMinHeight(160).setHeight(405);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		// 特殊处理的部分
		if (!this.isReadonly()) {// 有权限
			if (editable) {// 编辑状态显示保存按钮
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bc.contract4ChargerFormOperate.save"));
				pageOption.addButton(new ButtonOption(
						getText("label.saveAndClose"), null,
						"bc.contract4ChargerFormOperate.saveAndClose"));

			}
		}
	}

	// 表单可选项的加载
	public void initSelects() {

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CONTRACT_SIGNTYPE,
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CONTRACT_VERSION_NO,
						OptionConstants.MOTORCADE_PAYMENT_DATE });

		// 加载可选签约类型
		this.signTypeList = optionItems.get(OptionConstants.CONTRACT_SIGNTYPE);
		if (!this.getE().isNew()
				&& this.getE().getSignType()
						.equals(getText("contract4Labour.optype.renew"))) {
			OptionItem.insertIfNotExist(signTypeList,
					getText("contract4Labour.optype.renew"),
					getText("contract4Labour.optype.renew"));
		}
		// 加载可选营运性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选合同版本号列表
		this.contractVersionNoList = optionItems
				.get(OptionConstants.CONTRACT_VERSION_NO);
		// 加载缴费日列表
		this.paymentDates = optionItems
				.get(OptionConstants.MOTORCADE_PAYMENT_DATE);

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

	/**
	 * 获取合同的状态列表
	 * 
	 * @return
	 */
	private Map<String, String> getContractStatuses() {
		Map<String, String> types = new HashMap<String, String>();
		types.put(String.valueOf(Contract.STATUS_NORMAL),
				getText("contract.status.normal"));
		types.put(String.valueOf(Contract.STATUS_LOGOUT),
				getText("contract.status.logout"));
		types.put(String.valueOf(Contract.STATUS_RESGIN),
				getText("contract.status.resign"));
		return types;
	}

}