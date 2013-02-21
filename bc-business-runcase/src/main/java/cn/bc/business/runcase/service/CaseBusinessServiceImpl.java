/**
 * 
 */
package cn.bc.business.runcase.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.runcase.dao.CaseBusinessDao;
import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.core.exception.CoreException;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.domain.Attach;
import cn.bc.docs.service.AttachService;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;
import cn.bc.workflow.domain.WorkflowModuleRelation;
import cn.bc.workflow.flowattach.domain.FlowAttach;
import cn.bc.workflow.flowattach.service.FlowAttachService;
import cn.bc.workflow.service.WorkflowModuleRelationService;
import cn.bc.workflow.service.WorkflowService;

/**
 * 营运事件营运违章Service的实现
 * 
 * @author dragon
 */
public class CaseBusinessServiceImpl extends DefaultCrudService<Case4InfractBusiness> implements
		CaseBusinessService {
	private static final Log logger = LogFactory
			.getLog(CaseBusinessServiceImpl.class);
	
	private CaseBusinessDao caseBusinessDao;
	private SyncBaseDao syncBaseDao;
	private WorkflowService workflowService;
	private WorkflowModuleRelationService workflowModuleRelationService;
	private TaskService taskService;
	private MotorcadeService motorcadeService;
	private AttachService attachService;
	private FlowAttachService flowAttachService;
	private IdGeneratorService idGeneratorService;
	
	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Autowired
	public void setFlowAttachService(FlowAttachService flowAttachService) {
		this.flowAttachService = flowAttachService;
	}

	@Autowired
	public void setAttachService(AttachService attachService) {
		this.attachService = attachService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}
	
	@Autowired
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
	
	@Autowired
	public void setWorkflowModuleRelationService(
			WorkflowModuleRelationService workflowModuleRelationService) {
		this.workflowModuleRelationService = workflowModuleRelationService;
	}
	
	public CaseBusinessDao getCaseBusinessDao() {
		return caseBusinessDao;
	}
	
	@Autowired
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}


	public void setCaseBusinessDao(CaseBusinessDao caseBusinessDao) {
		this.caseBusinessDao = caseBusinessDao;
		this.setCrudDao(caseBusinessDao);
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4InfractBusiness save(Case4InfractBusiness e, SyncBase sb) {
		//默认的保存处理
		e = super.save(e);
		if(sb != null){
			//保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	/**
	 * 结案操作
	 * @param fromBusinessId
	 * @param closeDate
	 * @return
	 */
	public Case4InfractBusiness doCloseFile(Long fromBusinessId,
			Calendar closeDate) {
		Case4InfractBusiness business = this.caseBusinessDao.load(fromBusinessId);
		if(business == null)
			throw new CoreException("要处理的营运违章已不存在！businessId=" + fromBusinessId);
		
		//更新营运违章相关信息
		business.setStatus(CaseBase.STATUS_CLOSED);
		
		// 设置创建人信息和最后修改人信息
		SystemContext context = SystemContextHolder.get();
		business.setModifier(context.getUserHistory());
		business.setModifiedDate(Calendar.getInstance());
		
		//设置结案人,结案日期
		business.setCloserId(context.getUserHistory().getId());
		business.setCloserName(context.getUserHistory().getName());
		business.setCloseDate(closeDate);
		
		return this.caseBusinessDao.save(business);
	}
	
	public List<Map<String,String>> doStartFlow(String key, Long[] ids) throws Exception {
		// 声明返回的信息
		List<Map<String,String>> returnValue=new ArrayList<Map<String,String>>();
		
		Map<String,String> returnMap;
		
		// 循环Id数组
		for (Long id : ids) {
			returnMap=new HashMap<String,String>();
			Case4InfractBusiness cib = this.caseBusinessDao.load(id);
			// 声明变量
			Map<String, Object> variables = new HashMap<String, Object>();
			
			//设置任务能够完成办理的条件控制值                                                    车队长任务
			variables.put("completeTaskCodition","t020MotorcadeLeaderCheck_s");
			
			// 发起流程
			String procInstId = this.workflowService.startFlowByKey(key,
					this.returnParam(cib, variables));
			
			// 完成第一步办理
			Task task = this.taskService.createTaskQuery()
					.processInstanceId(procInstId).singleResult();
			this.workflowService.completeTask(task.getId());
			//同步附件到第一个任务
			this.syscAttach(cib, procInstId,task.getId());
			
			// 保存流程与营运违章信息的关系
			WorkflowModuleRelation workflowModuleRelation = new WorkflowModuleRelation();
			workflowModuleRelation.setMid(id);
			workflowModuleRelation.setPid(procInstId);
			workflowModuleRelation.setMtype(Case4InfractBusiness.class.getSimpleName());
			this.workflowModuleRelationService.save(workflowModuleRelation);
			// 将状态更改为处理中
			cib.setStatus(CaseBase.STATUS_HANDLING);
			this.caseBusinessDao.save(cib);
			
			//记录发起的流程信息
			returnMap.put("moduleId", String.valueOf(id));
			returnMap.put("procInstId", procInstId);
			returnValue.add(returnMap);
		}
		
		return returnValue;
	}
	
	private Map<String,Object> returnParam(Case4InfractBusiness cib,Map<String,Object> variables){
		if(variables==null)return null;
		if(cib==null)return null;
		
		// 流转
		variables.put("goTo", true);
		variables.put("module_id",cib.getId());
		variables.put("module_type", Case4InfractBusiness.class.getSimpleName());
		
		//违法行为
		variables.put("illegalSubject", cib.getSubject());
		//违章地点
		variables.put("address", cib.getAddress());
		//违章日期
		variables.put("happenDate", DateUtils.formatCalendar2Second(cib.getHappenDate()));
		//违章类别
		variables.put("category", cib.getCategory());
		//案号
		variables.put("caseNo", cib.getCaseNo());
		//接案人
		variables.put("receiverName", cib.getReceiverName()==null?"":cib.getReceiverName());
		//自编号
		variables.put("code", cib.getCode()==null?"":cib.getCode());
		//扣件证号
		variables.put("confiscateCertNo", cib.getConfiscateCertNo()==null?"":cib.getConfiscateCertNo());
		//执法人
		variables.put("operator", cib.getOperator()==null?"":cib.getOperator());
		//执法机关
		variables.put("operateUnit", cib.getOperateUnit()==null?"":cib.getOperateUnit());
		//所属区县
		variables.put("area", cib.getArea()==null?"":cib.getArea());
		//拖车单位
		variables.put("pullUnit", cib.getPullUnit()==null?"":cib.getPullUnit());
		
		//来源
		variables.put("businessFrom",cib.getFrom()==null?"":cib.getFrom());
		
		//车ID
		variables.put("carId", cib.getCarId());
		//车号
		variables.put("carPlate", cib.getCarPlate());
		//车队
		if(cib.getMotorcadeId()!=null){
			variables.put("motorcadeId", cib.getMotorcadeId());
			variables.put("motorcadeName", cib.getMotorcadeName());
			Motorcade m = this.motorcadeService.load(cib.getMotorcadeId());
			//分公司
			variables.put("filialeId", m.getUnit().getId());
			variables.put("filiale", m.getUnit().getName());
		}
		
		//主题
		String subject=cib.getCarPlate()+"驾驶员";
		
		if(cib.getCategory()==Case4InfractBusiness.CATEGORY_BUSINESS){
			subject+="营运";
		}else if(cib.getCategory()==Case4InfractBusiness.CATEGORY_STATION){
			subject+="站场";
		}else{
			subject+="服务";
		}
		subject+="违章处理";
		
		variables.put("subject", subject);
		
		return variables;
	}
	
	//同步附件
	private void syscAttach(Case4InfractBusiness cib,String procInstId,String taskId){
		if(cib==null||procInstId==null||procInstId.length()==0)return;
		
		List<Attach> attachs=attachService.findByPtype(Case4InfractBusiness.ATTACH_TYPE, cib.getUid());
		if(attachs==null||attachs.size()==0)return;
	
		for(Attach attach:attachs){
			// 复制附件到流程附件位置中----开始---
			// 扩展名
			String extension = StringUtils.getFilenameExtension(attach.getPath());
			// 文件存储的相对路径（年月），避免超出目录内文件数的限制
			String subFolder = DateUtils.formatCalendar(Calendar.getInstance(), "yyyyMM");
			// 上传文件存储的绝对路径
			String appRealDir = Attach.DATA_REAL_PATH + File.separator+ FlowAttach.DATA_SUB_PATH;
			// 所保存文件所在的目录的绝对路径名
			String realFileDir = appRealDir + File.separator + subFolder;
			// 不含路径的文件名
			String fileName = DateUtils.formatCalendar(Calendar.getInstance(), "yyyyMMddHHmmssSSSS")+ "." + extension;
			// 所保存文件的绝对路径名
			String realFilePath = realFileDir + File.separator + fileName;
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
			
			// 附件路径
			String path= Attach.DATA_REAL_PATH + File.separator +attach.getPath();
			
			// 从附件目录下的指定文件复制到attachment目录下
			try {
				FileCopyUtils.copy(new FileInputStream(new File(path)),
						new FileOutputStream(realFilePath));
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
			
			// 复制附件到流程附件位置中----结束---
			
			// 插入流程附件记录信息
			FlowAttach flowAttach = new FlowAttach();
			flowAttach.setUid(idGeneratorService.next(FlowAttach.ATTACH_TYPE));
			flowAttach.setType(FlowAttach.TYPE_ATTACHMENT); // 类型：1-附件，2-意见
			flowAttach.setPid(procInstId); // 流程id
			flowAttach.setPath(subFolder+ File.separator +fileName); // 附件路径，物理文件保存的相对路径
			flowAttach.setExt(extension); // 扩展名
			flowAttach.setSubject(attach.getSubject()); // 标题
			flowAttach.setSize(attach.getSize());
			flowAttach.setFormatted(false);// 附件是否需要格式化
			
			if(taskId==null){
				flowAttach.setCommon(true); //公共附件
			}else{
				flowAttach.setCommon(false); //任务附件
				flowAttach.setTid(taskId);
			}
			
			// 创建人,最后修改人信息
			SystemContext context = SystemContextHolder.get();
			flowAttach.setAuthor(context.getUserHistory());
			flowAttach.setModifier(context.getUserHistory());
			flowAttach.setFileDate(Calendar.getInstance());
			flowAttach.setModifiedDate(Calendar.getInstance());
			this.flowAttachService.save(flowAttach);
		}
		
	}
	
	public void updateCaseBusinessInfo4Flow(Long id,
			Map<String, Object> attributes) {
		this.caseBusinessDao.update4Flow(id, attributes);
	}
}