/**
 * 
 */
package cn.bc.business.info.web.struts2;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.info.domain.Info;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.service.CrudService;
import cn.bc.docs.service.AttachService;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 信息Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class InfoAction extends FileEntityAction<Long, Info> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(InfoAction.class);
	private static final long serialVersionUID = 1L;
	protected IdGeneratorService idGeneratorService;
	protected AttachService attachService;
	public String statusDesc;

	public InfoAction() {
		super();
		this.setEntityClass(Info.class);
	}

	@Autowired
	public void setInfoService(
			@Qualifier(value = "infoService") CrudService<Info> crudService) {
		this.setCrudService(crudService);
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	/**
	 * 信息类型
	 * 
	 * @return
	 */
	protected abstract int getType();

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BC_ADMIN");
	}

	@Override
	protected void afterCreate(Info entity) {
		SystemContext context = (SystemContext) this.getContext();
		Info e = this.getE();
		e.setFileDate(Calendar.getInstance());
		e.setSendDate(e.getFileDate());// 发布日期默认等于创建日期
		e.setSource(context.getBelong().getName());// 来源默认等于当前用户所属部门
		e.setAuthor(context.getUserHistory());

		e.setStatus(Info.STATUS_DRAFT);

		e.setUid(this.idGeneratorService.next("info.uid"));

		e.setType(getType());

		// 构建附件控件
		attachsUI = buildAttachsUI(true, false);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(680)
				.setHeight(400);
	}

	@Override
	protected void buildFormPageButtons(PageOption option, boolean editable) {
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.preview"),
					"preview"));
			if (this.getE().getStatus() == Info.STATUS_DRAFT)
				option.addButton(new ButtonOption(getText("info.issue"), null,
						"bs.companyFileForm.issue"));
			option.addButton(this.getDefaultSaveButtonOption());
		}
	}

	@Override
	protected void beforeSave(Info e) {
		SystemContext context = (SystemContext) this.getContext();
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());
		super.beforeSave(e);
	}

	// 发布
	public String issue() throws Exception {
		SystemContext context = (SystemContext) this.getContext();
		Info e = this.getE();

		// 最后修改人
		e.setModifier(context.getUserHistory());
		e.setModifiedDate(Calendar.getInstance());

		// 发布人
		e.setSendDate(Calendar.getInstance());
		e.setStatus(Info.STATUS_ISSUED);

		this.getCrudService().save(e);

		Json json = new Json();
		json.put("id", e.getId());
		json.put("msg", getText("info.issueSuccess"));
		this.json = json.toString();
		return "json";
	}

	public AttachWidget attachsUI;

	@Override
	protected void afterEdit(Info entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);
	}

	@Override
	protected void afterOpen(Info entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, true);
	}

	protected AttachWidget buildAttachsUI(boolean isNew, boolean forceReadonly) {
		// 构建附件控件
		String ptype = "info.main";
		String puid = this.getE().getUid();
		boolean readonly = forceReadonly ? true : this.isReadonly();
		AttachWidget attachsUI = AttachWidget.defaultAttachWidget(isNew,
				readonly, isFlashUpload(), this.attachService, ptype, puid);

		// 上传附件的限制
		attachsUI.addExtension(getText("app.attachs.extensions"))
				.setMaxCount(Integer.parseInt(getText("app.attachs.maxCount")))
				.setMaxSize(Integer.parseInt(getText("app.attachs.maxSize")));

		return attachsUI;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);

		// 状态描述
		statusDesc = this.getStatuses().get(
				String.valueOf(this.getE().getStatus()));
	}

	@Override
	protected String getJs() {
		return contextPath + "/bc/bulletin/list.js";
	}

	/**
	 * 获取状态值转换列表
	 * 
	 * @return
	 */
	private Map<String, String> getStatuses() {
		Map<String, String> statuses = new HashMap<String, String>();
		statuses.put(String.valueOf(Info.STATUS_DRAFT),
				getText("info.status.draft"));
		statuses.put(String.valueOf(Info.STATUS_ISSUED),
				getText("info.status.issued"));
		statuses.put(String.valueOf(Info.STATUS_ARCHIVED),
				getText("info.status.archived"));
		return statuses;
	}
}
