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

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.policy.domain.BuyPlant;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.policy.service.PolicyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车保Action
 * 
 * @author dragon
 * 
 */
@SuppressWarnings("unused")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class PolicyAction extends FileEntityAction<Long, Policy> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	public OptionService optionService;
	public PolicyService policyService;
	private AttachService attachService;
	public CarService carService;
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

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public boolean isReadonly() {
		// 车辆保单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.policy"),
				getText("key.role.bc.admin"));

	}

	@Override
	protected void afterCreate(Policy entity) {
		super.afterCreate(entity);
		if (carId != null) {
			Car car = this.carService.load(carId);
			this.getE().setCar(car);
		}
		// 新建时填写表单的默认信息
		// 默认购买强制险
		this.getE().setGreenslip(true);
		// 默认强制险时间与商业险时间不一致
		this.getE().setGreenslipSameDate(false);
		this.getE().setVerMajor(Policy.MAJOR_DEFALUT);
		this.getE().setVerMinor(Policy.MINOR_DEFALUT);
		this.getE().setOpType(Policy.OPTYPE_CREATE);
		this.getE().setUid(this.getIdGeneratorService().next(Policy.KEY_UID));
		this.getE().setPatchNo(this.getE().getUid());	
	}


	@Override
	protected void beforeSave(Policy entity) {
		super.beforeSave(entity);
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
					resource.setCoverage(json.getString("coverage"));
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
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	
	
	
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (editable) {// edit,create
			// if (this.getE().isNew()) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.policyForm.save").setId("policySave"));
			// }
		} else {// open
			if (!readonly) {
			if(this.getE().getStatus()==Policy.STATUS_ENABLED){
				//维护
				pageOption.addButton(new ButtonOption(
						getText("policy.optype.edit"), null,
						"bc.policyForm.doMaintenance").setId("policyeEdit"));
				//注销
				pageOption.addButton(new ButtonOption(
						getText("policy.status.disabled"),null,
						"bc.policyForm.doLogout"));
				//停保 
				/*pageOption.addButton(new ButtonOption(
						getText("policy.optype.surrenders"), null,
						"bc.policyForm.doSurrender").setId("policySurrenders"));*/
			  }
			}
		}
	}

	@Override
	protected void afterEdit(Policy entity) {
		super.afterEdit(entity);
		// 维护时对车保信息进行的修改
		// 次版本号加1
		Integer verMinor = this.getE().getVerMinor();
		this.getE().setVerMinor(verMinor + 1);
		// 操作类型为：维护
		this.getE().setOpType(Policy.OPTYPE_EDIT);
	}

	@Override
	protected void afterOpen(Policy entity) {
		if(isReadonly()){
			this.getE().setLiabilityAmount((float)-1);
			this.getE().setCommerialAmount((float)-1);
			this.getE().setGreenslipAmount((float)-1);
		}
		super.afterOpen(entity);
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);
		Date startTime = new Date();
		statusesValue = this.getBSStatuses1();
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CA_COMPANY, });

		// 加载可选保险公司列表
		this.companyList = optionItems.get(OptionConstants.CA_COMPANY);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));

	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(730)
				.setMinWidth(300).setHeight(540).setMinHeight(300);
	}

}