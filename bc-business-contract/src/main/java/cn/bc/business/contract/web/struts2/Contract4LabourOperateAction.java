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
import cn.bc.business.contract.service.Contract4LabourService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 劳动合同相关操作的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Contract4LabourOperateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public Contract4LabourService contract4LabourService;

	@Autowired
	public void setContract4LabourService(
			Contract4LabourService contract4LabourService) {
		this.contract4LabourService = contract4LabourService;
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

	public Calendar getResignDate() {
		return resignDate;
	}

	public void setResignDate(Calendar resignDate) {
		this.resignDate = resignDate;
	}

	/**
	 * 劳动合同离职
	 */
	public String doResign() throws Exception {
		Long fromContractId = this.getId();
		this.contract4LabourService.doResign(fromContractId, resignDate);
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
		Long fromContractId = this.getId();
		Contract newContract = this.contract4LabourService.doChangeCar(fromContractId, newCarId , newCarPlate);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Labour.changeCar.success"));
		return "json";
	}

	// ========劳动合同转车代码结束========
}