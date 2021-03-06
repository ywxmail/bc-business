package cn.bc.business.ownership.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.business.ownership.service.OwnershipService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 车辆经营权Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class OwnershipAction extends FileEntityAction<Long, Ownership> {
	private static final long serialVersionUID = 1L;
	public OwnershipService ownershipService;
	public CarService carService;
	public Map<String, String> statusesValue;
	private OptionService optionService;
	public List<Map<String, String>> natures; // 经营权性质
	public List<Map<String, String>> situations; // 经营权情况
	public List<Map<String, String>> owners; // 车辆产权列表
	public String owner;// 车辆产权值

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setOwnershipService(OwnershipService ownershipService) {
		this.ownershipService = ownershipService;
		this.setCrudService(ownershipService);
	}

	@Override
	public boolean isReadonly() {
		// 车辆经营权管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.ownership"));
	}

	public boolean isCheck() {
		// 车辆经营权查看角色
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.ownership.check"));
	}

	public boolean isCheck4Advanced() {
		// 车辆经营权查看(高级)
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bs.ownership.check_advanced"));
	}

	// public String json;
	Json jsonObject = new Json();

	@Override
	protected void afterEdit(Ownership entity) {
		super.afterEdit(entity);
		// 如果该经营权号已配车(在案)就获取车的产权值
		Car car = this.ownershipService.getCarByNumber(entity.getNumber());
		if (car != null) {
			owner = car.getOwner();
		}
	}

	@Override
	public String save() throws Exception {

		// 如果存在相同经营权号的就提示已存在
		if (this.getE().getNumber() != null) {
			Ownership ownership = this.ownershipService
					.getOwershipByNumber(this.getE().getNumber());
			// 新建的判断
			if (this.getE().isNew()) {
				if (ownership != null) {
					jsonObject.put("success", false);
					jsonObject.put("id", this.getE().getId());
					jsonObject.put("msg", "经营权：“" + this.getE().getNumber()
							+ "”已经存在！");
				} else {
					this.beforeSave(this.getE());
					this.ownershipService.save(this.getE());
					// 更新车辆的产权字段
					this.ownershipService.updateCar4OwnerByNumber(owner, this
							.getE().getNumber());
					jsonObject.put("success", true);
					jsonObject.put("id", this.getE().getId());
					jsonObject.put("msg", "保存成功！");
				}

			} else {
				// 已存在的
				if (ownership != null
						&& !this.getE().getId().equals(ownership.getId())) {
					jsonObject.put("success", false);
					jsonObject.put("id", this.getE().getId());
					jsonObject.put("msg", "经营权：“" + this.getE().getNumber()
							+ "”已经存在！");
				} else {
					this.beforeSave(this.getE());
					this.ownershipService.save(this.getE());
					// 更新车辆的产权字段
					this.ownershipService.updateCar4OwnerByNumber(owner, this
							.getE().getNumber());
					jsonObject.put("success", true);
					jsonObject.put("id", this.getE().getId());
					jsonObject.put("msg", "保存成功！");
				}

			}

		}
		this.json = jsonObject.toString();
		return "json";

	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getBSStatuses1();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.OWNERSHIP_NATURE,
						OptionConstants.OWNERSHIP_OWNER,
						OptionConstants.OWNERSHIP_SITUATION });
		// 经营权性质
		natures = optionItems.get(OptionConstants.OWNERSHIP_NATURE);
		// 经营权情况
		situations = optionItems.get(OptionConstants.OWNERSHIP_SITUATION);
		// 车辆产权
		owners = optionItems.get(OptionConstants.OWNERSHIP_OWNER);

	}

	// 不使用打印按钮
	protected boolean useFormPrint() {
		return false;
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(400)
				.setMinWidth(250).setMaximizable(false).setMinimizable(false)
				.setPrint(null);
	}

}
