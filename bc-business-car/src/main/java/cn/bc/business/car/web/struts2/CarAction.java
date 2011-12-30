/**
 * 
 */
package cn.bc.business.car.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntityImpl;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车辆Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarAction extends FileEntityAction<Long, Car> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private MotorcadeService motorcadeService;
	private OptionService optionService;

	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> levelTypeList; // 可选车辆定级列表
	public List<Map<String, String>> factoryTypeList; // 可选厂牌类型列表
	public List<Map<String, String>> fuelTypeList; // 可选燃料类型列表
	public List<Map<String, String>> colorTypeList; // 可选颜色类型列表
	public List<Map<String, String>> taximeterFactoryTypeList; // 可选计价器制造厂列表
	public Map<String, String> statusesValue;

	@Autowired
	public void setCarService(CarService carService) {
		this.setCrudService(carService);
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.car"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(790)
				.setMinWidth(250).setHeight(590).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Car entity) {
		super.afterCreate(entity);

		// 设置默认的原归属单位信息
		this.getE().setOldUnitName(getText("app.oldUnitName"));

		// 自动生成uid
		this.getE().setUid(this.getIdGeneratorService().next(Car.KEY_UID));

		// 自动生成自编号
		this.getE().setCode(
				this.getIdGeneratorService().nextSN4Month(Car.KEY_CODE));

		// 初始化车辆的状态
		this.getE().setStatus(BCConstants.STATUS_ENABLED);
	}

	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getBSStatuses1();

		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.find4Option();
		Motorcade m = this.getE().getMotorcade();
		if (m != null) {
			OptionItem.insertIfNotExist(this.motorcadeList, m.getId()
					.toString(), m.getName());
		}

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.CAR_RANK, OptionConstants.CAR_BRAND,
						OptionConstants.CAR_FUEL_TYPE,
						OptionConstants.CAR_COLOR,
						OptionConstants.CAR_TAXIMETERFACTORY });

		// 加载可选营运性质列表
		this.businessTypeList = optionItems
				.get(OptionConstants.CAR_BUSINESS_NATURE);

		// 加载可选营运性质列表
		this.levelTypeList = optionItems.get(OptionConstants.CAR_RANK);

		// 加载可选厂牌类型列表
		this.factoryTypeList = optionItems.get(OptionConstants.CAR_BRAND);

		// 加载可选燃料类型列表
		this.fuelTypeList = optionItems.get(OptionConstants.CAR_FUEL_TYPE);

		// 加载可选颜色类型列表
		this.colorTypeList = optionItems.get(OptionConstants.CAR_COLOR);

		// 加载可选 计价器制造厂列表
		this.taximeterFactoryTypeList = optionItems
				.get(OptionConstants.CAR_TAXIMETERFACTORY);
	}
}