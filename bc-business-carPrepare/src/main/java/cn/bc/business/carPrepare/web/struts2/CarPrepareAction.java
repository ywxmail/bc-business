/**
 * 
 */
package cn.bc.business.carPrepare.web.struts2;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.carPrepare.service.CarPrepareService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

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
	private AttachService attachService;
	public CarService carService;
	public AttachWidget attachsUI;
	public Long carId;
	public Long unitId;
	public Long motorcadeId;
	public Map<String, String> statusesValue;
	public List<Map<String, String>> companyList; // 可选保险公司列表
	public String buyPlants;// 所买险种的json字符串
	public boolean canCopy;

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
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	public boolean isReadonly() {
		// 车辆保单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.policy"),
				getText("key.role.bc.admin"));

	}

	// @Override
	// protected void beforeSave(Policy entity) {
	// super.beforeSave(entity);
	// // 插入险种值
	// try {
	// Set<BuyPlant> buyPlants = null;
	// // 保存险种字符串如 :车辆(ZB) 第三者(ZB)
	// String buyPlantStr = "";
	// if (this.buyPlants != null && this.buyPlants.length() > 0) {
	// buyPlants = new LinkedHashSet<BuyPlant>();
	// BuyPlant resource;
	// JSONArray jsons = new JSONArray(this.buyPlants);
	// JSONObject json;
	// for (int i = 0; i < jsons.length(); i++) {
	// json = jsons.getJSONObject(i);
	// resource = new BuyPlant();
	// if (json.has("id"))
	// resource.setId(json.getLong("id"));
	// resource.setOrderNo(i);
	// resource.setPolicy(this.getE());
	// resource.setName(json.getString("name"));
	// buyPlantStr += "[";
	// buyPlantStr += json.getString("name");
	// buyPlantStr += ":";
	// resource.setCoverage(json.getString("coverage"));
	// buyPlantStr += json.getString("coverage");
	// // 只要备注不为空，也显示备注
	// if (json.getString("description") != null
	// && json.getString("description").length() > 0) {
	// buyPlantStr += ":";
	// buyPlantStr += json.getString("description");
	// }
	// buyPlantStr += "]";
	// buyPlantStr += "  ";
	// resource.setDescription(json.getString("description"));
	// buyPlants.add(resource);
	// }
	// }
	// if (this.getE().getBuyPlants() != null) {
	// this.getE().getBuyPlants().clear();
	// this.getE().getBuyPlants().addAll(buyPlants);
	// this.getE().setBuyPlantStr(buyPlantStr);
	// } else {
	// this.getE().setBuyPlants(buyPlants);
	// this.getE().setBuyPlantStr(buyPlantStr);
	// }
	// } catch (JSONException e) {
	// logger.error(e.getMessage(), e);
	// try {
	// throw e;
	// } catch (JSONException e1) {
	// e1.printStackTrace();
	// }
	// }
	// }


	@Override
	protected void initForm(boolean editable) throws Exception {
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

		if (editable && !this.isReadonly()) {
			canCopy = false;

		} else {
			canCopy = true;
		}

		return super.buildFormPageOption(editable).setWidth(730)
				.setMinWidth(300).setHeight(540).setMinHeight(300);
	}

}