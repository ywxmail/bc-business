package cn.bc.business.tempdriver.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commontemplate.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.business.tempdriver.service.TempDriverService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 司机招聘信息表单Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class TempDriverAction extends FileEntityAction<Long, TempDriver> {
	// private static Log logger = LogFactory.getLog(MotorcadeAction.class);
	private static final long serialVersionUID = 1L;
	private TempDriverService tempDriverService;
	public List<Map<String, String>> list_WorkExperience; // 工作经历集合
	public List<Map<String, String>> list_Family; // 家庭成员集合
	
	public Integer creditStatus;//信誉档案更新的状态 控制
	public AttachWidget attachsUI;

	@Autowired
	public void setArrangeService(TempDriverService tempDriverService) {
		this.tempDriverService = tempDriverService;
		this.setCrudService(tempDriverService);
	}

	@Override
	public boolean isReadonly() {
		// 司机招聘管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.tempDriver"),
				getText("key.role.bc.admin"));
	}
	
	public boolean isAdvancedRead(){
		// 司机招聘高级查询角色
		SystemContext context = (SystemContext) this.getContext();
		return context.hasAnyRole(getText("key.role.bs.tempDriver.read.advanced"));
	}

	@Override
	protected void afterCreate(TempDriver entity) {
		super.afterCreate(entity);
		entity.setSex(TempDriver.SEX_MAN);
		entity.setStatus(TempDriver.STATUS_RESERVE);
		entity.setUid(this.getIdGeneratorService().next(TempDriver.KEY_UID));
	}

	@Override
	protected void beforeSave(TempDriver entity) {
		super.beforeSave(entity);
		if (entity.getCredit() != null && entity.getCredit() != "")
			entity.setCredit(StringUtils.compressHtml(entity.getCredit()));
		
		//信誉档案已更新时
		if(creditStatus.equals(2)){
			entity.setCreditDate(Calendar.getInstance());
		}

	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		PageOption option = super.buildFormPageOption(editable);
		option.setWidth(675).setMinWidth(500).setHeight(420).setMinHeight(200);
		return option;
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {

		if (!this.isReadonly()) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.tempDriverForm.save").setId("tempDriverSave"));
			/*
			 * pageOption.addButton(new ButtonOption(
			 * getText("invoice.saveAndClose"), null,
			 * "bs.invoice4SellForm.saveAndClose") .setId("invoice4SellSave"));
			 */
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		TempDriver td = this.getE();
		
		this.buildAttachsUI();
		
		//设置信誉档案更新的控制值
		creditStatus=1;
		
		// 初始化集合
		list_WorkExperience = parseListStr(td.getListWorkExperience());
		list_Family = parseListStr(td.getListFamily());
	}

	// 解释json数组字符串为集合
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> parseListStr(String s) throws Exception {
		if (s == null || s.length() == 0)
			return new ArrayList<Map<String, String>>();

		List<Map<String, String>> lm = new ArrayList<Map<String, String>>();
		Map<String, String> m;
		Map<String, String> _m;
		JSONArray jsonArray = new JSONArray(s);
		JSONObject jsonObj;
		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObj = jsonArray.getJSONObject(i);
			_m = JSONUtils.fromJsonToMap(jsonObj.toString());
			m = new HashMap<String, String>();
			for (String key : _m.keySet()) {
				m.put(key, jsonObj.getString(key));
			}
			lm.add(m);
		}
		return lm;
	}

	// 身份证唯一性检查
	public String certIdentity;
	public String tdId;

	public String isUniqueCertIdentity() {
		Json json = new Json();
		boolean unique = this.tempDriverService.isUniqueCertIdentity(
				tdId == null || tdId == "" || tdId.length() == 0 ? null : Long
						.valueOf(tdId), certIdentity);
		json.put("unique", unique);
		
		if (!unique){
			TempDriver td = this.tempDriverService
					.loadByCertIdentity(certIdentity);
			json.put("id", td.getId());
		}
		this.json = json.toString();
		return "json";
	}

	// ---发起流程---开始---
	public String tdIds;

	public String startFlow() {
		Json json = new Json();
		// 去掉最后一个逗号
		String[] _ids = tdIds.substring(0, tdIds.lastIndexOf(",")).split(",");
		String procInstIds = this.tempDriverService.doStartFlow(
				getText("tempDriverWorkFlow.startFlow.key"),
				StringUtils.stringArray2LongArray(_ids));
		if (procInstIds == "") {
			json.put("success", false);
			json.put("msg",
					getText("tempDriverWorkFlow.startFlow.success.false"));
		} else {
			json.put("success", true);
			json.put("msg",
					getText("tempDriverWorkFlow.startFlow.success.true"));
			// 发起一个时候返回信息
			String[] _procInstIds = procInstIds.substring(0,
					procInstIds.lastIndexOf(",")).split(",");
			if (_procInstIds.length == 1) {
				json.put("procInstId", _procInstIds[0]);
				json.put("offerStatus",
						getText("tempDriverWorkFlow.offerStatus.check"));
				json.put("startTime",
						DateUtils.formatCalendar2Minute(Calendar.getInstance()));
			}
		}
		this.json = json.toString();
		return "json";
	}
	// ---发起流程结束---

	//构建附件UI
	private void buildAttachsUI() {
		attachsUI=this.buildAttachsUI(this.getE().isNew(), this.isReadonly()
				, TempDriver.ATTACH_TYPE, this.getE().getUid());
		
		//设置最大附件数量控制
		attachsUI.setMaxCount(20);
		
		if(!attachsUI.isReadOnly())
			attachsUI.addHeadButton(AttachWidget.createButton("添加模板", null,
					"bs.tempDriverForm.addAttachFromTemplate", null));// 添加模板
		
		
		attachsUI.addHeadButton(AttachWidget
				.defaultHeadButton4DownloadAll(null));// 打包下载
		
		if (!attachsUI.isReadOnly()) 
			attachsUI.addHeadButton(AttachWidget
					.defaultHeadButton4DeleteAll(null));// 删除

	}
	
	// 从模板添加附件
	@Override
	protected Attach buildAttachFromTemplate() throws Exception {
		return this.tempDriverService.doAddAttachFromTemplate(
				this.getId(), this.tpl);
	}
	
	
}
