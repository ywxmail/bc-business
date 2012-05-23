/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.fee.template.service.FeeTemplateService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * Action
 * 
 * @author
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeeTemplateAction extends FileEntityAction<Long, FeeTemplate> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long serialVersionUID = 1L;
	private FeeTemplateService feeTemplateService;

	public List<Map<String, String>> payTypeList;// 收费方式列表(每月、每季、每年、一次性)
	public List<Map<String, String>> templateList;// 模板列表

	@Autowired
	public void setFeeTemplateService(FeeTemplateService feeTemplateService) {
		this.feeTemplateService = feeTemplateService;
		this.setCrudService(feeTemplateService);
	}

	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"),
				getText("key.role.bs.fee.template"));
	}

	@Override
	protected void afterCreate(FeeTemplate entity) {
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setType(FeeTemplate.TYPE_FEE);
		entity.setPrice(0f);
		entity.setCount(0);
		super.afterCreate(entity);
	}

	@Override
	protected void afterEdit(FeeTemplate entity) {
		if (entity.getPid() != null) {
			FeeTemplate template = this.feeTemplateService
					.load(entity.getPid());
			OptionItem.insertIfNotExist(this.templateList, template.getId()
					.toString(), template.getName());
		}
		super.afterEdit(entity);
	}

	@Override
	protected void afterOpen(FeeTemplate entity) {
		if (entity.getPid() != null) {
			FeeTemplate template = this.feeTemplateService
					.load(entity.getPid());
			OptionItem.insertIfNotExist(this.templateList, template.getId()
					.toString(), template.getName());
		}
		super.afterOpen(entity);
	}
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.feeTemplateForm.save"));
		}
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(380)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		list.add(this.getListsKeyAndValue(FeeTemplate.PAY_TYPE_MONTH,
				getText("feeTemplate.payType.month")));
		list.add(this.getListsKeyAndValue(FeeTemplate.PAY_TYPE_SEASON,
				getText("feeTemplate.payType.season")));
		list.add(this.getListsKeyAndValue(FeeTemplate.PAY_TYPE_YEAR,
				getText("feeTemplate.payType.year")));
		list.add(this.getListsKeyAndValue(FeeTemplate.PAY_TYPE_ALL,
				getText("feeTemplate.payType.all")));
		payTypeList = list;

		templateList = this.feeTemplateService.getTemplate();

	}

	// 生成OptionItem列表 key、value值
	private Map<String, String> getListsKeyAndValue(Object key, Object value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key.toString());
		map.put("value", value.toString());
		return map;
	}

	
	public Long tid;
	// 检测此模板是否还存在费用单属于它
	public String isTemplateExistFee() {
		Json json = new Json();
		if (this.feeTemplateService.isTemplateExistFee(tid)) {
			json.put("result", "true");
		} else {
			json.put("result", "false");
		}
		this.json = json.toString();
		return "json";
	}

	// 检测此费用单是否属于状态正常的模板，费用单状态从禁用变为正常时检测
	public String isFeeBelong2Template() {
		Json json = new Json();
		if (this.feeTemplateService.isFeeBelong2Template(tid)) {
			json.put("result", "true");
		} else {
			json.put("result", "false");
		}
		this.json = json.toString();
		return "json";
	}

	//禁用
	@Override
	public String delete() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", new Integer(BCConstants.STATUS_DISABLED));
		attributes.put("modifier", context.getUserHistory());
		attributes.put("modifiedDate", Calendar.getInstance());
		if (this.getId() != null) {// 处理一条
			this.feeTemplateService.update(this.getId(), attributes);
		}else {// 处理一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.feeTemplateService.update(ids, attributes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		Json json = new Json();
		json.put("msg", getText("form.disabled.success"));
		this.json=json.toString();
		return "json";
	}
	
	
	//返回一个模板的所属模块
	public String getTemplateModule(){
		Json json = new Json();
		FeeTemplate template=this.feeTemplateService.load(this.tid);
		if(template!=null){
			json.put("result", template.getModule());
		}else{
			json.put("result", "");
		}
		this.json=json.toString();
		return "json";	
	}
	
	public Long fid;
	public String code;
	
	//检测编码唯一
	public String isUniqueCode(){
		Json json = new Json();
		boolean flag=this.feeTemplateService.isUniqueCode(fid, code);
		json.put("result",!flag);
		this.json = json.toString();
		return "json";
	}

}