/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.service.Contract4LabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.json.Json;

/**
 * 劳动合同相关操作的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4LabourOperateAction extends
		FileEntityAction<Long, Contract4Labour> {
	private static final long serialVersionUID = 1L;
	public Contract4LabourService contract4LabourService;
	private OptionService optionService;

	public Map<String, String> statusesValue;
	public Map<String, Object> certInfoMap; // 证件信息map
	public Map<String, Object> carInfoMap; // 车辆信息map
	public Map<String, Object> carManInfoMap; // 司机信息map
	public List<Map<String, Object>> infoList;

	// public List<Map<String, String>> businessTypeList; // 可选营运性质列表
	public List<Map<String, String>> insurancetypeList; // 可选营运性质列表
	public List<Map<String, String>> houseTypeList; // 可选户口类型列表
	public List<Map<String, String>> buyUnitList; // 可选购买单位列表

	@Autowired
	public void setContract4LabourService(
			Contract4LabourService contract4LabourService) {
		this.contract4LabourService = contract4LabourService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	private Long id;
	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// ========劳动合同续签代码开始========
	private Calendar newStartDate;
	private Calendar newEndDate;

	public Calendar getNewStartDate() {
		return newStartDate;
	}

	public void setNewStartDate(Calendar newStartDate) {
		this.newStartDate = newStartDate;
	}

	public Calendar getNewEndDate() {
		return newEndDate;
	}

	public void setNewEndDate(Calendar newEndDate) {
		this.newEndDate = newEndDate;
	}

	/**
	 * 劳动合同续签
	 */
	public String doRenew() throws Exception {
		Long fromContractId = this.getId();
		Contract newContract = this.contract4LabourService.doRenew(
				fromContractId, newStartDate, newEndDate);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Labour.renew.success"));
		return "json";
	}

	// ========劳动合同续签代码结束========

	// ========劳动合同离职代码开始========
	private Calendar resignDate;
	private Calendar stopDate;

	public Calendar getResignDate() {
		return resignDate;
	}

	public void setResignDate(Calendar resignDate) {
		this.resignDate = resignDate;
	}

	public Calendar getStopDate() {
		return stopDate;
	}

	public void setStopDate(Calendar stopDate) {
		this.stopDate = stopDate;
	}

	/**
	 * 劳动合同离职
	 */
	public String doResign() throws Exception {
		Long fromContractId = this.getId();
		this.contract4LabourService.doResign(fromContractId, resignDate,
				stopDate);
		json = new Json();
		json.put("id", fromContractId);
		json.put("msg", getText("contract4Labour.resign.success"));
		return "json";
	}

	// ========劳动合同离职代码结束========

	// ========劳动合同转车代码开始========
	private Long newCarId;
	private String newCarPlate;

	public Long getNewCarId() {
		return newCarId;
	}

	public void setNewCarId(Long newCarId) {
		this.newCarId = newCarId;
	}

	public String getNewCarPlate() {
		return newCarPlate;
	}

	public void setNewCarPlate(String newCarPlate) {
		this.newCarPlate = newCarPlate;
	}

	/**
	 * 劳动合同转车
	 */
	public String doChangeCar() throws Exception {
		// Long fromContractId = this.getId();
		// Contract newContract =
		// this.contract4LabourService.doChangeCar(fromContractId, newCarId ,
		// newCarPlate);
		// json = new Json();
		// json.put("id", newContract.getId());
		// json.put("oldId", fromContractId);
		// json.put("msg", getText("contract4Labour.changeCar.success"));
		// return "json";
		return "form";
	}

	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态列表
		statusesValue = this.getBSStatuses3();

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_BUSINESS_NATURE,
						OptionConstants.LB_INSURANCETYPE,
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.LB_BUYUNIT });

		// 加载可选营运性质列表
		// this.businessTypeList = optionItems
		// .get(OptionConstants.CAR_BUSINESS_NATURE);
		// 加载可选社保险种列表
		this.insurancetypeList = optionItems
				.get(OptionConstants.LB_INSURANCETYPE);
		OptionItem.insertIfNotExist(this.insurancetypeList, this.getE()
				.getInsuranceType(), null);
		// 加载可选户口类型列表
		this.houseTypeList = optionItems.get(OptionConstants.CARMAN_HOUSETYPE);
		// 加载可选购买单位列表
		this.buyUnitList = optionItems.get(OptionConstants.LB_BUYUNIT);
		OptionItem.insertIfNotExist(this.buyUnitList, this.getE().getBuyUnit(),
				null);
	}

	// ========劳动合同转车代码结束========
}