package cn.bc.business.motorcade.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 车队信息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class MotorcadeAction extends FileEntityAction<Long, Motorcade> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	private String MANAGER_KEY = getText("key.role.bs.motorcade");// 车队管理角色的编码
	private OptionService optionService;
	private MotorcadeService motorcadeService;
	public List<OptionItem> paymentDates; // 可选缴费日列表

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
		this.setCrudService(motorcadeService);
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(MANAGER_KEY, getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(Motorcade entity) {
		super.afterCreate(entity);
		Motorcade e = this.getE();

		// 所属单位
		e.setUnit(this.getSystyemContext().getUnit());
		e.setUid(this.getIdGeneratorService().next(Motorcade.KEY_UID));
		e.setType(Motorcade.TYPE_TEAM);

		// 初始状态
		e.setStatus(BCConstants.STATUS_ENABLED);
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(this.getDefaultSaveButtonOption());
			if (!this.getE().isNew()) {
				pageOption.addButton(new ButtonOption(
						getText("motorcade.historicalInformation"), null,
						"bc.business.motorcadeForm.check"));
			}
		}
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(620)
				.setMinWidth(400).setHeight(360).setMinHeight(300);
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 加载缴费日列表
		this.paymentDates = this.optionService
				.findOptionItemByGroupKey(OptionConstants.MOTORCADE_PAYMENT_DATE);
	}


	@Override
	protected void beforeSave(Motorcade entity) {
		super.beforeSave(entity);
		// 处理所属父类
		Motorcade e = this.getE();
		if (e.getParent() != null && e.getParent().getId() == null)
			e.setParent(null);
	}

	@Override
	public String delete() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", new Integer(BCConstants.STATUS_DISABLED));
		attributes.put("modifier", context.getUserHistory());
		attributes.put("modifiedDate", Calendar.getInstance());

		if (this.getId() != null) {// 处理一条
			this.motorcadeService.update(this.getId(), attributes);
		} else {// 处理一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.motorcadeService.update(ids, attributes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		Json json = new Json();
		json.put("msg", getText("form.disabled.success"));
		this.json = json.toString();
		return "json";
	}
}
