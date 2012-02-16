/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.domain.ActorDetail;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 司机责任人Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManAction extends FileEntityAction<Long, CarMan> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	private OptionService optionService;
	private CarManService carManService;

	public Map<String, String> statusesValue;
	public List<Map<String, String>> carManHouseTypeList;// 司机责任人户口性质列表
	public List<Map<String, String>> carManLevelList;// 司机责任人等级列表
	public List<Map<String, String>> carManModelList;// 司机责任人准驾车型列表

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
		this.setCrudService(carManService);
	}

	@Override
	public boolean isReadonly() {
		// 司机管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.driver"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(CarMan entity) {
		super.afterCreate(entity);

		// 额外参数的设置
		this.getE().setStatus(BCConstants.STATUS_ENABLED);
		this.getE().setUid(this.getIdGeneratorService().next(CarMan.KEY_UID));
		this.getE().setOrderNo(
				this.getIdGeneratorService().nextSN4Month(Car.KEY_CODE));

		this.getE().setSex(ActorDetail.SEX_MAN);
		// 新建司机时，迁移类型为空值
		this.getE().setMoveType(CarByDriverHistory.MOVETYPE_NULL);
		// 驾驶状态为空
		this.getE().setClasses(CarByDriver.TYPE_WEIDINGYI);
		
		// 广州驾证默认为“是”
		this.getE().setGz(true);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getBSStatuses1();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.CARMAN_LEVEL,
						OptionConstants.CARMAN_MODEL, });

		// 司机责任人户口性质列表
		this.carManHouseTypeList = optionItems
				.get(OptionConstants.CARMAN_HOUSETYPE);

		// 司机责任人等级列表
		this.carManLevelList = optionItems.get(OptionConstants.CARMAN_LEVEL);

		// 司机责任人准驾车型列表
		this.carManModelList = optionItems.get(OptionConstants.CARMAN_MODEL);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(760)
				.setMinWidth(250).setMinHeight(200);
	}

	// ========服务资格证唯一性检测代码开始========
	public String cert4FWZG;
	private Long excludeId;

	public Long getExcludeId() {
		return excludeId;
	}

	public void setExcludeId(Long excludeId) {
		this.excludeId = excludeId;
	}

	public String checkCert4FWZGIsExists() {
		Json json = new Json();
		Long existsId = this.carManService.checkCert4FWZGIsExists(
				this.excludeId, this.cert4FWZG);
		if (existsId != null) {
			json.put("id", existsId);
			json.put("isExists", "true"); // 存在重复自编号
			json.put("msg", getText("carMan.error.cert4FWZGIsExists"));
		} else {
			json.put("isExists", "false");
		}

		this.json = json.toString();
		return "json";
	}
	// ========服务资格证唯一性检测代码结束========
}