/**
 * 
 */
package cn.bc.business.injury.web.struts2;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.injury.domain.Injury;
import cn.bc.business.injury.service.InjuryService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 工伤Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InjuryAction extends FileEntityAction<Long, Injury> {
	// private static Log logger = LogFactory.getLog(InjuryAction.class);
	private static final long serialVersionUID = 1L;
	public InjuryService injuryService;
	public boolean isManager;
	public Long contractId;

	public Map<String, String> statusesValue;

	@Autowired
	public void setInjuryService(InjuryService injuryService) {
		this.injuryService = injuryService;
		this.setCrudService(injuryService);
	}

	@Override
	public boolean isReadonly() {
		// 合同管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.contract4charger"),
				getText("key.role.bs.contract4labour"),
				getText("key.role.bc.admin"));
	}
	
	@SuppressWarnings("static-access")
	@Override
	protected void afterCreate(Injury entity){
		super.afterCreate(entity);
		
		entity.setStatus(Injury.STATUS_NORMAL);
		entity.setContractId(this.contractId);
		// 自动生成UID
		entity.setUid(this.getIdGeneratorService().next(this.getE().KEY_UID));
		// 自动生成合同编号
		entity.setCode(this.getIdGeneratorService().nextSN4Month(Injury.KEY_CODE));
		
	}
	
	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 状态列表
		statusesValue		=	this.getEntityStatuses();
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return	super.buildFormPageOption(editable).setWidth(735).setHeight(360)
				.setMinWidth(250).setMinHeight(170);
	}


}