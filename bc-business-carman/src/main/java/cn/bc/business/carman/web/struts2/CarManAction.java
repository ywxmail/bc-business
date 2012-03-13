/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.placeorigin.service.PlaceOriginService;
import cn.bc.web.ui.html.page.ButtonOption;
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
		this.getE().setType(CarMan.TYPE_DRIVER_AND_CHARGER);
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

	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.carManForm.save"));
			pageOption.addButton(new ButtonOption(
					getText("label.saveAndClose"), null,
					"bc.carManForm.saveAndClose"));

		}
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

	// ========根据身份证号码匹配出司机的籍贯、生日和区域开始========
	private String identityId;

	public String getIdentityId() {
		return identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	private PlaceOriginService placeOriginServie;

	@Autowired
	public void setPlaceOriginServie(PlaceOriginService placeOriginServie) {
		this.placeOriginServie = placeOriginServie;
	}

	public String autoLoadCarManIdentityInfo() {
		Json json = new Json();
		// 根据编码找出籍贯
		List<PlaceOrigin> pList = null;
		String code = this.identityId.substring(0, 6);
		// 先按身份证前6位查找
		pList = this.placeOriginServie.findPlaceOrigin(code);
		if (pList == null || pList.size() == 0) {
			// 若前6位找不到然后按前4位
			code = this.identityId.substring(0, 4);
			pList = this.placeOriginServie.findPlaceOrigin(code + "00");
			if (pList == null || pList.size() == 0) {
				code = this.identityId.substring(0, 2);
				pList = this.placeOriginServie.findPlaceOrigin(code + "0000");
			}
		}

		if (pList != null && pList.size() > 0) {
			// 取集合中第一的对象
			PlaceOrigin po = pList.get(0);
			String fullname = po.getFullname();
			json.put("origin", fullname);
			// 根据全名判断区域
			String[] isFlag = fullname.split("广东省广州市");
			if (isFlag.length >= 2) {
				// 本市
				json.put("area", "1");
			} else {
				isFlag = fullname.split("广东省");
				if (isFlag.length >= 2) {
					// 本省
					json.put("area", "2");
				} else {
					// 外省
					json.put("area", "3");
				}
			}

		}
		// 生成出生日期
		String birthday = "";
		birthday += this.identityId.substring(6, 10);
		birthday += "-";
		birthday += this.identityId.substring(10, 12);
		birthday += "-";
		birthday += this.identityId.substring(12, 14);
		json.put("birthday", birthday);
		this.json = json.toString();
		return "json";
	}

	// ========根据身份证号码匹配出司机的籍贯、生日和区域结束========

	public String phone1;// 电话1
	public String phone2;// 电话2
	public String error;// 错误信息

	/**
	 * 车辆查询中 更新司机、责任人的联系电话信息的页面
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updatePhonePage() throws Exception {
		// 判断权限:司机管理 或 更新司机联系电话
		if (!((SystemContext) this.getContext()).hasAnyRole("BS_DRIVER",
				"BS_DRIVER_UPDATE_PHONE")) {
			error = "你没有更新联系电话的权限";
		}
		return SUCCESS;
	}

	/**
	 * 更新司机、责任人的联系电话信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updatePhone() throws Exception {
		JSONObject json = new JSONObject();

		// 判断权限:司机管理 或 更新司机联系电话
		if (((SystemContext) this.getContext()).hasAnyRole("BS_DRIVER",
				"BS_DRIVER_UPDATE_PHONE")) {
			// TODO: 更新联系电话并记录操作日志

			json.put("success", true);
			json.put("msg", "成功更新联系电话信息");
		} else {
			json.put("success", false);
			json.put("msg", "你没有更新联系电话的权限");
		}

		this.json = json.toString();
		return "json";
	}
}