/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.service.CarService;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.policy.service.PolicyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.html.toolbar.ToolbarMenuButton;
import cn.bc.web.ui.json.Json;

/**
 * 黑名单Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PolicyAction extends FileEntityAction<Long, Policy> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public OptionService optionService;
	public PolicyService policyService;
	public String carPlate;// 车牌号码
	private CarService carService;
	private AttachService attachService;
	public AttachWidget attachsUI;
	public Long carId;
	public Long unitId;
	public Long motorcadeId;
	public Map<String, String> statusesValue;
	public List<Map<String, String>> companyList; // 可选保险公司列表

	@Autowired
	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
		this.setCrudService(policyService);
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

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆保单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.policy"),
				getText("key.role.bc.admin"));
	}

	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		// 构建附件控件
		String ptype = "contractLabour.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	@Override
	public String create() throws Exception {
		String result = super.create();
		statusesValue = this.getBSStatuses1();
		this.getE().setGreenslip(true);
		this.getE().setGreenslipSameDate(false);
		this.getE().setVerMajor(Policy.MAJOR_DEFALUT);
		this.getE().setUid(
				this.getIdGeneratorService().next(Policy.POLICY_TYPE));
		this.getE().setPatchNo(this.getE().getUid());
		// 构建附件控件
		attachsUI = buildAttachsUI(true);
		initSelects();
		return result;
	}

	@Override
	public String save() throws Exception {
		SystemContext context = this.getSystyemContext();

		super.save();
		return "saveSuccess";
	}

	@Override
	public String edit() throws Exception {
		String result = super.edit();
		statusesValue = this.getBSStatuses1();
		initSelects();
		return result;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = super.buildFormPageOption().setWidth(740)
				.setMinWidth(250).setMinHeight(200).setHeight(540);
		if (!this.isReadonly()) {
			ButtonOption buttonOption = new ButtonOption(getText("label.save"),
					"save");
			buttonOption.put("id", "bcSaveBtn");
			option.addButton(buttonOption);
			if (!this.getE().isNew()&& this.getE().getMain() == Policy.MAIN_NOW) {
				ToolbarMenuButton toolbarMenuButton = new ToolbarMenuButton(
						getText("policy.labour.op"));
				toolbarMenuButton.setId("bcOpBtn");
				toolbarMenuButton
						.addMenuItem(getText("policy.labour.optype.edit"),
								Policy.OPTYPE_EDIT + "")
						.addMenuItem(getText("policy.labour.optype.renewal"),
								Policy.OPTYPE_RENEWAL + "")
						.addMenuItem(
								getText("policy.labour.optype.surrenders"),
								Policy.OPTYPE_SURRENDERS + "")
						.setChange("bc.policyForm.selectMenuButtonItem");
				option.addButton(toolbarMenuButton);
			}
		}
		return option;
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(700).setMinWidth(300)
				.setHeight(350).setMinHeight(300);
	}

	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carId != null)
			page.setAttr("data-extras", new Json().put("carManId", carId)
					.toString());
		return page;
	}

	// 视图特殊条件
	@Override
	protected Condition getSpecalCondition() {
		return null;

	}

	// 表单可选项的加载
	public void initSelects() {
		Date startTime = new Date();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CA_COMPANY, });

		// 加载可选保险公司列表
		this.companyList = optionItems.get(OptionConstants.CA_COMPANY);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));
	}

}