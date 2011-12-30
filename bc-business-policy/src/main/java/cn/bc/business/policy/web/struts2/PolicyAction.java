/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.Calendar;
import java.util.Date;
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
import cn.bc.business.car.service.CarService;
import cn.bc.business.policy.domain.BuyPlant;
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
	public String buyPlants;// 所买险种的json字符串

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

	public boolean isReadonly() {

		if (!this.getE().isNew()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isRole() {
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
		this.getE().setOpType(Policy.OPTYPE_CREATE);
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
		// 如果操作类型为续保，执行续保方法
		if (this.getE().getOpType() == Policy.OPTYPE_RENEWAL) {
			renewal(true);
		}
		// 插入险种值
		try {
			Set<BuyPlant> buyPlants = null;
			if (this.buyPlants != null && this.buyPlants.length() > 0) {
				buyPlants = new LinkedHashSet<BuyPlant>();
				BuyPlant resource;
				JSONArray jsons = new JSONArray(this.buyPlants);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new BuyPlant();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setOrderNo(i);
					resource.setPolicy(this.getE());
					resource.setName(json.getString("name"));
					resource.setCoverage(new Float(json.getLong("coverage")));
					resource.setPremium(new Float(json.getLong("premium")));
					resource.setDescription(json.getString("description"));
					buyPlants.add(resource);
				}
			}
			if (this.getE().getBuyPlants() != null) {
				this.getE().getBuyPlants().clear();
				this.getE().getBuyPlants().addAll(buyPlants);
			} else {
				this.getE().setBuyPlants(buyPlants);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		super.save();
		return "saveSuccess";

	}

	// 续保
	protected void renewal(boolean flag) throws Exception {

		SystemContext context = this.getSystyemContext();
		Policy p = this.getE();
		// 旧车保
		Policy oldE = policyService.load(p.getPid());
		// 版本号加1
		int oldVerMajor = oldE.getVerMajor();
		p.setVerMajor(oldVerMajor + 1);
		// 标识为当前版本
		p.setMain(Policy.MAIN_NOW);
		// 设置创建人和创建时间
		p.setAuthor(context.getUserHistory());
		p.setFileDate(Calendar.getInstance());
		// 设置UId
		this.getE().setUid(
				this.getIdGeneratorService().next(Policy.POLICY_TYPE));
		// 续保后旧车保要做相应的处理
		if (p.getPid() != null) {
			if (oldE != null) {
				oldE.setStatus(BCConstants.STATUS_DISABLED); // 把旧的车保状态改为注销
				oldE.setMain(Policy.MAIN_HISTORY); // 设定为历史标识
				// 保存旧的记录
				this.getCrudService().save(oldE);
			}
		}

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
		if (!this.isRole()) {
			ButtonOption buttonOption = new ButtonOption(getText("label.save"),
					null, "bc.policyForm.save");
			buttonOption.put("id", "bcSaveBtn");
			option.addButton(buttonOption);
			if (!this.getE().isNew()
					&& this.getE().getMain() == Policy.MAIN_NOW) {
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