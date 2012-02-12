/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.service.Contract4ChargerService;
import cn.bc.core.util.DateUtils;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 经济合同相关操作的Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4ChargerOperateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public Contract4ChargerService contract4ChargerService;

	@Autowired
	public void setContract4ChargerService(
			Contract4ChargerService contract4ChargerService) {
		this.contract4ChargerService = contract4ChargerService;
	}

	private Long id;
	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Calendar newStartDate;
	private Calendar newEndDate;
	private String code;

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

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	// ========劳动合同续签代码开始========

	/**
	 * 经济合同续签
	 */
	public String doRenew() throws Exception {
		Long fromContractId = this.getId();
		Contract newContract = this.contract4ChargerService.doRenew(
				fromContractId, newStartDate, newEndDate, code);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Charger.renew.success"));
		return "json";
	}

	// ========劳动合同续签代码结束========

	// ========劳动合同过户代码开始========
	private Long carId;
	private Boolean takebackOrigin;
	private String assignChargerIds;
	private String assignChargerNames;

	public String getAssignChargerIds() {
		return assignChargerIds;
	}

	public void setAssignChargerIds(String assignChargerIds) {
		this.assignChargerIds = assignChargerIds;
	}

	public String getAssignChargerNames() {
		return assignChargerNames;
	}

	public void setAssignChargerNames(String assignChargerNames) {
		this.assignChargerNames = assignChargerNames;
	}

	public Boolean getTakebackOrigin() {
		return takebackOrigin;
	}

	public void setTakebackOrigin(Boolean takebackOrigin) {
		this.takebackOrigin = takebackOrigin;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 经济合同过户
	 */
	public String doChangeCharger() throws Exception {
		Long fromContractId = this.getId();
		// 组装责任人字符串如:姓名1,id1;姓名2,id2;
		String assignChargerNamesStr = setChargerName(assignChargerIds,
				assignChargerNames);

		Contract newContract = this.contract4ChargerService.doChaneCharger(
				carId, takebackOrigin, assignChargerIds, assignChargerNamesStr,
				fromContractId, newStartDate, newEndDate, code);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Charger.changeCharger.success"));
		return "json";
	}

	// ========劳动合同过户代码结束========

	// ========劳动合同重发包代码开始========

	/**
	 * 经济合同重发包
	 */
	public String doChangeCharger2() throws Exception {
		Long fromContractId = this.getId();
		// 组装责任人字符串如:姓名1,id1;姓名2,id2;
		String assignChargerNamesStr = setChargerName(assignChargerIds,
				assignChargerNames);

		Contract newContract = this.contract4ChargerService.doChaneCharger2(
				carId, takebackOrigin, assignChargerIds, assignChargerNamesStr,
				fromContractId, newStartDate, newEndDate, code);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Charger.changeCharger2.success"));
		return "json";
	}

	// ========劳动合同重发包代码结束========

	// ========劳动合同注销代码开始========
	public String logoutDate;// 注销日期

	/**
	 * 经济合同注销
	 */
	public String doLogout() throws Exception {
		Long contractId = this.getId();
		Calendar logoutDate = DateUtils.getCalendar(this.logoutDate);
		this.contract4ChargerService.doLogout(contractId, logoutDate);
		json = new Json();
		json.put("id", contractId);
		json.put("msg", getText("contract4Charger.logout.success"));
		return "json";
	}

	// ========劳动合同注销代码开始========

	/**
	 * 设置责任人姓名
	 * 
	 * @param assignChargerIds
	 * @param assignChargerNames
	 * @return
	 */
	private String setChargerName(String assignChargerIds,
			String assignChargerNames) {
		String chargerName = "";
		if (assignChargerIds.length() > 0) {
			String[] ids = assignChargerIds.split(",");
			String[] names = assignChargerNames.split(",");
			for (int i = 0; i < ids.length; i++) { // 设置责任人如:姓名1,id1;姓名2,id2;
				chargerName += names[i] + ",";
				chargerName += ids[i] + ";";
			}
		}
		return chargerName;
	}

}