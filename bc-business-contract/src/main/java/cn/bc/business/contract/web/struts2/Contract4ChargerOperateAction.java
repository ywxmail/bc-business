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
	 * 经济合同续签
	 */
	public String doRenew() throws Exception {
		Long fromContractId = this.getId();
		Contract newContract = this.contract4ChargerService.doRenew(
				fromContractId, newStartDate, newEndDate);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract4Labour.renew.success"));
		return "json";
	}

	// ========劳动合同续签代码结束========

}