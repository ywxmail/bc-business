/**
 * 
 */
package cn.bc.business.socialSecurityRule.web.struts2;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.OptionConstants;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRuleDetail;
import cn.bc.business.socialSecurityRule.service.SocialSecurityRuleService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * Action
 * 
 * @author
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SocialSecurityRuleAction extends FileEntityAction<Long, SocialSecurityRule> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private SocialSecurityRuleService socialSecurityRuleService;
	private OptionService optionService;
	
	public String details;//保险明细
	public List<Map<String, String>> houseTypeList; // 可选户口类型列表
	public JSONArray names;//保险明细名称，如养老
	
	@Autowired
	public void setSocialSecurityRuleService(
			SocialSecurityRuleService socialSecurityRuleService) {
		this.socialSecurityRuleService = socialSecurityRuleService;
		this.setCrudService(socialSecurityRuleService);
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"),
				getText("key.role.bs.socialSecurityRule"));
	}

	@Override
	protected void afterCreate(SocialSecurityRule entity) {
		Calendar c=Calendar.getInstance();
		entity.setStartMonth(c.get(Calendar.MONTH)+1);
		entity.setStartYear(c.get(Calendar.YEAR));
		super.afterCreate(entity);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(560)
				.setMinWidth(380).setMinHeight(250);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.socialSecurityRuleForm.save"));
		}
	}
	
	@Override
	protected void beforeSave(SocialSecurityRule entity) {
		super.beforeSave(entity);
		// 插入保险明细
		try {
			Set<SocialSecurityRuleDetail> details = null;

			if (this.details != null && this.details.length() > 0) {
				details = new LinkedHashSet<SocialSecurityRuleDetail>();
				SocialSecurityRuleDetail resource;
				JSONArray jsons = new JSONArray(this.details);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new SocialSecurityRuleDetail();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setSocialSecurityRule(this.getE());
					resource.setName(json.getString("name"));
					resource.setUnitRate(new Float(json.getString("unitRate")));
					resource.setPersonalRate(new Float(json.getString("personalRate")));
					resource.setBaseNumber(new Float(json.getString("baseNumber")));
					resource.setDesc(json.getString("desc"));
					details.add(resource);
				}
			}
			if (this.getE().getSocialSecurityRuleDetail() != null) {
				this.getE().getSocialSecurityRuleDetail().clear();
				this.getE().getSocialSecurityRuleDetail().addAll(details);
			} else {
				this.getE().setSocialSecurityRuleDetail(details);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CARMAN_HOUSETYPE,
						OptionConstants.SOCIALSECURTYRULEDETAIL_NAME});
		
		// 加载可选户口类型列表
		this.houseTypeList =optionItems.get(OptionConstants.CARMAN_HOUSETYPE);
		this.names=OptionItem.toLabelValues(optionItems.get(OptionConstants.SOCIALSECURTYRULEDETAIL_NAME));
		
	}

}