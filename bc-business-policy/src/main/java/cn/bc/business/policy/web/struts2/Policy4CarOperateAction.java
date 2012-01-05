/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.policy.domain.Policy;
import cn.bc.business.policy.service.PolicyService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 车辆保单相关操作的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Policy4CarOperateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	public Policy policy;
	public PolicyService policyService;

	@Autowired
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	@Autowired
	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	private Long id;
	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// ========车辆保单续保代码开始========

	/**
	 * 续保续签
	 */
	public String doRenew() throws Exception {
		Long oldPolicyId = this.getId();
		Policy newPolicy = this.policyService.doRenew(oldPolicyId);
		json = new Json();
		json.put("id", newPolicy.getId());
		json.put("oldId", oldPolicyId);
		json.put("msg", getText("contract4Labour.renew.success"));
		return "json";
	}

	// ========车保代码结束========

	// ========车保停保代码开始========
	private Calendar surrenderDate;

	public Calendar getSurrenderDate() {
		return surrenderDate;
	}

	public void setSurrenderDate(Calendar surrenderDate) {
		this.surrenderDate = surrenderDate;
	}

	/**
	 * 车保停保
	 */
	public String doSurrender() throws Exception {
		Long fromPolicyId = this.getId();
		this.policyService.doSurrender(fromPolicyId, surrenderDate);
		json = new Json();
		json.put("id", fromPolicyId);
		json.put("msg", getText("contract4Labour.resign.success"));
		return "json";
	}

	// ========车保停保代码结束========

}