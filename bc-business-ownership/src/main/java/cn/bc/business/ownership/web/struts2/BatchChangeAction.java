/**
 * 
 */
package cn.bc.business.ownership.web.struts2;

import java.util.HashMap;
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

/**
 * 批量修改车辆经营权Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class BatchChangeAction extends FileEntityAction<Long, Ownership> {
	private static final long serialVersionUID = 1L;
	public CarService carService;
	public OwnershipService ownershipService;
	public Map<Long, String> cars;
	private OptionService optionService;
	public List<Map<String, String>> natures; // 经营权性质
	public List<Map<String, String>> situations; // 经营权情况
	public List<Map<String, String>> owners; // 车辆产权
	public String vehicles;// 批量修改经营权的车辆
	public boolean isNature;
	public boolean isSituation;
	public boolean isOwner;
	public String natureValue;
	public String situationValue;
	public String ownerValue;
	public Map<String, Object> ownershipInfo;// 修改信息
	public String ids;// 视图选择多辆车进行修改时的车辆id
	public Long id;// 选一辆车时
	public String changeCars;// 视图选择多辆车

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
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
	public void setOwnershipService(OwnershipService ownershipService) {
		this.ownershipService = ownershipService;
		this.setCrudService(ownershipService);
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	@Override
	public boolean isReadonly() {
		// 车辆经营权管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.ownership"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(Ownership entity) {
		super.afterCreate(entity);
		// 车辆
		cars = new HashMap<Long, String>();
		if (ids != null) {
			String[] id = ids.split(",");
			// 顶班车辆
			changeCars = "";
			for (int i = 0; i < id.length; i++) {
				Car car = this.carService.load(new Long(id[i]));
				cars.put(new Long(id[i]),
						car.getPlateType() + "." + car.getPlateNo());
				if (i > 0) {
					changeCars = changeCars + ";";
				}
				changeCars += car.getPlateType() + "." + car.getPlateNo() + ","
						+ id[i];
			}

		}
		if (id != null) {
			Car car = this.carService.load(new Long(id));
			cars.put(new Long(id), car.getPlateType() + "." + car.getPlateNo());
			changeCars = car.getPlateType() + "." + car.getPlateNo() + "," + id;

		}
		vehicles = changeCars;
	}

	public String save() throws Exception {
		this.beforeSave(this.getE());
		Ownership o = this.getE();
		ownershipInfo = new HashMap<String, Object>();
		// 基本信息
		ownershipInfo.put("fileDate", o.getFileDate());
		ownershipInfo.put("author", o.getAuthor().getId());
		ownershipInfo.put("modifiedDate", o.getModifiedDate());
		ownershipInfo.put("modifier", o.getModifier().getId());
		// 如果经营权性质有改动则设置经营权性质值
		if (isNature) {
			ownershipInfo.put("nature", natureValue);
			o.setNature(natureValue);
		}
		// 如果经营权情况有改动则设置经营权情况值
		if (isSituation) {
			ownershipInfo.put("situation", situationValue);
			o.setSituation(situationValue);

		}
		// 如果车辆产权有改动则设置车辆产权值
		if (isOwner) {
			ownershipInfo.put("owner", ownerValue);
			//o.setOwner(ownerValue);

		}
		// 设置备注
		if (o.getDescription() != null
				&& o.getDescription().trim().length() != 0) {
			ownershipInfo.put("description", o.getDescription());
		}
		// 获取批量处理经营权车辆的Id
		String[] taxis = vehicles.split(";");
		Long[] carIds = new Long[taxis.length];
		for (int i = 0; i < taxis.length; i++) {
			carIds[i] = new Long(taxis[i].split(",")[1]);
		}
		this.ownershipService.saveBatchTaxis(ownershipInfo, carIds);
		return "saveSuccess";
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(530)
				.setMinWidth(320);

	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
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

}
