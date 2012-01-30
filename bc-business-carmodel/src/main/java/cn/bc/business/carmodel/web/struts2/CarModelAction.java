/**
 * 
 */
package cn.bc.business.carmodel.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.business.carmodel.service.CarModelService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 车型配置Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarModelAction extends FileEntityAction<Long, CarModel> {
	// private static Log logger = LogFactory.getLog(CarModelAction.class);
	private static final long serialVersionUID = 1L;
	public CarModelService carModelService;
	private OptionService optionService;
	public boolean isManager;

	public Map<String, String> statusesValue;
	public List<Map<String, String>> factoryTypeList; // 可选厂牌类型列表
	public List<Map<String, String>> fuelTypeList; // 可选燃料类型列表


	@Autowired
	public void setCarModelServicee(CarModelService carModelService) {
		this.carModelService = carModelService;
		this.setCrudService(carModelService);
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
	
	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(CarModel entity){
		super.afterCreate(entity);
		
		// 自动生成UID
		entity.setUid(this.getIdGeneratorService().next(this.getE().KEY_UID));
		entity.setStatus(CarModel.STATUS_NORMAL);
	}
	
	@Override
	protected void initForm(boolean editable) {
		super.initForm(editable);
		// 状态列表
		statusesValue		=	this.getEntityStatuses();
		
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { 
					OptionConstants.CAR_BRAND,
					OptionConstants.CAR_FUEL_TYPE
				});
		
		
		// 加载可选厂牌类型列表
		this.factoryTypeList = optionItems.get(OptionConstants.CAR_BRAND);

		// 加载可选燃料类型列表
		this.fuelTypeList = optionItems.get(OptionConstants.CAR_FUEL_TYPE);
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return	super.buildFormPageOption(editable).setWidth(735).setHeight(350)
				.setMinWidth(250).setMinHeight(170);
	}


}