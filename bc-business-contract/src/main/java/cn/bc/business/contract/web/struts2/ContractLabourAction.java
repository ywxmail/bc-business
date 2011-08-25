/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.cert.service.CertService;
import cn.bc.business.contract.domain.Contract;
import cn.bc.business.contract.domain.Contract4Labour;
import cn.bc.business.contract.service.ContractLabourService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.RichEntityImpl;
import cn.bc.core.query.condition.impl.OrderCondition;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.HtmlPage;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 司机劳动合同Action
 * 
 * @author wis.ho
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ContractLabourAction extends FileEntityAction<Long, Contract4Labour> {
	// private static Log logger = LogFactory.getLog(ContractAction.class);
	private static final long 		serialVersionUID 			= 1L;
	public 	ContractLabourService 	contractLabourService;
	public	CertService				certService;
	private AttachService 			attachService;
	private String					MANAGER_KEY 				= "R_ADMIN";// 管理角色的编码
	public 	boolean 	   			isManager;
	public	Long 					carManId;
	public 	AttachWidget 			attachsUI;
	public 	Map<String,String>		statusesValue;
	

	@Autowired
	public void setContractLabourService(ContractLabourService contractLabourService) {
		this.contractLabourService = contractLabourService;
		this.setCrudService(contractLabourService);
	}
	
	@Autowired
	public void setCertService(CertService certService) {
		this.certService = certService;
	}
	
	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}




	@SuppressWarnings("static-access")
	public String create() throws Exception {
		String r = super.create();
		isManager = isManager();
		this.getE().setUid(this.getIdGeneratorService().next(this.getE().KEY_UID));
		this.getE().setType(Contract.TYPE_LABOUR);
		this.getE().setStatus(RichEntityImpl.STATUS_DISABLED);
		
		attachsUI = buildAttachsUI(true);

		return r;
	}
	
	@Override
	public String edit() throws Exception {
		this.setE(this.getCrudService().load(this.getId()));
		// 表单可选项的加载
		this.formPageOption = 	buildFormPageOption();
		statusesValue		=	this.getEntityStatuses();
		
		// 构建附件控件
		attachsUI = buildAttachsUI(false);
		return "form";
	}
	
	@SuppressWarnings("static-access")
	private AttachWidget buildAttachsUI(boolean isNew) {
		isManager = isManager();
		// 构建附件控件
		String ptype = "contractLabour.main";
		AttachWidget attachsUI = new AttachWidget();
		attachsUI.setFlashUpload(this.isFlashUpload());
		attachsUI.addClazz("formAttachs");
		if (!isNew)
			attachsUI.addAttach(this.attachService.findByPtype(ptype, this
					.getE().getUid()));
		attachsUI.setPuid(this.getE().getUid()).setPtype(ptype);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));
		attachsUI.setReadOnly(!this.getE().isNew());
		return attachsUI;
	}

	@Override
	protected PageOption buildFormPageOption() {
		PageOption option = new PageOption().setWidth(750).setMinWidth(250)
				.setMinHeight(160).setModal(false);
		option.addButton(new ButtonOption(getText("label.save"), "save"));
		return option;
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}

	@Override
	protected OrderCondition getDefaultOrderCondition() {
		return null;// new OrderCondition("fileDate", Direction.Desc);
	}

	@Override
	protected PageOption buildListPageOption() {
		return super.buildListPageOption().setWidth(800).setMinWidth(300)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String[] getSearchFields() {
		return new String[] { "name", "description" };
	}
	
	
	// 判断当前用户是否是本模块管理员
	private boolean isManager() {
		return ((SystemContext) this.getContext()).hasAnyRole(MANAGER_KEY);
	}
	
	public Json json;
	public String carManMess(){
		//CarMan carMan = this.certService;
		return "json";
	}
	
	@Override
	protected HtmlPage buildHtml4Paging() {
		HtmlPage page = super.buildHtml4Paging();
		if (carManId != null)
			page.setAttr("data-extras", new Json().put("carManId", carManId)
					.toString());
		return page;
	}


}