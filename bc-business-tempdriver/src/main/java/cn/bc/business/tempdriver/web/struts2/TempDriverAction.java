package cn.bc.business.tempdriver.web.struts2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commontemplate.util.JSONUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;

import cn.bc.business.OptionConstants;
import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.business.tempdriver.service.TempDriverService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.ConstraintViolationException;
import cn.bc.core.exception.CoreException;
import cn.bc.core.exception.NotExistsException;
import cn.bc.core.exception.PermissionDeniedException;
import cn.bc.core.util.DateUtils;
import cn.bc.core.util.StringUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.web.ui.html.AttachWidget;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.workflow.flowattach.domain.FlowAttach;
import cn.bc.workflow.flowattach.service.FlowAttachService;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkspaceServiceImpl;

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
	private OptionService optionService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	private FlowAttachService flowAttachService;

	public List<Map<String, String>> list_WorkExperience; // 工作经历集合
	public List<Map<String, String>> list_Family; // 家庭成员集合
	public List<Map<String, Object>> list_WorkflowModuleRelation; // 工作流程集合
	public JSONArray companyNames; // 公司名称列表

	public Integer creditStatus;// 信誉档案更新的状态 控制
	public AttachWidget attachsUI;
	
	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Autowired
	public void setTempDriverService(TempDriverService tempDriverService) {
		this.tempDriverService = tempDriverService;
		this.setCrudService(tempDriverService);
	}

	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}
	
	@Autowired
	public void setFlowAttachService(FlowAttachService flowAttachService) {
		this.flowAttachService = flowAttachService;
	}

	@Override
	public boolean isReadonly() {
		// 司机招聘管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.tempDriver"),
				getText("key.role.bc.admin"));
	}

	public boolean isAdvancedRead() {

		// 司机招聘高级查询角色
		SystemContext context = (SystemContext) this.getContext();
		return context
				.hasAnyRole(getText("key.role.bs.tempDriver.read.advanced"));
	}

	@Override
	protected void afterCreate(TempDriver entity) {
		super.afterCreate(entity);
		entity.setSex(TempDriver.SEX_MAN);
		entity.setStatus(TempDriver.STATUS_RESERVE);
		entity.setUid(this.getIdGeneratorService().next(TempDriver.KEY_UID));
		entity.setRegisterDate(Calendar.getInstance());
	}

	@Override
	protected void beforeSave(TempDriver entity) {
		super.beforeSave(entity);
		if (entity.getCredit() != null && entity.getCredit() != "")
			entity.setCredit(StringUtils.compressHtml(entity.getCredit()));

		// 信誉档案已更新时
		if (creditStatus.equals(2)) {
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

		if (!this.isReadonly()&&editable) {
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.tempDriverForm.save").setId("tempDriverSave"));
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		TempDriver td = this.getE();

		this.buildAttachsUI();

		// 设置信誉档案更新的控制值
		creditStatus = 1;
		// 初始化集合
		list_WorkExperience = parseListStr(td.getListWorkExperience());
		list_Family = parseListStr(td.getListFamily());
		if (!td.isNew()) {
			list_WorkflowModuleRelation = this.workflowModuleRelationService
					.findList(td.getId(), TempDriver.WORKFLOW_MTYPE,
							new String[] { "isPass", "isGiveUp" , "subject" });
		}
		
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = optionService
				.findOptionItemByGroupKeys(new String[] {OptionConstants.COMPANY_NAME });
		
		// 公司名称列表
		companyNames = OptionItem.toLabelValues(optionItems
						.get(OptionConstants.COMPANY_NAME));
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

		if (!unique) {
			TempDriver td = this.tempDriverService
					.loadByCertIdentity(certIdentity);
			json.put("id", td.getId());
		}
		this.json = json.toString();
		return "json";
	}

	// ---流程关联---开始---
	public String tdIds;// 招聘司机ID
	public String flowKey;// 流程编码
	public Boolean flagStatus;// 是否更新司机状态为审批中的控制

	public String startFlow() {
		Json json = new Json();
		
		//服务资格证流程特殊的处理
		if(this.flowKey.endsWith(getText("tempDriverWorkFlow.startFlow.key2"))){
			this.startFlow4RequestServiceCertificate(json,getText("tempDriverWorkFlow.startFlow.key2"));
			this.json = json.toString();
			return "json";
		}
		
		// 去掉最后一个逗号
		String[] _ids = tdIds.substring(0, tdIds.lastIndexOf(",")).split(",");
		String procInstIds = this.tempDriverService.doStartFlow(flowKey,
				StringUtils.stringArray2LongArray(_ids),
				flagStatus != null ? flagStatus : false);
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
				/*json.put("offerStatus",
						getText("tempDriverWorkFlow.offerStatus.check"));*/
				json.put("startTime",
						DateUtils.formatCalendar2Minute(Calendar.getInstance()));
			}
		}
		this.json = json.toString();
		return "json";
	}
	
	public String listDriver;
	private void startFlow4RequestServiceCertificate(Json json,String key){
		if(this.listDriver==null||this.listDriver.length()==0){
			json.put("success", false);
			json.put("msg", "listDrivers is null!");
			return;
		}
		
		String subject="";
		String driverIds="";
		try{
			JSONArray jsons = new JSONArray(listDriver);		
			for(int i=0;i<jsons.length();i++){
				subject+=jsons.getJSONObject(i).getString("name");
				driverIds+=jsons.getJSONObject(i).getString("id");
				if(i+1<jsons.length()){
					subject+="、";
					driverIds+=",";
				}
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		
		subject+=getText("tempDriverWorkFlow.subject.rdc");
		
		json.put("pid", this.tempDriverService.doStartFlow(driverIds,key,subject, this.listDriver));
		json.put("success", true);
	}
	
	public Long driverId;
	/*
	        发起服务资格证流程的验证 
	 */
	public String requestServiceCertificateValidate(){
		Json json = new Json();
		if(this.driverId==null){
			json.put("validate", false);
			json.put("validate_lost_type", 0);
			json.put("msg", "driverId is null!");
			this.json=json.toString();
			return "json";
		}
		
		String carManEntryKey=getText("tempDriverWorkFlow.startFlow.key");
		
		//不存在流程关系
		if(!this.workflowModuleRelationService.hasRelation4Key(driverId
				,TempDriver.WORKFLOW_MTYPE,carManEntryKey)){
			json.put("validate", false);
			json.put("validate_lost_type", 1);
			this.json=json.toString();
			return "json";
		}
		
		//获取最新的司机入职流程相关参数
		Map<String,Object> wmr=
				this.workflowModuleRelationService.findList(driverId, TempDriver.WORKFLOW_MTYPE
				,carManEntryKey,new String[] {"isGiveUp","isPass","isPairDriver","pairDriverName","pairDriverNameId" }).get(0);
		
		//设置流程的信息
		json.put("pid", wmr.get("pid").toString());
		json.put("pname", wmr.get("name").toString());
		
		//流程状态非已结束
		if(!(WorkspaceServiceImpl.COMPLETE==Integer.valueOf(wmr.get("status").toString()))){
			json.put("validate", false);
			json.put("validate_lost_type", 2);
			this.json=json.toString();
			return "json";
		}
		
		//放弃入职审批
		if("1".equals(wmr.get("isGiveUp").toString())){
			json.put("validate", false);
			json.put("validate_lost_type", 3);
			this.json=json.toString();
			return "json";
		}
		
		//入职审批流程不通过
		if("0".equals(wmr.get("isPass").toString())){
			json.put("validate", false);
			json.put("validate_lost_type", 4);
			this.json=json.toString();
			return "json";
		}
		
		//入职通过，但没有对班
		if("0".equals(wmr.get("isPairDriver").toString())){
			json.put("validate", true);
			json.put("isPairDriver", false);
			this.json=json.toString();
			return "json";
		}
		
		//对班司机
		Long pairDriverNameId = Long.valueOf(wmr.get("pairDriverNameId").toString());
		
		this.pairDriverValidate(json, pairDriverNameId, carManEntryKey);
		this.json=json.toString();
		
		return "json";
	}
	
	//对班司机的司机入职审批流程验证
	private void pairDriverValidate(Json json,Long id,String carManEntryKey){
		json.put("isPairDriver", true);
		TempDriver pairDriver=this.tempDriverService.load(id);
		//设置对班司机信息
		json.put("pair_id", id.toString());
		json.put("pair_name", pairDriver.getName());
		json.put("pair_applyAttr", pairDriver.getApplyAttr());//申请属性
		//设置对班司机的身份证很从业资格证
		json.put("pair_certIdentity", pairDriver.getCertIdentity());
		json.put("pair_certCYZG", pairDriver.getCertCYZG());
		
		boolean validate=true;
		
		//不存在流程关系
		if(!this.workflowModuleRelationService.hasRelation4Key(id,TempDriver.WORKFLOW_MTYPE,carManEntryKey)){
			validate=false;
			json.put("validate", validate);
			json.put("validate_pair_lost_type", 1);//没有流程关系
		}else{
			//获取对班司机最新的司机入职流程相关参数
			Map<String,Object> wmr4pair=this.workflowModuleRelationService.findList(id, TempDriver.WORKFLOW_MTYPE
					,carManEntryKey,new String[] { "isGiveUp","isPass","isPairDriver","pairDriverName","pairDriverNameId"}).get(0);
		
			//设置对班司机最新参与的流程信息
			json.put("pair_pid", wmr4pair.get("pid").toString());
			json.put("pair_pname", wmr4pair.get("name").toString());
			
			
			//流程未结束
			if(!(WorkspaceServiceImpl.COMPLETE==Integer.valueOf(wmr4pair.get("status").toString()))){
				validate=false;
				json.put("validate", validate);
				json.put("validate_pair_lost_type", 2);
			}
			
			//放弃入职审批
			if("1".equals(wmr4pair.get("isGiveUp").toString())){
				validate=false;
				json.put("validate", validate);
				json.put("validate_pair_lost_type", 3);
			}
			
			//入职不通过
			if("0".equals(wmr4pair.get("isPass").toString())){
				validate=false;
				json.put("validate", validate);
				json.put("validate_pair_lost_type", 4);
			}
			
			json.put("validate", validate);
		}
		
		if(!validate){
			//查找其他司机的入职审批流程与当前司机的关联性
			this.findOtherRelation(json, carManEntryKey);
		}
	}
	
	//查找其他司机的入职审批流程与当前司机的关联性
	private void findOtherRelation(Json json,String carManEntryKey){
		//查找其他司机的入职审批流程
		List<Map<String,Object>> others=this.workflowModuleRelationService.findList(null, TempDriver.WORKFLOW_MTYPE
				,carManEntryKey,new String[] { "isGiveUp","isPass","isPairDriver","pairDriverName","pairDriverNameId","tempDriver_id"});
		for(Map<String,Object> wmr:others){
				if("1".equals(wmr.get("isPairDriver").toString())//有选择对班
						&&wmr.get("pairDriverNameId").toString().equals(String.valueOf(this.driverId))
						&&wmr.get("status").toString().equals(String.valueOf(WorkspaceServiceImpl.COMPLETE))
						&&"0".equals(wmr.get("isGiveUp").toString())
						&&"1".equals(wmr.get("isPass").toString())){
					//新的对班司机
					TempDriver pairDriver=this.tempDriverService.load(Long.valueOf(wmr.get("tempDriver_id").toString()));
					//设置新的对班司机信息
					json.put("pair_id", pairDriver.getId().toString());
					json.put("pair_name", pairDriver.getName());
					json.put("pair_applyAttr", pairDriver.getApplyAttr());//申请属性
					//设置对班司机的身份证很从业资格证
					json.put("pair_certIdentity", pairDriver.getCertIdentity());
					json.put("pair_certCYZG", pairDriver.getCertCYZG());
					
					//设置流程信息
					json.put("pair_pid", wmr.get("pid").toString());
					json.put("pair_pname", wmr.get("name").toString());
					
					json.put("validate", true);
					json.put("validate_pair_lost_type", 0);
					//跳出循环
					break;
				}
			
		}
		
	}
	
	public String procinstId;//流程id
	public String procinstTaskId;//任务id
	public String templateCode;//模板编码
	
	//添加 添加服务资格证申领表 或 免摇珠申请书附件到流程
	public String addWorkflowAttachFromTemplate() throws Exception{
		Attach attach =this.tempDriverService.doGetAttachFromTemplate(this.driverId, this.templateCode);
		TempDriver tempDriver=this.tempDriverService.load(this.driverId);
		String attachPath=Attach.DATA_REAL_PATH + File.separator+attach.getPath();
		// 模板文件扩展名
		String extension = org.springframework.util.StringUtils.getFilenameExtension(attach.getPath());
		// 声明当前日期时间
		Calendar now = Calendar.getInstance();
		// 文件存储的相对路径（年月），避免超出目录内文件数的限制
		String subFolder = DateUtils.formatCalendar(now, "yyyyMM");
		// 上传文件存储的绝对路径
		String appRealDir = Attach.DATA_REAL_PATH + File.separator + FlowAttach.DATA_SUB_PATH;
		// 所保存文件所在的目录的绝对路径名
		String realFileDir = appRealDir +  File.separator + subFolder;
		// 不含路径的文件名
		String fileName = DateUtils.formatCalendar(now, "yyyyMMddHHmmssSSSS") + "." + extension;
		// 所保存文件的绝对路径名
		String realFilePath = realFileDir +  File.separator + fileName;
		// 构建文件要保存到的目录
		File _fileDir = new File(realFileDir);
		if (!_fileDir.exists()) {
			if (logger.isFatalEnabled())
				logger.fatal("mkdir=" + realFileDir);
			_fileDir.mkdirs();
		}
		// 直接复制附件
		if (logger.isInfoEnabled())
			logger.info("pure copy file");
		try {
			FileCopyUtils.copy(new FileInputStream(attachPath), new FileOutputStream(
					realFilePath));
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}

		// 插入流程附件记录信息
		FlowAttach flowAttach = new FlowAttach();
		flowAttach.setUid(this.getIdGeneratorService().next(FlowAttach.ATTACH_TYPE));
		flowAttach.setType(FlowAttach.TYPE_ATTACHMENT); // 类型：1-附件，2-意见
		flowAttach.setPid(this.procinstId); // 流程id
		String path = subFolder + "/" + fileName; // 文件夹加文件名路径
		if (path.length() > 0) {
			flowAttach.setPath(path); // 附件路径，物理文件保存的相对路径
			flowAttach.setExt(extension); // 扩展名
		}
		flowAttach.setSubject(attach.getSubject()+"("+tempDriver.getName()+")"); // 标题
		if(this.procinstTaskId==null){
			flowAttach.setCommon(true); // 公共信息
		}else{
			flowAttach.setCommon(false);
			flowAttach.setTid(this.procinstTaskId);
		}
		
		flowAttach.setSize(attach.getSize());
		flowAttach.setFormatted(false);// 附件是否需要格式化


		// 创建人,最后修改人信息
		SystemContext context = SystemContextHolder.get();
		flowAttach.setAuthor(context.getUserHistory());
		flowAttach.setModifier(context.getUserHistory());
		flowAttach.setFileDate(Calendar.getInstance());
		flowAttach.setModifiedDate(Calendar.getInstance());

		this.flowAttachService.save(flowAttach);
		
		
		Json json=new Json();

		json.put("success", true);
		json.put("id", flowAttach.getId());
		json.put("size", flowAttach.getSize());
		json.put("subject", flowAttach.getSubject());
		json.put("ext",flowAttach.getExt());
		json.put("path", flowAttach.getPath());
		json.put("uid", flowAttach.getUid());
		json.put("formatted", flowAttach.getFormatted());
		json.put("author",context.getUserHistory().getName());
		json.put("fileDate",DateUtils.formatCalendar2Second(Calendar.getInstance()));
		json.put("msg", getText("tempDriverWorkFlow.flowAttach.success"));
		this.json=json.toString();
		return "json";
	}

	// ---流程关联--结束---

	// 构建附件UI
	private void buildAttachsUI() {
		attachsUI = this.buildAttachsUI(this.getE().isNew(), this.isReadonly(),
				TempDriver.ATTACH_TYPE, this.getE().getUid());

		// 设置最大附件数量控制
		attachsUI.setMaxCount(20);

		if (!attachsUI.isReadOnly())
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
		return this.tempDriverService.doAddAttachFromTemplate(this.getId(),
				this.tpl);
	}

	// 更新状态
	public Integer status;

	public String updateStatus() throws Exception {
		Json _json = new Json();
		try {
			if (status == null) {
				throw new CoreException("must set property status");
			} else if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.tempDriverService.doUpdateStatus(ids, status);
			} else {
				throw new CoreException("must set property id or ids");
			}

			_json.put("success", true);
			_json.put("msg", getText("form.delete.success"));
			json = _json.toString();
			return "json";
		} catch (PermissionDeniedException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (NotExistsException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (ConstraintViolationException e) {
			// 违反约束关联引发的异常
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (Exception e) {
			// 其他异常
			dealOtherDeleteException(_json, e);
		}
		_json.put("success", false);
		json = _json.toString();
		return "json";
	}

	// 更新面试日期
	public String interviewDate;

	public String updateInterviewDate() throws Exception {
		Json _json = new Json();
		try {
			if (interviewDate == null) {
				throw new CoreException("must set property interviewDate");
			} else if (this.getIds() != null && this.getIds().length() > 0) {
				Long[] ids = cn.bc.core.util.StringUtils
						.stringArray2LongArray(this.getIds().split(","));
				this.tempDriverService.doUpdateInterviewDate(ids,
						DateUtils.getCalendar(interviewDate));
			} else {
				throw new CoreException("must set property id or ids");
			}

			_json.put("success", true);
			_json.put("msg", getText("form.delete.success"));
			json = _json.toString();
			return "json";
		} catch (PermissionDeniedException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (NotExistsException e) {
			// 执行没有权限的操作
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (ConstraintViolationException e) {
			// 违反约束关联引发的异常
			_json.put("msg", getDeleteExceptionMsg(e));
			_json.put("e", e.getClass().getSimpleName());
		} catch (Exception e) {
			// 其他异常
			dealOtherDeleteException(_json, e);
		}
		_json.put("success", false);
		json = _json.toString();
		return "json";
	}

}
