package cn.bc.business.insuranceType.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.business.insuranceType.service.InsuranceTypeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.Entity;
import cn.bc.core.exception.CoreException;
import cn.bc.core.query.condition.Direction;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 车辆保单险种Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InsuranceTypeAction extends FileEntityAction<Long, InsuranceType> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	public InsuranceTypeService insuranceTypeService;

	@Autowired
	public void setInsuranceTypeService(
			InsuranceTypeService insuranceTypeService) {
		this.insuranceTypeService = insuranceTypeService;
		this.setCrudService(insuranceTypeService);
	}

	@Override
	public boolean isReadonly() {
		// 车辆保单险种管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.insuranceType"),
				getText("key.role.bc.admin"));
	}

	@Override
	public String create() throws Exception {
		String r = super.create();
		InsuranceType e = this.getE();
		// 初始状态
		e.setStatus(Entity.STATUS_ENABLED);
		return r;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(500).setMinWidth(300)
				.setMinHeight(250).setModal(false);

		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.save"), "save"));
		}
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return new OrderCondition("status", Direction.Asc).add("code",
				Direction.Asc);
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(600).setMinWidth(300)
				.setHeight(500).setMinHeight(300);
	}

	public Json json;

	@Override
	public String delete() throws Exception {
		SystemContext context = this.getSystyemContext();
		// 将状态设置为禁用而不是物理删除,更新最后修改人和修改时间
		Map<String, Object> attributes = new HashMap<String, Object>();
		attributes.put("status", new Integer(Entity.STATUS_DISABLED));
		attributes.put("modifier", context.getUserHistory());
		attributes.put("modifiedDate", Calendar.getInstance());

		if (this.getId() != null) {// 处理一条
			this.insuranceTypeService.update(this.getId(), attributes);
		} else {// 处理一批
			if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.insuranceTypeService.update(ids, attributes);
			} else {
				throw new CoreException("must set property id or ids");
			}
		}
		json = new Json();
		json.put("msg", getText("form.disabled.success"));
		return "json";
	}

}
