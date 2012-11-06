package cn.bc.business.motorcade.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.motorcade.domain.HistoryCarQuantity;
import cn.bc.business.motorcade.service.HistoryCarQuantityService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 查看历史车辆数Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class HistoryCarQuantityAction extends
		FileEntityAction<Long, HistoryCarQuantity> {
	private static final long serialVersionUID = 1L;
	private HistoryCarQuantityService historyCarQuantityService;
	private MotorcadeService motorcadeService;
	
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	
	@Autowired
	public void setHistoryCarQuantityService(
			HistoryCarQuantityService historyCarQuantityService) {
		this.setCrudService(historyCarQuantityService);
		this.historyCarQuantityService = historyCarQuantityService;
	}
	
	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Override
	public boolean isReadonly() {
		// 车队管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.motorcade"),
				getText("key.role.bc.admin"));
	}
	
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(450)
				.setMinWidth(250).setHeight(260).setMinHeight(250);
	}
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (!this.isReadonly()) {
			if (editable) {// 可编辑时显示保存按钮
				pageOption.addButton(getDefaultSaveButtonOption());
				pageOption.addButton(new ButtonOption(
						getText("historyCarQuantity.saveAndClose"), null,
						"bs.historyCarQuantityForm.saveAndClose")
						.setId("historyCarQuantitySaveAndClose"));
			} else {// open时
					// 维护
					pageOption.addButton(new ButtonOption(
							getText("historyCarQuantity.doMaintenance"), null,
							"bs.historyCarQuantityForm.doMaintenance")
							.setId("historyCarQuantityDoMaintenance"));
				
			}
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcade() != null) {
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcade().getId().toString(), this.getE()
					.getMotorcade().getName());
		}
	}
	
	
}
