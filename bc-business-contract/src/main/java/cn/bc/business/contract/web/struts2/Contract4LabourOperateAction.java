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

	// ========合同续签相关
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
	 * 合同续签
	 */
	public String renew() throws Exception {
		Long fromContractId = this.getId();
		Contract newContract = this.contract4LabourService.doRenew(
				fromContractId, newStartDate, newEndDate);
		json = new Json();
		json.put("id", newContract.getId());
		json.put("oldId", fromContractId);
		json.put("msg", getText("contract.labour.renew.success"));
		return "json";
	}
}