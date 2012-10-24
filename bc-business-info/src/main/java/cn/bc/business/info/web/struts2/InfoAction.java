/**
 * 
 */
package cn.bc.business.info.web.struts2;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.info.domain.Info;
import cn.bc.business.info.service.InfoService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.CoreException;
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
public class InfoAction extends FileEntityAction<Long, Info> implements
		SessionAware {
	// private static Log logger = LogFactory.getLog(InfoAction.class);
	private static final long serialVersionUID = 1L;
	protected IdGeneratorService idGeneratorService;
	protected AttachService attachService;
	protected InfoService infoService;
	public String statusDesc;

	public InfoAction() {
		super();
		this.setEntityClass(Info.class);
	}

	@Autowired
	public void setInfoService(InfoService infoService) {
		this.infoService = infoService;
		this.setCrudService(infoService);
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
	protected int getType() {
		throw new CoreException("unimplement method.");
	}

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
		if (editable)
			return super.buildFormPageOption(editable).setWidth(680)
					.setHeight(450);
		else
			return super.buildFormPageOption(editable).setWidth(650)
					.setHeight(400);
	}

	@Override
	protected void buildFormPageButtons(PageOption option, boolean editable) {
		// 非编辑状态没有任何操作按钮
		if (!editable)
			return;

		// 管理员
		if (!this.isReadonly()) {
			option.addButton(new ButtonOption(getText("label.preview"), null,
					"bs.companyFileForm.preview"));
			option.addButton(this.getDefaultSaveButtonOption());
		}
	}

	// 发布
	public String doIssue() throws Exception {
		Json json = new Json();
		json.put("id", getId());
		try {
			this.infoService.doIssue(this.getId());
			json.put("success", true);
			json.put("msg", getText("info.issueSuccess"));
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		this.json = json.toString();
		return "json";
	}

	// 禁用
	public String doDisabled() throws Exception {
		Json json = new Json();
		json.put("id", getId());
		try {
			this.infoService.doDisabled(this.getId());
			json.put("success", true);
			json.put("msg", getText("info.disabledSuccess"));
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		this.json = json.toString();
		return "json";
	}

	public AttachWidget attachsUI;

	@Override
	protected void afterEdit(Info entity) {
		// 构建附件控件
		attachsUI = buildAttachsUI(false, false);
	}

	public String typeDesc;

	@Override
	protected void afterOpen(Info e) {
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

	/**
	 * 获取状态值转换列表
	 * 
	 * @return
	 */
	public Map<String, String> getStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(Info.STATUS_DRAFT),
				getText("info.status.draft"));
		statuses.put(String.valueOf(Info.STATUS_ISSUED),
				getText("info.status.issued"));
		statuses.put(String.valueOf(Info.STATUS_DISABLED),
				getText("info.status.disadled"));
		return statuses;
	}
}
