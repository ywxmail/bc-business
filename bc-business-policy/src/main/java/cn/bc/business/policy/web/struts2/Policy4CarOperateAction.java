/**
 * 
 */
package cn.bc.business.policy.web.struts2;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.policy.domain.Policy;
import cn.bc.business.policy.service.PolicyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.service.OptionService;
import cn.bc.web.struts2.EntityAction;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 车辆保单相关操作的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Policy4CarOperateAction extends FileEntityAction<Long, Policy> {
	private static final long serialVersionUID = 1L;
	public PolicyService policyService;
	public OptionService optionService;
	private AttachService attachService;
	public Map<String, String> statusesValue;
	public AttachWidget attachsUI;
	public List<Map<String, String>> companyList; // 可选保险公司列表

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	private Long id;
	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isReadonly() {
		// 车辆保单管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.policy"),
				getText("key.role.bc.admin"));

	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(725)
				.setMinWidth(300).setHeight(540).setMinHeight(300);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();

		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.policyForm.save").setId("policySave"));
		}

	}

	protected void initForm(boolean editable) {
		super.initForm(editable);
		Date startTime = new Date();
		statusesValue = this.getBSStatuses1();
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CA_COMPANY, });

		// 加载可选保险公司列表
		this.companyList = optionItems.get(OptionConstants.CA_COMPANY);

		if (logger.isInfoEnabled())
			logger.info("findOptionItem耗时：" + DateUtils.getWasteTime(startTime));

	}

	private AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly,
			Policy entity) {
		// 构建附件控件
		String ptype = "contractLabour.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(EntityAction.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype,
					entity.getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(forceReadonly ? true : this.isReadonly());
		return attachsUI;
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
		json.put("msg", getText("policy.renew.success"));
		this.initForm(true);
		this.formPageOption = buildFormPageOption(true);
		this.setE(newPolicy);
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false, newPolicy);
		return "formr";
	}

	// ========车保续保代码结束========

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
		json.put("msg", getText("policy.surrender.success"));
		return "json";
	}

	// ========车保停保代码结束========

	
	// ========车保注销代码开始========
	public String doLogout() throws Exception{		
		Long fromPolicyId = this.getId();
		this.policyService.doLogout(fromPolicyId);
		json = new Json();
		json.put("msg", getText("policy.logout.success"));
		return "json";
	}
	
	
	// ========车保注销代码结束========
}